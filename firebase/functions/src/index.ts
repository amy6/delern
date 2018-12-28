import * as cors from 'cors';
import * as admin from 'firebase-admin';
import * as functions from 'firebase-functions';
import * as nodemailer from 'nodemailer';

admin.initializeApp();

let mailTransport = null;
if ('gmail' in functions.config()) {
    // TODO(dotdoom): change this to something like:
    //   nodemailer.createTransport(functions.config().email).
    const gmailConfig = functions.config().gmail;
    mailTransport = nodemailer.createTransport({
        service: 'gmail',
        auth: {
            user: gmailConfig.email,
            pass: gmailConfig.password,
        },
    });
}

const delern = {
    createScheduledCardObject: () => {
        return {
            level: 'L0',
            // TODO(dotdoom): figure out better repeatAt
            repeatAt: 0,
        };
    },
    createMissingScheduledCards: async (uid: string, deckKey: string) => {
        const scheduledCards = (await admin.database().ref('learning')
            .child(uid).child(deckKey).once('value')).val() || {};
        const cards = (await admin.database().ref('cards').child(deckKey)
            .once('value')).val() || {};

        const scheduledCardsUpdates = {};
        for (const cardKey in cards) {
            if (!(cardKey in scheduledCards)) {
                scheduledCardsUpdates[cardKey] =
                    delern.createScheduledCardObject();
            }
        }

        // TODO(dotdoom): we already do this on app side, not necessary.
        for (const cardKey in scheduledCards) {
            if (!(cardKey in cards)) {
                scheduledCardsUpdates[cardKey] = null;
            }
        }

        if (Object.keys(scheduledCardsUpdates).length !== 0) {
            console.error(Error('Database denormalized in deck ' + deckKey +
                ' for user ' + uid + ', fixing (see below for details)'));
            console.log(scheduledCardsUpdates);
        }

        await admin.database().ref('learning').child(uid).child(deckKey)
            .update(scheduledCards);
    },
    setScheduledCardForAllUsers: async (deckKey: string, cardKey: string,
        skipUid: string, scheduledCard: any) => {
        const deckAccesses = (await admin.database()
            .ref('deck_access').child(deckKey).once('value')).val();

        const learningUpdate = {};
        for (const sharedWithUid in deckAccesses) {
            if (sharedWithUid !== skipUid) {
                learningUpdate[`${sharedWithUid}/${deckKey}/${cardKey}`] =
                    scheduledCard;
            }
        }
        await admin.database().ref('learning').update(learningUpdate);
    },
    forEachUser: null,
};

export const userLookup = functions.https.onRequest((req, res) =>
    // https://firebase.google.com/docs/functions/http-events
    cors({ origin: true })(req, res, async () => {
        // TODO(dotdoom): check auth, e.g.:
        // https://github.com/firebase/functions-samples/tree/master/authorized-https-endpoint

        if (!req.query.q) {
            res.status(400).end();
            return;
        }

        try {
            res.send(
                (await admin.auth().getUserByEmail(req.query.q)).uid);
        } catch (error) {
            // TODO(dotdoom): getUserByPhoneNumber.
            res.status(404).end();
        }
    }));

export const deckShared = functions.database
    .ref('/deck_access/{deckKey}/{sharedWithUid}')
    .onCreate(async (data, context) => {
        if (data.val().access === 'owner') {
            console.log('Deck is being created (not shared), skipping');
            return;
        }

        const deckKey = context.params.deckKey;
        const sharedWithUser =
            await admin.auth().getUser(context.params.sharedWithUid);

        const scheduledCards = {};
        const cards = (await admin.database()
            .ref('cards').child(deckKey).once('value')).val();
        for (const cardKey in cards) {
            scheduledCards[cardKey] =
                delern.createScheduledCardObject();
        }
        await admin.database()
            .ref('learning').child(sharedWithUser.uid).child(deckKey)
            .set(scheduledCards);

        if (context.authType !== 'USER') {
            // If the deck is shared by admin, we do not send notifications.
            return;
        }

        const numberOfCards = Object.keys(scheduledCards).length;
        const actorUser = await admin.auth().getUser(context.auth.uid);
        const deckName = (await admin.database()
            .ref('decks').child(sharedWithUser.uid).child(deckKey).child('name')
            .once('value')).val();
        const mailOptions = {
            // Either "from" or "reply-to" will work with most servers/clients.
            from: {
                name: actorUser.displayName + ' via Delern',
                address: actorUser.email,
            },
            replyTo: actorUser.email,
            to: sharedWithUser.email,
            subject: actorUser.displayName + ' shared a Delern deck with you',
            text: 'Hello! ' + actorUser.displayName + ' has shared a Delern ' +
                'deck "' + deckName + '" (' + numberOfCards + ' cards) ' +
                'with you! Go to the Delern app on your device to check it out',
        };
        console.log('Sending notification email', mailOptions);
        try {
            await mailTransport.sendMail(mailOptions);
        } catch (e) {
            console.error('Cannot send email', e);
        }

        const fcmSnapshot = admin.database().ref('fcm')
            .child(sharedWithUser.uid);
        const fcmEntries = (await fcmSnapshot.once('value')).val() || {};
        const payload = {
            notification: {
                title: actorUser.displayName + ' shared a deck with you',
                body: actorUser.displayName + ' shared their deck "' +
                    deckName + '" (' + numberOfCards + ' cards) with you',
            },
            token: null,
        };

        const tokenUpdates = {};
        for (const fcmId in fcmEntries) {
            console.log('Notifying user ' + sharedWithUser.uid + ' on ' +
                fcmEntries[fcmId].name + ' about user ' + actorUser.uid +
                ' sharing a deck ' + deckName + ' (' + numberOfCards +
                ' cards)');
            payload.token = fcmId;
            try {
                console.log('Notified:', await admin.messaging().send(payload));
            } catch (e) {
                if (e.code === 'messaging/invalid-registration-token' ||
                    e.code === 'messaging/registration-token-not-registered') {
                    console.warn('Removing a token because of', e.code);
                    tokenUpdates[sharedWithUser.uid + '/' + fcmId] = null;
                } else {
                    console.error('Failed:', e);
                }
            }
        }
        await admin.database().ref('fcm').update(tokenUpdates);
    });

