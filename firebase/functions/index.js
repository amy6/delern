'use strict';

const functions = require('firebase-functions');
// https://firebase.google.com/docs/functions/http-events
const cors = require('cors')({
  origin: true,
});

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const nodemailer = require('nodemailer');
const mailEmail = functions.config().gmail.email;
const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: mailEmail,
    pass: functions.config().gmail.password,
  },
});

const delern = {
  deleteDeck: function(deckId) {
    Promise.resolve(true)
      .then(() => {
        return admin.database().ref('deck_access').child(deckId)
          .set(null);
      })
      .then(() => {});
  },
  deleteUser: function(uid) {
    Promise.resolve(true)
      .then(() => {
        return admin.auth().deleteUser(uid);
      })
      .then(() => {});
  },
  createScheduledCardObject: function() {
    return {
      level: 'L0',
      // TODO(dotdoom): figure out better repeatAt
      repeatAt: 0,
    };
  },
  createMissingScheduledCards: function(uid, deckId) {
    return admin.database().ref('learning').child(uid).child(deckId)
      .once('value')
      .then((scheduledCards) => {
        scheduledCards = scheduledCards.val();
        return admin.database().ref('cards').child(deckId)
          .once('value').then((cards) => {
            cards = cards.val();
            if (!cards) {
              cards = {};
            }
            if (!scheduledCards) {
              scheduledCards = {};
            }

            let anyUpdate = false;

            Object.keys(cards).forEach((cardId) => {
              if (!scheduledCards[cardId]) {
                scheduledCards[cardId] = delern.createScheduledCardObject();
                anyUpdate = true;
                console.log('Creating missing scheduled card ' + cardId +
                  ' for ' + uid);
              };
            });

            Object.keys(scheduledCards).forEach((cardId) => {
              if (!cards[cardId]) {
                delete scheduledCards[cardId];
                anyUpdate = true;
                console.log('Deleting extra scheduled card ' + cardId +
                  ' for ' + uid);
              };
            });

            if (anyUpdate) {
              // Throw a real Error object to notify via StackDriver.
              console.error(new Error('Database denormalized in deck ' +
                deckId + ' for ' + uid + ', fixing (see log for details)'));
              return admin.database().ref('learning').child(uid).child(deckId)
                .set(scheduledCards);
            }

            return Promise.resolve();
          });
      });
  },
};

exports.userLookup = functions.https.onRequest((req, res) => {
  cors(req, res, () => {
    // TODO(dotdoom): check auth, e.g.:
    // https://github.com/firebase/functions-samples/tree/master/authorized-https-endpoint

    if (!req.query.q) {
      return res.status(400).end();
    }

    admin.auth().getUserByEmail(req.query.q)
      .then((user) => {
        return res.send(user.uid);
      })
      .catch((error) => {
        // TODO(dotdoom): getUserByPhoneNumber.
        return res.status(404).end();
      });
  });
});

exports.deckShared = functions.database.ref('/deck_access/{deckId}/{userId}').onCreate((event) => {
  if (event.data.val().access === 'owner') {
    console.log('Deck is being created (not shared), skipping');
    // Return some 'true' value per
    // https://cloud.google.com/functions/docs/writing/background#using_promises
    return true;
  }

  let deckId = event.params.deckId;
  let userId = event.params.userId;
  let user = null;
  let actorUserId = 'ADMIN';
  if (event.auth && event.auth.variable) {
    actorUserId = event.auth.variable.uid;
  }
  let numberOfCards = 0;
  let actorUser = null;
  let deckName = null;

  return admin.database().ref('decks').child(userId).child(deckId).child('name')
    .once('value')
    .then((createdDeckName) => {
      deckName = createdDeckName.val();
      return admin.database().ref('cards').child(deckId).once('value');
    })
    .then((cardsSnapshot) => {
      let scheduledCards = {};
      // Check that there are actually some cards.
      if (cardsSnapshot.val()) {
        Object.keys(cardsSnapshot.val()).forEach((cardId) => {
          scheduledCards[cardId] = delern.createScheduledCardObject();
          numberOfCards++;
        });
        return admin.database().ref('learning').child(userId).child(deckId)
          .set(scheduledCards);
      }
    })
    .then(() => {
      return admin.auth().getUser(userId);
    })
    .then((userRecord) => {
      user = userRecord;
      return admin.database().ref('users').child(actorUserId).once('value');
    })
    .then((actorUserSnapshot) => {
      actorUser = actorUserSnapshot.val();

      if (user.email.endsWith('.example.com')) {
        // Do not send emails to test users (delivery fails anyway).
        return;
      }

      let mailOptions = {
        // TODO(dotdoom): <mailEmail>+<actorUser.name>@gmail.com (avoid filters)
        from: actorUser.name + ' via Delern <' + mailEmail + '>',
        to: user.email,
        subject: actorUser.name + ' shared a Delern deck with you',
        text: 'Hello! ' + actorUser.name + ' has shared a Delern deck "' +
          deckName + '" with you! Go to the Delern app on your phone to ' +
          'check it out',
      };

      console.log('Sending email', mailOptions);

      return mailTransport.sendMail(mailOptions).catch((error) => {
        console.error('Cannot send email', error);
      });
    })
    .then(() => {
      return admin.database().ref('fcm').child(userId).once('value');
    })
    .then((fcmSnapshot) => {
      let fcm = fcmSnapshot.val();
      Object.keys(fcm).forEach((fcmId) => {
        console.log('Notifying ' + userId + ' on ' + fcm[fcmId].name +
          ' about ' + actorUser.name + ' sharing a deck ' + deckName +
          ' with ' + numberOfCards + ' cards');
      });
      let payload = {
        notification: {
          title: 'A user has shared a deck with you',
          body: actorUser.name + ' shared deck "' + deckName + '" (' +
            numberOfCards + ' cards) with you',
          icon: actorUser.photoUrl || '',
        },
      };
      let tokens = Object.keys(fcm);
      return admin.messaging().sendToDevice(tokens, payload).then((response) => {
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          if (error) {
            // Cleanup the tokens which are not registered anymore.
            if (error.code === 'messaging/invalid-registration-token' ||
              error.code === 'messaging/registration-token-not-registered') {
              tokensToRemove.push(fcmSnapshot.ref.child(tokens[index]).remove());
            } else {
              console.error('Failure sending notification to', userId, 'at',
                fcm[tokens[index]].name, error);
            }
          }
        });
        return Promise.all(tokensToRemove);
      });
    });
});

