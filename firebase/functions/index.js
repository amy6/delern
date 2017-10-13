'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

var limits = {
	minRequestLength: 4,
	maxResponseItems: 3,
};

var userMatches = function(requestValueLC, user) {
	return (user.name.toLowerCase().indexOf(requestValueLC) > -1) ||
		(user.email.toLowerCase().indexOf(requestValueLC) > -1);
};

exports.userLookup = functions.https.onRequest((req, res) => {
	// TODO(dotdoom): check auth, e.g.:
	// https://github.com/firebase/functions-samples/tree/master/authorized-https-endpoint

	if (!req.query.q) {
		console.log('No "q" parameter');
		res.send([]);
		return;
	}

	var requestValue = req.query.q.toLowerCase();
	if (requestValue.length < limits.minRequestLength) {
		console.log('Requested match length', requestValue.length,
			'is too short');
		res.send([]);
		return;
	}

	admin.database().ref('/users').once('value').then(function(snapshot) {
		var allUsers = snapshot.val(),
			matchingUsers = [];
		for (var uid in allUsers) {
			var user = allUsers[uid];
			if (userMatches(requestValue, user)) {
				matchingUsers.push({
					uid: uid,
					name: user.name,
					photoUrl: user.photoUrl,
				});
			}
		}
		console.log('Gathered', matchingUsers.length, 'results');
		res.send(matchingUsers.slice(0, limits.maxResponseItems));
	});
});
