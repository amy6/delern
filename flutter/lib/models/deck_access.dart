import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

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
  String uid;
  Deck deck;
  AccessType access;

  String get key => uid;
  set key(String newValue) {
    if (newValue != null) {
      throw UnsupportedError(
          'DeckAccess must always be bound to an existing user');
    }
    uid = null;
  }

  DeckAccess.fromSnapshot(this.uid, snapshotValue, this.deck) {
    _parseSnapshot(snapshotValue);
  }

  DeckAccess({@required this.deck, this.uid, this.access})
      : assert(deck != null) {
    uid ??= deck.uid;
  }

  static Stream<KeyedListEvent<DeckAccess>> getDeckAccesses(Deck deck) async* {
    Map initialValue = (await FirebaseDatabase.instance
                .reference()
                .child('deck_access')
                .child(deck.key)
                .orderByKey()
                .onValue
                .first)
            .snapshot
            .value ??
        {};
    yield KeyedListEvent(
        eventType: ListEventType.setAll,
        fullListValueForSet: initialValue.entries.map(
            (item) => DeckAccess.fromSnapshot(item.key, item.value, deck)));
    yield* childEventsStream(
        FirebaseDatabase.instance
            .reference()
            .child('deck_access')
            .child(deck.key)
            .orderByKey(),
        (snapshot) =>
            DeckAccess.fromSnapshot(snapshot.key, snapshot.value, deck));
  }

  Stream<User> getUser() => FirebaseDatabase.instance
      .reference()
      .child('users')
      .child(key)
      .onValue
      .map((evt) => User.fromSnapshot(evt.snapshot.key, evt.snapshot.value));

  static Future<DeckAccess> fetch(Deck deck, [String uid]) async {
    var access = DeckAccess(deck: deck);
    if (uid != null) {
      access.uid = uid;
    }
    await access.updates.first;
    return access;
  }

  void _parseSnapshot(snapshotValue) {
    if (snapshotValue == null) {
      // Assume the DeckAccess doesn't exist anymore.
      key = null;
      return;
    }
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