export const deckUnShared = functions.database
    .ref('/deck_access/{deckKey}/{uid}')
    .onDelete((data, context) => {
        const deckKey = context.params.deckKey;
        const uid = context.params.uid;

        return admin.database().ref('/').update({
            [`learning/${uid}/${deckKey}`]: null,
            [`views/${uid}/${deckKey}`]: null,
            [`decks/${uid}/${deckKey}`]: null,
        });
    });

export const cardAdded = functions.database.ref('/cards/{deckKey}/{cardKey}')
    .onCreate((data, context) =>
        delern.setScheduledCardForAllUsers(context.params.deckKey,
            // Don't update for the user creating this card - it is done by app.
            context.params.cardKey, context.auth.uid,
            delern.createScheduledCardObject()));

// TODO(dotdoom): deleting deck with too many cards may be bad. Debounce?
export const cardDeleted = functions.database.ref('/cards/{deckKey}/{cardKey}')
    .onDelete((data, context) =>
        delern.setScheduledCardForAllUsers(context.params.deckKey,
            context.params.cardKey, context.auth.uid, null));

delern.forEachUser = async (batchSize: number,
    callback: (user: admin.auth.UserRecord) => Promise<void>,
    pageToken?: string) => {
    const listUsersResult = await admin.auth().listUsers(batchSize,
        pageToken);

    await Promise.all(listUsersResult.users.map(callback));

    if (listUsersResult.pageToken) {
        await delern.forEachUser(batchSize, callback,
            listUsersResult.pageToken);
    }
};

export const databaseMaintenance = functions.https
    .onRequest(async (req, res) => {
        const deckAccesses = (await admin.database()
            .ref('deck_access').once('value')).val();
        const uidCache: { [uid: string]: admin.auth.UserRecord } = {};
        const userInformationUpdates = {};
        // TODO(dotdoom): this should be done by app.
        const deckAccessInsideDeckUpdates = {};
        const missingCardsOperations = [];
        for (const deckKey in deckAccesses) {
            const deckAccess = deckAccesses[deckKey];

            for (const uid in deckAccess) {
                missingCardsOperations.push(
                    delern.createMissingScheduledCards(uid, deckKey));

                if (!(uid in uidCache)) {
                    try {
                        uidCache[uid] = await admin.auth().getUser(uid);
                    } catch (e) {
                        console.error('Cannot find user ' + uid +
                            ' for deck ' + deckKey, e);
                        continue;
                    }
                }
                const user = uidCache[uid];
                userInformationUpdates[`${deckKey}/${uid}/displayName`] =
                    user.displayName || null;
                userInformationUpdates[`${deckKey}/${uid}/photoUrl`] =
                    user.photoURL || null;
                userInformationUpdates[`${deckKey}/${uid}/email`] =
                    user.email || null;
                deckAccessInsideDeckUpdates[`${uid}/${deckKey}/access`] =
                    deckAccess[uid].access;
            }
        }
        await Promise.all(missingCardsOperations);
        await admin.database().ref('deck_access')
            .update(userInformationUpdates);
        await admin.database().ref('decks')
            .update(deckAccessInsideDeckUpdates);

        res.end();
    });
