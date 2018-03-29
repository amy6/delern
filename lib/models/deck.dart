import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';

class Deck {
  final String name;
  final String key;
  final String uid;

  Deck.fromSnapshot(this.key, dynamic snapshotValue, this.uid)
      : name = snapshotValue['name'];

  static Stream<Iterable<Deck>> getDecks(String uid) {
    return FirebaseDatabase.instance
        .reference()
        .child('decks')
        .child(uid)
        .onValue
        .map((evt) => (evt.snapshot.value as Map).entries.map(
            (entry) => new Deck.fromSnapshot(entry.key, entry.value, uid)));
  }

  Stream<String> getAccess() {
    return FirebaseDatabase.instance
        .reference()
        .child('deck_access')
        .child(key)
        .child(uid)
        .child('access')
        .onValue
        .map((evt) => evt.snapshot.value as String);
  }

  Stream<int> getNumberOfCardsToLearn([int limit = 201]) {
    return FirebaseDatabase.instance
        .reference()
        .child('learning')
        .child(uid)
        .child(key)
        .orderByChild('repeatAt')
        .endAt(new DateTime.now().millisecondsSinceEpoch)
        .limitToFirst(limit)
        .onValue
        .map((evt) => (evt.snapshot.value as Map)?.length ?? 0);
  }
}
