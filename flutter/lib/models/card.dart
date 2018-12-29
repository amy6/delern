import 'dart:async';
import 'dart:core';

import 'package:delern_flutter/models/deck.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import 'base/database_list_event.dart';
import 'base/model.dart';

class CardModel implements Model {
  String deckKey;
  String key;
  String front;
  String back;
  DateTime createdAt;

  // Card always should belong to a deck. Even if card is empty
  CardModel({@required this.deckKey}) : assert(deckKey != null);

  // We expect this to be called often and optimize for performance.
  CardModel.copyFrom(CardModel other)
      : deckKey = other.deckKey,
        key = other.key,
        front = other.front,
        back = other.back,
        createdAt = other.createdAt;

  CardModel._fromSnapshot(this.deckKey, this.key, snapshotValue) {
    if (snapshotValue == null) {
      // Assume the card doesn't exist anymore.
      key = null;
      return;
    }
    front = snapshotValue['front'];
    back = snapshotValue['back'];
    createdAt = snapshotValue['createdAt'] == null
        ? null
        : DateTime.fromMillisecondsSinceEpoch(snapshotValue['createdAt']);
  }

  @override
  String get rootPath => 'cards/$deckKey';

  @override
  Map<String, dynamic> toMap(bool isNew) {
    var map = <String, dynamic>{
      'cards/$deckKey/$key/front': front,
      'cards/$deckKey/$key/back': back
    };
    if (isNew) {
      // Important note: we ask server to fill in the timestamp, but we do not
      // update it in our object immediately. Something trivial like
      // 'await get(...).first' would work most of the time. But when offline,
      // Firebase "lies" to the application, replacing ServerValue.TIMESTAMP
      // with phone's time, although later it saves to the server correctly.
      // For this reason, we should never *update* createdAt because we risk
      // changing it (see the note above), in which case Firebase Database will
      // reject the update.
      map['cards/$deckKey/$key/createdAt'] = ServerValue.timestamp;
    }
    return map;
  }

  static Stream<CardModel> get(
          {@required String deckKey, @required String key}) =>
      FirebaseDatabase.instance
          .reference()
          .child('cards')
          .child(deckKey)
          .child(key)
          .onValue
          .map((evt) =>
              CardModel._fromSnapshot(deckKey, key, evt.snapshot.value));

  static Stream<DatabaseListEvent<CardModel>> getCards(String deckKey) =>
      fullThenChildEventsStream(
          FirebaseDatabase.instance
              .reference()
              .child('cards')
              .child(deckKey)
              .orderByKey(),
          (key, value) => CardModel._fromSnapshot(deckKey, key, value));

  static Future<CardModel> fetch(DeckModel deck, String cardId) async {
    var card = CardModel(deckKey: deck.key)..key = cardId;
    await card.updates.first;
    return card;
  }

  Stream<void> get updates => FirebaseDatabase.instance
      .reference()
      .child('cards')
      .child(deckKey)
      .child(key)
      .onValue
      .map((event) => _parseSnapshot(event.snapshot.value));

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
        : DateTime.fromMillisecondsSinceEpoch(snapshotValue['createdAt']);
  }
}
