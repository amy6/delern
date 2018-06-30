import 'dart:async';
import 'dart:core';

import 'package:meta/meta.dart';
import 'package:firebase_database/firebase_database.dart';

import 'base/keyed_list.dart';
import '../remote/error_reporting.dart';

class ScheduledCard implements KeyedListItem {
  static const levelDurations = [
    Duration(hours: 4),
    Duration(days: 1),
    Duration(days: 2),
    Duration(days: 5),
    Duration(days: 14),
    Duration(days: 30),
    Duration(days: 60),
  ];

  String key;
  int level;
  DateTime repeatAt;

  String get cardId => key;
  final String uid;

  ScheduledCard({@required this.uid, this.level, this.repeatAt});

  ScheduledCard.fromSnapshot(this.key, snapshotValue, {@required this.uid}) {
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
          .map((event) {
        // Since we 'limitToFirst(1)', there must always be only 1 child.
        var firstChildSnapshot = event.snapshot.value.entries.first;
        return ScheduledCard.fromSnapshot(
            firstChildSnapshot.key, firstChildSnapshot.value,
            uid: uid);
      });
}
