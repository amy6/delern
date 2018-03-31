import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';

import 'keyed_event_list_mixin.dart';
import 'child_events_stream.dart';

class Deck implements KeyedListItem {
  final String key;
  final String name;
  final String uid;

  Deck.fromSnapshot(this.key, dynamic snapshotValue, this.uid)
      : name = snapshotValue['name'];

  static Stream<Iterable<Deck>> getDecks(String uid) => FirebaseDatabase
      .instance
      .reference()
      .child('decks')
      .child(uid)
      .orderByKey()
      .onValue
      .map((evt) => (evt.snapshot.value as Map)
          .entries
          .map((entry) => new Deck.fromSnapshot(entry.key, entry.value, uid)));

  // TODO(dotdoom): merge both getDecks and getDecksEvents into a single call?
  static Stream<KeyedListEvent<Deck>> getDecksEvents(
          String uid) =>
      childEventsStream(
          FirebaseDatabase.instance
              .reference()
              .child('decks')
              .child(uid)
              .orderByKey(),
          (snapshot) =>
              new Deck.fromSnapshot(snapshot.key, snapshot.value, uid));

  Stream<String> getAccess() => FirebaseDatabase.instance
      .reference()
      .child('deck_access')
      .child(key)
      .child(uid)
      .child('access')
      .onValue
      .map((evt) => evt.snapshot.value as String);

  Stream<int> getNumberOfCardsToLearn([int limit = 201]) =>
      FirebaseDatabase.instance
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
