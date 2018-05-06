import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import 'base/keyed_list.dart';
import 'base/observable_list.dart';
import 'deck_access.dart';

enum DeckType { basic, german, swiss }

class Deck implements KeyedListItem {
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

  Deck.fromSnapshot(this.key, dynamic snapshotValue, this.uid)
      : name = snapshotValue['name'],
        markdown = snapshotValue['markdown'] ?? false,
        type = _stringToDeckType(snapshotValue['deckType']),
        accepted = snapshotValue['accepted'] ?? false,
        lastSyncAt = new DateTime.fromMillisecondsSinceEpoch(
            snapshotValue['lastSyncAt'],
            isUtc: true),
        category = snapshotValue['category'];

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
                .value as Map)
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

  Future<void> updateExisting() => FirebaseDatabase.instance
      .reference()
      .child('decks')
      .child(uid)
      .child(key)
      .set(_toMap());

  Future<void> saveAsNew() {
    key = FirebaseDatabase.instance
        .reference()
        .child('decks')
        .child(uid)
        .push()
        .key;

    return updateExisting().then((_) => FirebaseDatabase.instance
        .reference()
        .child('deck_access')
        .child(key)
        .child(uid)
        .set({'access': 'owner'}));
  }

  Stream<AccessType> getAccess() => FirebaseDatabase.instance
      .reference()
      .child('deck_access')
      .child(key)
      .child(uid)
      .child('access')
      .onValue
      .map((evt) => DeckAccess.stringToAccessType(evt.snapshot.value));

  static DeckType _stringToDeckType(String value) => DeckType.values.firstWhere(
      (deckType) => deckType.toString().split('.').last.toUpperCase() == value,
      orElse: () => DeckType.basic);

  static String _deckTypeToString(DeckType value) =>
      value.toString().split('.').last.toUpperCase();

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

  Map<String, dynamic> _toMap() => {
        'name': name,
        'markdown': markdown,
        'type': _deckTypeToString(type),
        'accepted': accepted,
        'lastSyncAt': lastSyncAt.toUtc().millisecondsSinceEpoch,
        'category': category,
      };
}
