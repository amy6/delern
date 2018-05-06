import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';

import 'base/keyed_list.dart';
import 'base/observable_list.dart';

class Card implements KeyedListItem {
  String key;
  String front;
  String back;
  final String deckId;

  Card(this.deckId, {this.front, this.back});

  Card.fromSnapshot(this.key, dynamic snapshotValue, this.deckId)
      : front = snapshotValue['front'],
        back = snapshotValue['back'];

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
                .value as Map)
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

  Future<void> save() {
    if (key == null) {
      key = FirebaseDatabase.instance
          .reference()
          .child('cards')
          .child(deckId)
          .push()
          .key;
    }

    return FirebaseDatabase.instance
        .reference()
        .child('cards')
        .child(deckId)
        .child(key)
        .set(_toMap());
  }

  Map<String, dynamic> _toMap() => {
        'front': front,
        'back': back,
      };
}
