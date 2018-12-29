import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import 'base/database_list_event.dart';
import 'base/enum.dart';
import 'base/model.dart';
import 'deck_access.dart';

enum DeckType { basic, german, swiss }

class DeckModel implements Model {
  String uid;
  String key;
  String name;
  var markdown = false;
  var type = DeckType.basic;
  var accepted = true;
  DateTime lastSyncAt;
  String category;
  AccessType access;

  DeckModel(this.uid) : assert(uid != null) {
    lastSyncAt = DateTime.fromMillisecondsSinceEpoch(0);
  }

  // We expect this to be called often and optimize for performance.
  DeckModel.copyFrom(DeckModel other)
      : uid = other.uid,
        key = other.key,
        name = other.name,
        markdown = other.markdown,
        type = other.type,
        accepted = other.accepted,
        lastSyncAt = other.lastSyncAt,
        category = other.category,
        access = other.access;

  DeckModel._fromSnapshot(this.uid, this.key, snapshotValue) {
    if (snapshotValue == null) {
      // Assume the deck doesn't exist anymore.
      key = null;
      return;
    }
    name = snapshotValue['name'];
    markdown = snapshotValue['markdown'] ?? false;
    type = Enum.fromString(
        snapshotValue['deckType']?.toString()?.toLowerCase(), DeckType.values);
    accepted = snapshotValue['accepted'] ?? false;
    lastSyncAt =
        DateTime.fromMillisecondsSinceEpoch(snapshotValue['lastSyncAt'] ?? 0);
    category = snapshotValue['category'];
    access = Enum.fromString(snapshotValue['access'], AccessType.values);
  }

  void _parseSnapshot(snapshotValue) {
    if (snapshotValue == null) {
      // Assume the deck doesn't exist anymore.
      key = null;
      return;
    }
    name = snapshotValue['name'];
    markdown = snapshotValue['markdown'] ?? false;
    type = Enum.fromString(
        snapshotValue['deckType']?.toString()?.toLowerCase(), DeckType.values);
    accepted = snapshotValue['accepted'] ?? false;
    lastSyncAt =
        DateTime.fromMillisecondsSinceEpoch(snapshotValue['lastSyncAt'] ?? 0);
    category = snapshotValue['category'];
    access = Enum.fromString(snapshotValue['access'], AccessType.values);
  }

  @override
  String get rootPath => 'decks/$uid';

  Map<String, dynamic> toMap(bool isNew) => {
        'decks/$uid/$key': {
          'name': name,
          'markdown': markdown,
          'deckType': Enum.asString(type)?.toUpperCase(),
          'accepted': accepted,
          'lastSyncAt': lastSyncAt.toUtc().millisecondsSinceEpoch,
          'category': category,
          'access': Enum.asString(access),
        }
      };

  static Stream<DeckModel> get({@required String uid, @required String key}) =>
      FirebaseDatabase.instance
          .reference()
          .child('decks')
          .child(uid)
          .child(key)
          .onValue
          .map((evt) => DeckModel._fromSnapshot(uid, key, evt.snapshot));

  static Stream<DatabaseListEvent<DeckModel>> getDecks(String uid) {
    FirebaseDatabase.instance
        .reference()
        .child('decks')
        .child(uid)
        .keepSynced(true);
    return fullThenChildEventsStream(
        FirebaseDatabase.instance
            .reference()
            .child('decks')
            .child(uid)
            .orderByKey(), (key, value) {
      _keepDeckSynced(uid, key);
      return DeckModel._fromSnapshot(uid, key, value);
    });
  }

  static void _keepDeckSynced(String uid, String deckId) {
    // Install a background listener on ScheduledCard and Card. The listener
    // is cancelled automatically when the deck is deleted or un-shared because
    // of the security rules.

    FirebaseDatabase.instance
        .reference()
        .child('cards')
        .child(deckId)
        .keepSynced(true);
  }

  Stream<void> get updates => FirebaseDatabase.instance
      .reference()
      .child('decks')
      .child(uid)
      .child(key)
      .onValue
      .map((event) => _parseSnapshot(event.snapshot.value));
}
