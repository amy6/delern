import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';

import 'keyed_list.dart';
import 'observable_list.dart';

class Card implements KeyedListItem {
  final String key;
  final String front;
  final String back;
  final String deckId;

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
}
