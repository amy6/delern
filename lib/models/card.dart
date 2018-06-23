import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';

import 'base/keyed_list.dart';
import 'base/observable_list.dart';

class Card implements KeyedListItem {
  String key;
  String front;
  String back;
  DateTime createdAt;
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

  // TODO(dotdoom): replace deckId with Deck model.
  Future<void> save([String uid]) {
    var data = new Map<String, dynamic>();

    if (key == null) {
      assert(uid != null, 'User ID null when creating a card');

      key = FirebaseDatabase.instance
          .reference()
          .child('cards')
          .child(deckId)
          .push()
          .key;

      data['learning/$uid/$deckId/$key'] = {
        'level': 'L0',
        'repeatAt': 0,
      };

      // Important note: we ask server to fill in the timestamp, but we do not
      // update it in our object. Something trivial like 'await updates.first'
      // would work most of the time. But when offline, Firebase "lies" to the
      // application, replacing ServerValue.TIMESTAMP with phone's time,
      // although later it saves to the server correctly.
      data['cards/$deckId/$key/createdAt'] = ServerValue.timestamp;
    }

    // We should never *update* createdAt because we risk changing it (see the
    // note above), in which case Firebase Database will reject the update.
    data['cards/$deckId/$key/front'] = front;
    data['cards/$deckId/$key/back'] = back;
    return FirebaseDatabase.instance.reference().update(data);
  }

  Stream<void> get updates => FirebaseDatabase.instance
      .reference()
      .child('cards')
      .child(deckId)
      .child(key)
      .onValue
      .map((event) => _parseSnapshot(event.snapshot.value));

  Future<void> delete(String uid) {
    var data = new Map<String, dynamic>();

    data['learning/$uid/$deckId/$key'] = null;
    data['cards/$deckId/$key'] = null;
    data['views/$uid/$deckId/$key'] = null;
    return FirebaseDatabase.instance.reference().update(data);
  }
}
