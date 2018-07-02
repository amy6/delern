import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';

import 'base/enum.dart';
import 'base/keyed_list.dart';
import 'base/model.dart';
import 'base/observable_list.dart';
import 'deck.dart';
import 'user.dart';

enum AccessType {
  read,
  write,
  owner,
}

class DeckAccess implements KeyedListItem, Model {
  // TODO(dotdoom): relay this to User model associated with this object.
  String key;
  Deck deck;
  AccessType access;

  DeckAccess.fromSnapshot(this.key, dynamic snapshotValue, this.deck) {
    _parseSnapshot(snapshotValue);
  }

  DeckAccess(this.deck, {this.access});

  static Stream<KeyedListEvent<DeckAccess>> getDeckAccesses(Deck deck) async* {
    yield new KeyedListEvent(
        eventType: ListEventType.set,
        fullListValueForSet: ((await FirebaseDatabase.instance
                    .reference()
                    .child('deck_access')
                    .child(deck.key)
                    .orderByKey()
                    .onValue
                    .first)
                .snapshot
                .value as Map)
            .entries
            .map((item) =>
                new DeckAccess.fromSnapshot(item.key, item.value, deck)));
    yield* childEventsStream(
        FirebaseDatabase.instance
            .reference()
            .child('deck_access')
            .child(deck.key)
            .orderByKey(),
        (snapshot) =>
            new DeckAccess.fromSnapshot(snapshot.key, snapshot.value, deck));
  }

  Stream<User> getUser() => FirebaseDatabase.instance
      .reference()
      .child('users')
      .child(key)
      .onValue
      .map((evt) => User.fromSnapshot(evt.snapshot.key, evt.snapshot.value));

  static Future<DeckAccess> fetch(Deck deck) async {
    var access = DeckAccess(deck)..key = deck.uid;
    await access.updates.first;
    return access;
  }

  void _parseSnapshot(snapshotValue) {
    // TODO(dotdoom): snapshotValue can be null.
    access = Enum.fromString(snapshotValue['access'], AccessType.values);
  }

  Stream<void> get updates => FirebaseDatabase.instance
      .reference()
      .child('deck_access')
      .child(deck.key)
      .child(key)
      .onValue
      .map((evt) => _parseSnapshot(evt.snapshot.value));

  @override
  String get rootPath => 'deck_access/${deck.key}';

  @override
  Map<String, dynamic> toMap(bool isNew) => {
        'deck_access/${deck.key}/$key/access': Enum.asString(access),
      };
}
