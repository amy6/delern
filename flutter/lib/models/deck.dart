import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import 'base/enum.dart';
import 'base/keyed_list.dart';
import 'base/model.dart';
import 'base/observable_list.dart';
import 'deck_access.dart';

enum DeckType { basic, german, swiss }

class Deck implements KeyedListItem, Model {
  final String uid;
  String key;

  String name;
  bool markdown;
  DeckType type;
  bool accepted;
  DateTime lastSyncAt;
  String category;
  AccessType access;

  Deck({
    @required this.uid,
    this.name,
    this.markdown = false,
    this.type = DeckType.basic,
    this.accepted = true,
    this.lastSyncAt,
    this.category,
    this.access,
  }) : assert(uid != null) {
    lastSyncAt ??= DateTime.fromMillisecondsSinceEpoch(0);
  }

  Deck.fromSnapshot(this.key, snapshotValue, this.uid) {
    _parseSnapshot(snapshotValue);
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

  static Stream<KeyedListEvent<Deck>> getDecks(String uid) async* {
    FirebaseDatabase.instance
        .reference()
        .child('decks')
        .child(uid)
        .keepSynced(true);
    Map initialValue = (await FirebaseDatabase.instance
                .reference()
                .child('decks')
                .child(uid)
                .orderByKey()
                .onValue
                .first)
            .snapshot
            .value ??
        {};
    yield KeyedListEvent(
        eventType: ListEventType.setAll,
        fullListValueForSet: initialValue.entries.map((item) {
          _keepDeckSynced(uid, item.key);
          return Deck.fromSnapshot(item.key, item.value, uid);
        }));
    yield* childEventsStream(
        FirebaseDatabase.instance
            .reference()
            .child('decks')
            .child(uid)
            .orderByKey(), (snapshot) {
      _keepDeckSynced(uid, snapshot.key);
      return Deck.fromSnapshot(snapshot.key, snapshot.value, uid);
    });
  }

  static void _keepDeckSynced(String uid, String deckId) {
    // Install a background listener on ScheduledCard and Card. The listener
    // is cancelled automatically when the deck is deleted or un-shared because
    // of the security rules.

    FirebaseDatabase.instance
        .reference()
        .child('learning')
        .child(uid)
        .child(deckId)
        .keepSynced(true);
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

  Stream<int> getNumberOfCardsToLearn(int limit) => FirebaseDatabase.instance
          .reference()
          .child('learning')
          .child(uid)
          .child(key)
          .orderByChild('repeatAt')
          .endAt(DateTime.now().toUtc().millisecondsSinceEpoch)
          .limitToFirst(limit)
          .onValue
          .map((evt) {
        Map allValues = evt.snapshot.value;
        return allValues?.length ?? 0;
      });

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

  @override
  String get rootPath => 'decks/$uid';
}

class DeckModel implements Model {
  String uid;
  String key;
  String name;
  bool markdown;
  DeckType type;
  bool accepted;
  DateTime lastSyncAt;
  String category;

  DeckModel();

  // We expect this to be called often and optimize for performance.
  DeckModel.copyFrom(DeckModel other)
      : uid = other.uid,
        key = other.key,
        name = other.name,
        markdown = other.markdown,
        type = other.type,
        accepted = other.accepted,
        lastSyncAt = other.lastSyncAt,
        category = other.category;

  DeckModel.copyFromLegacy(Deck legacy)
      : uid = legacy.uid,
        key = legacy.key,
        name = legacy.name,
        markdown = legacy.markdown,
        type = legacy.type,
        accepted = legacy.accepted,
        lastSyncAt = legacy.lastSyncAt,
        category = legacy.category;

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
}
