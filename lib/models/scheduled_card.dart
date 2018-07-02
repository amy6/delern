import 'dart:async';
import 'dart:core';
import 'dart:math';

import 'package:meta/meta.dart';
import 'package:firebase_database/firebase_database.dart';

import 'base/keyed_list.dart';
import 'base/model.dart';
import 'card.dart';
import '../remote/error_reporting.dart';

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
          // TODO(dotdoom): do not endAt and instead let ViewModel decide when
          // to stop. This will make it (a) realtime and (b) possible to learn
          // cards beyond the intervals.
          .endAt(DateTime.now().millisecondsSinceEpoch)
          .limitToFirst(1)
          .onValue
          .transform(StreamTransformer.fromHandlers(
              handleData: (event, EventSink<ScheduledCard> sink) async {
        // TODO(dotdoom): figure out why snapshot.value is occasionally null.
        if (event.snapshot.key == null) {
          // No more learning!
          sink.close();
          return;
        }

        // Since we 'limitToFirst(1)', there must always be at most 1 child.
        var firstChildSnapshot = event.snapshot.value.entries.first;
        if (firstChildSnapshot.key == null) {
          // TODO(dotdoom): delete dangling 'learning'. Also brings next.
        } else {
          sink.add(ScheduledCard.fromSnapshot(firstChildSnapshot.value,
              uid: uid,
              card: await Card.fetch(deckId, firstChildSnapshot.key)));
        }
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

  void answer(bool knows) {
    if (knows) {
      level = min(level + 1, levelDurations.length - 1);
    } else {
      level = 0;
    }
    repeatAt = DateTime.now().toUtc().add(levelDurations[level]);
  }
}
