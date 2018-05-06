import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';

import 'base/keyed_list.dart';
import 'base/observable_list.dart';
import 'user.dart';

enum AccessType {
  read,
  write,
  owner,
}

class DeckAccess implements KeyedListItem {
  final String key;
  final String deckId;
  AccessType access;

  DeckAccess.fromSnapshot(this.key, dynamic snapshotValue, this.deckId)
      : access = stringToAccessType(snapshotValue['access']);

  static Stream<KeyedListEvent<DeckAccess>> getDeckAccesses(
      String deckId) async* {
    yield new KeyedListEvent(
        eventType: ListEventType.set,
        fullListValueForSet: ((await FirebaseDatabase.instance
                    .reference()
                    .child('deck_access')
                    .child(deckId)
                    .orderByKey()
                    .onValue
                    .first)
                .snapshot
                .value as Map)
            .entries
            .map((item) =>
                new DeckAccess.fromSnapshot(item.key, item.value, deckId)));
    yield* childEventsStream(
        FirebaseDatabase.instance
            .reference()
            .child('deck_access')
            .child(deckId)
            .orderByKey(),
        (snapshot) =>
            new DeckAccess.fromSnapshot(snapshot.key, snapshot.value, deckId));
  }

  Stream<User> getUser() => FirebaseDatabase.instance
      .reference()
      .child('users')
      .child(key)
      .onValue
      .map((evt) => User.fromSnapshot(evt.snapshot.key, evt.snapshot.value));

  static AccessType stringToAccessType(String value) =>
      AccessType.values.firstWhere(
          (accessType) => accessType.toString().split('.').last == value,
          orElse: () => null);

  static String accessTypeToString(AccessType value) =>
      value.toString().split('.').last;
}
