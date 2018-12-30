import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import 'base/database_list_event.dart';
import 'base/enum.dart';
import 'base/keyed_list_item.dart';
import 'base/model.dart';
import 'deck.dart';

enum AccessType {
  read,
  write,
  owner,
}

class DeckAccessModel implements KeyedListItem, Model {
  String uid;
  // TODO(dotdoom): remove DeckModel from here.
  DeckModel deck;
  AccessType access;
  String email;

  String _displayName;
  String _photoUrl;

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

  DeckAccessModel({@required this.deck, this.uid, this.access, this.email})
      : assert(deck != null) {
    uid ??= deck.uid;
  }

  DeckAccessModel.fromSnapshot(this.uid, snapshotValue, this.deck) {
    _parseSnapshot(snapshotValue);
  }

  static Stream<DatabaseListEvent<DeckAccessModel>> getDeckAccesses(
          DeckModel deck) =>
      fullThenChildEventsStream(
          FirebaseDatabase.instance
              .reference()
              .child('deck_access')
              .child(deck.key)
              .orderByKey(),
          (key, value) => DeckAccessModel.fromSnapshot(key, value, deck));

  static Future<DeckAccessModel> fetch(DeckModel deck, [String uid]) async {
    var access = DeckAccessModel(deck: deck);
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
  Map<String, dynamic> toMap(bool isNew) => {
        // Do not save displayName and photoUrl because these are populated by
        // Cloud functions.
        'deck_access/${deck.key}/$key/access': Enum.asString(access),
        'deck_access/${deck.key}/$key/email': email,
        // Update "access" field of the Deck, too.
        'decks/$key/${deck.key}/access': Enum.asString(access),
      };
}
