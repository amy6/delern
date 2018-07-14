import 'dart:async';
import 'dart:core';
import 'dart:math';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import '../remote/error_reporting.dart';
import 'base/keyed_list.dart';
import 'base/model.dart';
import 'card.dart';
import 'card_view.dart';

class ScheduledCard implements KeyedListItem, Model {
  static const levelDurations = [
    Duration(hours: 4),
    Duration(days: 1),
    Duration(days: 2),
    Duration(days: 5),
    Duration(days: 14),
    Duration(days: 30),
    Duration(days: 60),
  ];

  String get key => card.key;
  set key(_) => throw Exception('ScheduledCard key is always set via "card"');
  Card card;
  int level;
  DateTime repeatAt;
  // TODO(dotdoom): this should come from card.deck.uid or similar.
  final String uid;

  ScheduledCard(
      {@required this.uid, @required this.card, this.level: 0, this.repeatAt}) {
    repeatAt ??= DateTime.fromMillisecondsSinceEpoch(0);
  }

  ScheduledCard.fromSnapshot(snapshotValue,
      {@required this.uid, @required this.card}) {
    assert(uid != null);
    assert(card != null);
    _parseSnapshot(snapshotValue);
  }

  void _parseSnapshot(snapshotValue) {
    // TODO(dotdoom): snapshotValue can be null if scard was removed during
    // 'updates'.
    try {
      level = int.parse(snapshotValue['level'].toString().substring(1));
    } on FormatException catch (e, stackTrace) {
      reportError('ScheduledCard', e, stackTrace);
      level = 0;
    }
    repeatAt =
        new DateTime.fromMillisecondsSinceEpoch(snapshotValue['repeatAt']);
  }

  static Stream<ScheduledCard> next(String deckId, String uid) =>
      FirebaseDatabase.instance
          .reference()
          .child('learning')
          .child(uid)
          .child(deckId)
          .orderByChild('repeatAt')
          // Need at least 2 because of how Firebase local cache works.
          // After we pick up the latest ScheduledCard and update it, it
          // triggers onValue twice: once with the updated ScheduledCard (most
          // likely triggered by local cache) and the second time with the next
          // ScheduledCard (fetched from the server). Doing keepSynced(true) on
          // the learning tree fixes this because local cache gets all entries.
          .limitToFirst(2)
          .onValue
          .transform(StreamTransformer.fromHandlers(
              handleData: (event, EventSink<ScheduledCard> sink) async {
        if (event.snapshot.value == null) {
          // The deck is empty. Should we offer the user to re-sync?
          sink.close();
          return;
        }

        // TODO(dotdoom): remove sorting once Flutter Firebase issue is fixed.
        // Workaround for https://github.com/flutter/flutter/issues/19389.
        var latestScheduledCard =
            ((event.snapshot.value.entries.toList() as List<MapEntry>)
                  ..sort((s1, s2) =>
                      s1.value['repeatAt'].compareTo(s2.value['repeatAt'])))
                .first;

        // TODO(dotdoom): delete dangling 'learning' if Card.fetch returns null.
        sink.add(ScheduledCard.fromSnapshot(latestScheduledCard.value,
            uid: uid, card: await Card.fetch(deckId, latestScheduledCard.key)));
      }));

  @override
  String get rootPath => 'learning/$uid/${card.deckId}';

  @override
  Map<String, dynamic> toMap(bool isNew) => {
        'learning/$uid/${card.deckId}/$key': {
          'level': 'L$level',
          'repeatAt': repeatAt.toUtc().millisecondsSinceEpoch,
        }
      };

  CardView answer(bool knows) {
    var cv = CardView(card, uid);
    cv.reply = knows;
    cv.levelBefore = level;
    if (knows) {
      level = min(level + 1, levelDurations.length - 1);
    } else {
      level = 0;
    }
    repeatAt = DateTime.now().toUtc().add(levelDurations[level]);
    return cv;
  }
}
