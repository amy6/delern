'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

var delern = {
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
      repeatAt: (new Date()).getTime(),
    };
  }
};

exports.userLookup = functions.https.onRequest((req, res) => {
  // TODO(dotdoom): check auth, e.g.:
  // https://github.com/firebase/functions-samples/tree/master/authorized-https-endpoint

  if (!req.query.q) {
    return res.status(400).end();
  }

  admin.auth().getUserByEmail(req.query.q)
    .then(user => {
      return res.send(user.uid);
    })
    .catch(error => {
      // TODO(dotdoom): getUserByPhoneNumber.
      return res.status(404).end();
    });
});

var legacyCreateSharedDeck = (deckId, userId) => {
  return admin.database().ref('deck_access').child(deckId).once('value')
    .then(deckAccessSnapshot => {
      let deckAccesses = deckAccessSnapshot.val();
      for (let sharedWithUserId in deckAccesses) {
        if (deckAccesses[sharedWithUserId].access === 'owner') {
          return sharedWithUserId;
        }
      }
    }).then(ownerUserId => {
      if (ownerUserId !== userId) {
        return admin.database().ref('decks').child(ownerUserId)
          .child(deckId).once('value');
      }
    }).then(deckSnapshot => {
      if (deckSnapshot) {
        let deck = deckSnapshot.val();
        deck.accepted = false;
        return admin.database().ref('decks').child(userId).child(deckId)
          .set(deck);
      }
    });
};

exports.deckShared = functions.database.ref('/deck_access/{deckId}/{userId}').onCreate(event => {
  let deckId = event.params.deckId,
    userId = event.params.userId;

  legacyCreateSharedDeck(deckId, userId)
    .then(() => {
      return admin.database().ref('cards').child(deckId).once('value');
    })
    .then(cardsSnapshot => {
      let scheduledCards = {};
      for (let cardId in cardsSnapshot.val()) {
        scheduledCards[cardId] = delern.createScheduledCardObject();
      }
      return admin.database().ref('learning').child(userId).child(deckId)
        .set(scheduledCards);
    });
});

exports.deckUnShared = functions.database.ref('/deck_access/{deckId}/{userId}').onDelete(event => {
  let deckId = event.params.deckId,
    userId = event.params.userId;

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
  })
});

exports.cardAdded = functions.database.ref('/cards/{deckId}/{cardId}').onCreate(event => {
  let deckId = event.params.deckId,
    cardId = event.params.cardId;
  return admin.database().ref('deck_access').child(deckId).once('value')
    .then(deckAccessSnapshot => {
      let learningUpdate = {};
      for (let userId in deckAccessSnapshot.val()) {
        learningUpdate[[userId, deckId, cardId].join('/')] =
          delern.createScheduledCardObject();
      }
      return admin.database().ref('learning').update(learningUpdate);
    })
});

exports.cardDeleted = functions.database.ref('/cards/{deckId}/{cardId}').onDelete(event => {
  let deckId = event.params.deckId,
    cardId = event.params.cardId;
  return admin.database().ref('deck_access').child(deckId).once('value')
    .then(deckAccessSnapshot => {
      let learningAndViewsUpdate = {};
      for (let userId in deckAccessSnapshot.val()) {
        learningAndViewsUpdate[
          ['learning', userId, deckId, cardId].join('/')
        ] = null;
        learningAndViewsUpdate[
          ['views', userId, deckId, cardId].join('/')
        ] = null;
      };
      return admin.database().ref('/').update(learningAndViewsUpdate);
    })
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
    if (!user.email && !user.phoneNumber) {
      // No email/phone => anonymous user!
      let daysStale =
        (now - new Date(user.metadata.lastSignInTime).getTime()) /
        1000 / 60 / 60 / 24;
      if (daysStale > 1) {
        console.log('Deleting stale (', daysStale,
          'days) Anonymous user', user.uid);
        return admin.auth().deleteUser(user.uid);
      }
    }
  }).then(() => {
    return admin.database().ref('deck_access').once('value')
      .then(deckAccessSnapshot => {
        let deckAccesses = deckAccessSnapshot.val(),
          deckAccessUpdate = {};
        for (let deckId in deckAccesses) {
          let deckAccess = deckAccesses[deckId];
          for (let userId in deckAccess) {
            if (!deckAccess[userId].access) {
              deckAccessUpdate[[deckId, userId].join('/')] = {
                access: deckAccess[userId],
              };
            }
          }
        }
        return admin.database().ref('deck_access').update(deckAccessUpdate);
      });
  }).then(() => {
    let usersRef = admin.database().ref('users');
    return usersRef.once('value').then((usersSnapshot) => {
      let promises = [];
      for (let uid in usersSnapshot.val()) {
        promises.push(admin.auth().getUser(uid)
          .catch((e) => {
            if (e.errorInfo.code === 'auth/user-not-found') {
              console.log('Deleting orphaned /users entry', uid);
              return usersRef.child(uid).set(null);
            }
          }));
      }
      return Promise.all(promises);
    });
  }).then(() => {
    res.end();
  });
});
