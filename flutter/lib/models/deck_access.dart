import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import 'base/enum.dart';
import 'base/events.dart';
import 'base/keyed_list_item.dart';
import 'base/model.dart';
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
  String email;

  String _displayName;
  String _photoUrl;

  /// Set to "true" to update redundant "access" field in /decks.
  // This is ugly and does not belong to model at all, but Firebase Database
  // client implementation verifies that paths do not intersect within a single
  // update. I.e.: when we save both Deck and DeckAccess, DeckAccess still tries
  // to update redundant "access" field inside Deck, and this is not allowed,
  // even when the value is the same.
  // TODO(dotdoom): think of a workaround.
  bool updateAccessFieldInDeck = false;

  String get key => uid;
  set key(String newValue) {
    if (newValue != null) {
      throw UnsupportedError(
          'DeckAccess must always be bound to an existing user');
    }
    uid = null;
  }

  String get displayName => _displayName;
  String get photoUrl => _photoUrl;

  DeckAccess({@required this.deck, this.uid, this.access, this.email})
      : assert(deck != null) {
    uid ??= deck.uid;
  }

  DeckAccess.fromSnapshot(this.uid, snapshotValue, this.deck) {
    _parseSnapshot(snapshotValue);
  }

  static Stream<DatabaseListEvent<DeckAccess>> getDeckAccesses(
      Deck deck) async* {
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
    yield DatabaseListEvent(
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
    _displayName = snapshotValue['displayName'];
    _photoUrl = snapshotValue['photoUrl'];
    email = snapshotValue['email'];
    access = Enum.fromString(snapshotValue['access'], AccessType.values);
  }

  Stream<void> get updates => FirebaseDatabase.instance
          .reference()
          .child('deck_access')
          .child(deck.key)
          .child(key)
          .onValue
          .map((evt) {
        // TODO(dotdoom): either do not set key=null in _parseSnapshot, or do
        //                this "weird trick" in every model.
        if (key == null) {
          this.uid = evt.snapshot.key;
        }
        _parseSnapshot(evt.snapshot.value);
      });

  @override
  String get rootPath => 'deck_access/${deck.key}';

  @override
  Map<String, dynamic> toMap(bool isNew) {
    final updates = {
      // Do not save displayName and photoUrl because these are populated by
      // Cloud functions.
      'deck_access/${deck.key}/$key/access': Enum.asString(access),
      'deck_access/${deck.key}/$key/email': email,
    };
    if (updateAccessFieldInDeck) {
      // Update "access" field of the Deck, too.
      updates['decks/$key/${deck.key}/access'] = Enum.asString(access);
    }
    return updates;
  }
}