exports.deckUnShared = functions.database.ref('/deck_access/{deckId}/{userId}')
  .onDelete((event) => {
    let deckId = event.params.deckId;
    let userId = event.params.userId;

    return admin.database().ref('/').update({
      [
        ['learning', userId, deckId].join('/')
      ]: null,
      [
        ['views', userId, deckId].join('/')
      ]: null,
      [
        ['decks', userId, deckId].join('/')
      ]: null,
    });
  });

exports.cardAdded = functions.database.ref('/cards/{deckId}/{cardId}').onCreate((event) => {
  let deckId = event.params.deckId;
  let cardId = event.params.cardId;
  return admin.database().ref('deck_access').child(deckId).once('value')
    .then((deckAccessSnapshot) => {
      let learningUpdate = {};
      Object.keys(deckAccessSnapshot.val()).forEach((userId) => {
        if (userId === event.auth.variable.uid) {
          console.log('Skipping learning creation for', userId,
            'as they are creating this card');
        } else {
          learningUpdate[[userId, deckId, cardId].join('/')] =
            delern.createScheduledCardObject();
        }
      });
      return admin.database().ref('learning').update(learningUpdate);
    });
});

exports.cardDeleted = functions.database.ref('/cards/{deckId}/{cardId}').onDelete((event) => {
  let deckId = event.params.deckId;
  let cardId = event.params.cardId;
  return admin.database().ref('deck_access').child(deckId).once('value')
    .then((deckAccessSnapshot) => {
      let learningAndViewsUpdate = {};
      // TODO(dotdoom): deleting deck with too many cards may be bad.
      if (deckAccessSnapshot.val()) {
        Object.keys(deckAccessSnapshot.val()).forEach((userId) => {
          learningAndViewsUpdate[
            ['learning', userId, deckId, cardId].join('/')
          ] = null;
          learningAndViewsUpdate[
            ['views', userId, deckId, cardId].join('/')
          ] = null;
        });
        return admin.database().ref('/').update(learningAndViewsUpdate);
      }
    });
});

delern.forEachUser = (batchSize, callback, nextPageToken) => {
  return admin.auth().listUsers(batchSize, nextPageToken)
    .then((listUsersResult) => {
      return Promise.all(listUsersResult.users.map(callback))
        .then(() => {
          if (listUsersResult.pageToken) {
            return delern.forEachUser(batchSize, callback,
              listUsersResult.pageToken);
          }
        });
    });
};

exports.databaseMaintenance = functions.https.onRequest((req, res) => {
  let now = new Date().getTime();
  delern.forEachUser(1000, (user) => {
    // Properties at https://firebase.google.com/docs/auth/admin/manage-users
    if ((!user.email && !user.phoneNumber) ||
      user.email.endsWith('.example.com')) {
      // No email/phone => anonymous user!
      let daysStale =
        (now - new Date(user.metadata.lastSignInTime).getTime()) /
        1000 / 60 / 60 / 24;
      if (daysStale > 14) {
        console.log('Deleting stale (', daysStale,
          'days) test user', user.uid);
        return new Promise((resolve) => setTimeout(resolve, 2000)).then(() =>
          admin.auth().deleteUser(user.uid));
      }
    }
  }).then(() => {
    return admin.database().ref('deck_access').once('value')
      .then((deckAccessSnapshot) => {
        let deckAccesses = deckAccessSnapshot.val();
        let deckAccessUpdate = {};
        let missingCards = Promise.resolve();
        Object.keys(deckAccesses).forEach((deckId) => {
          let deckAccess = deckAccesses[deckId];
          Object.keys(deckAccess).forEach((userId) => {
            missingCards.then(
              delern.createMissingScheduledCards(userId, deckId));
            if (!deckAccess[userId].access) {
              deckAccessUpdate[[deckId, userId].join('/')] = {
                access: deckAccess[userId],
              };
            }
          });
        });
        return missingCards.then(admin.database()
          .ref('deck_access').update(deckAccessUpdate));
      });
  }).then(() => {
    let usersRef = admin.database().ref('users');
    return usersRef.once('value').then((usersSnapshot) => {
      let promises = [];
      Object.keys(usersSnapshot.val()).forEach((uid) => {
        promises.push(admin.auth().getUser(uid)
          .catch((e) => {
            if (e.errorInfo.code === 'auth/user-not-found') {
              console.log('Deleting orphaned /users entry', uid);
              return usersRef.child(uid).set(null);
            }
          }));
      });
      return Promise.all(promises);
    });
  }).then(() => {
    res.end();
  });
});
