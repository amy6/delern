import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';

import 'base/keyed_list.dart';
import 'base/observable_list.dart';
import 'base/model.dart';

class Card implements KeyedListItem, Model {
  String key;
  String front;
  String back;
  DateTime createdAt;
  // TODO(dotdoom): replace deckId with Deck model, so that they can both
  //                be present in a single 'CREATE' transaction.
  final String deckId;

  Card(this.deckId, {this.front, this.back});

  Card.fromSnapshot(this.key, snapshotValue, this.deckId) {
    _parseSnapshot(snapshotValue);
  }

  void _parseSnapshot(snapshotValue) {
    if (snapshotValue == null) {
      // Assume the card doesn't exist anymore.
      key = null;
      return;
    }
    front = snapshotValue['front'];
    back = snapshotValue['back'];
    createdAt = snapshotValue['createdAt'] == null
        ? null
        : new DateTime.fromMillisecondsSinceEpoch(snapshotValue['createdAt']);
  }

  static Future<Card> fetch(String deckId, String cardId) async {
    var card = Card(deckId)..key = cardId;
    await card.updates.first;
    return card;
  }

  static Stream<KeyedListEvent<Card>> getCards(String deckId) async* {
    yield new KeyedListEvent(
        eventType: ListEventType.set,
        fullListValueForSet: ((await FirebaseDatabase.instance
                        .reference()
                        .child('cards')
                        .child(deckId)
                        .orderByKey()
                        .onValue
                        .first)
                    .snapshot
                    .value as Map ??
                {})
            .entries
            .map(
                (item) => new Card.fromSnapshot(item.key, item.value, deckId)));

    yield* childEventsStream(
        FirebaseDatabase.instance
            .reference()
            .child('cards')
            .child(deckId)
            .orderByKey(),
        (snapshot) =>
            new Card.fromSnapshot(snapshot.key, snapshot.value, deckId));
  }

  Map<String, dynamic> toMap(bool isNew) {
    var map = new Map<String, dynamic>()
      ..['cards/$deckId/$key/front'] = front
      ..['cards/$deckId/$key/back'] = back;
    if (isNew) {
      // Important note: we ask server to fill in the timestamp, but we do not
      // update it in our object immediately. Something trivial like
      // 'await updates.first' would work most of the time. But when offline,
      // Firebase "lies" to the application, replacing ServerValue.TIMESTAMP
      // with phone's time, although later it saves to the server correctly.
      // For this reason, we should never *update* createdAt because we risk
      // changing it (see the note above), in which case Firebase Database will
      // reject the update.
      map['cards/$deckId/$key/createdAt'] = ServerValue.timestamp;
    }
    return map;
  }

  Stream<void> get updates => FirebaseDatabase.instance
      .reference()
      .child('cards')
      .child(deckId)
      .child(key)
      .onValue
      .map((event) => _parseSnapshot(event.snapshot.value));

  @override
  String get rootPath => 'cards/$deckId';
}
