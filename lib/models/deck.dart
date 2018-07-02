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
  // TODO(dotdoom): remove uid (Deck object doesn't need it for DB operations).
  final String uid;
  String key;

  String name;
  bool markdown;
  DeckType type;
  bool accepted;
  DateTime lastSyncAt;
  String category;

  Deck(
    this.uid, {
    @required this.name,
    this.markdown: false,
    this.type: DeckType.basic,
    this.accepted: true,
    this.lastSyncAt,
    this.category,
  }) {
    lastSyncAt ??= new DateTime.fromMillisecondsSinceEpoch(0);
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
        new DateTime.fromMillisecondsSinceEpoch(snapshotValue['lastSyncAt']);
    category = snapshotValue['category'];
  }

  static Stream<KeyedListEvent<Deck>> getDecks(String uid) async* {
    yield new KeyedListEvent(
        eventType: ListEventType.set,
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
            .map((item) => new Deck.fromSnapshot(item.key, item.value, uid)));
    yield* childEventsStream(
        FirebaseDatabase.instance
            .reference()
            .child('decks')
            .child(uid)
            .orderByKey(),
        (snapshot) => new Deck.fromSnapshot(snapshot.key, snapshot.value, uid));
  }

  Stream<void> get updates => FirebaseDatabase.instance
      .reference()
      .child('decks')
      .child(uid)
      .child(key)
      .onValue
      .map((event) => _parseSnapshot(event.snapshot.value));

  Stream<AccessType> getAccess() => FirebaseDatabase.instance
      .reference()
      .child('deck_access')
      .child(key)
      .child(uid)
      .child('access')
      .onValue
      .map((evt) => Enum.fromString(evt.snapshot.value, AccessType.values));

  Stream<int> getNumberOfCardsToLearn([int limit = 201]) =>
      FirebaseDatabase.instance
          .reference()
          .child('learning')
          .child(uid)
          .child(key)
          .orderByChild('repeatAt')
          .endAt(new DateTime.now().toUtc().millisecondsSinceEpoch)
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
