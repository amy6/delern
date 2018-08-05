import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import 'base/enum.dart';
import 'base/keyed_list.dart';
import 'base/model.dart';
import 'base/observable_list.dart';

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

  Deck({
    @required this.uid,
    this.name,
    this.markdown: false,
    this.type: DeckType.basic,
    this.accepted: true,
    this.lastSyncAt,
    this.category,
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
        DateTime.fromMillisecondsSinceEpoch(snapshotValue['lastSyncAt']);
    category = snapshotValue['category'];
  }

  static Stream<KeyedListEvent<Deck>> getDecks(String uid) async* {
    FirebaseDatabase.instance
        .reference()
        .child('decks')
        .child(uid)
        .keepSynced(true);
    yield KeyedListEvent(
        eventType: ListEventType.setAll,
        fullListValueForSet: ((await FirebaseDatabase.instance
                        .reference()
                        .child('decks')
                        .child(uid)
                        .orderByKey()
                        .onValue
                        .first)
                    .snapshot
                    .value as Map ??
                {})
            .entries
            .map((item) {
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

  Stream<int> getNumberOfCardsToLearn([int limit = 201]) =>
      FirebaseDatabase.instance
          .reference()
          .child('learning')
          .child(uid)
          .child(key)
          .orderByChild('repeatAt')
          .endAt(DateTime.now().toUtc().millisecondsSinceEpoch)
          .limitToFirst(limit)
          .onValue
          .map((evt) => (evt.snapshot.value as Map)?.length ?? 0);

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

  @override
  String get rootPath => 'decks/$uid';
}
