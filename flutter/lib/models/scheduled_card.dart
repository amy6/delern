import 'dart:async';
import 'dart:core';
import 'dart:math';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import '../remote/error_reporting.dart';
import 'base/database_list_event.dart';
import 'base/model.dart';
import 'base/stream_muxer.dart';
import 'base/transaction.dart';
import 'card.dart';
import 'card_view.dart';
import 'deck.dart';

class UnorderedListEvent<T> {
  final ListEventType eventType;
  final T value;
  final String key;

  UnorderedListEvent({
    @required this.key,
    @required this.eventType,
    @required this.value,
  });
}

Stream<UnorderedListEvent<T>> _unorderedChildEventsStream<T>(
        Query query, T snapshotParser(DataSnapshot s)) =>
    StreamMuxer<ListEventType>({
      ListEventType.itemAdded: query.onChildAdded,
      ListEventType.itemRemoved: query.onChildRemoved,
      ListEventType.itemChanged: query.onChildChanged,
    }).map((muxerEvent) {
      Event dbEvent = muxerEvent.value;
      return UnorderedListEvent(
        key: dbEvent.snapshot.key,
        eventType: muxerEvent.stream,
        value: snapshotParser(dbEvent.snapshot),
      );
    });

class ScheduledCardModel implements Model {
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
  CardModel card;
  // TODO(ksheremet): Find better place for storing uid
  String _uid;
  int level;
  DateTime repeatAt;

  ScheduledCardModel({@required this.card, @required String uid})
      : assert(card != null),
        assert(uid != null) {
    _uid = uid;
    level ??= 0;
    repeatAt ??= DateTime.fromMillisecondsSinceEpoch(0);
  }

  ScheduledCardModel.fromSnapshot(snapshotValue,
      {@required this.card, @required uid})
      : assert(card != null),
        assert(uid != null) {
    _uid = uid;
    _parseSnapshot(snapshotValue);
  }

/* It is used for cards randomizing appearance.
   Max jitter is 2h 59 min */
  Duration _getJitter() =>
      Duration(hours: Random().nextInt(3), minutes: Random().nextInt(60));

  void _parseSnapshot(snapshotValue) {
    if (snapshotValue == null) {
      // Assume the ScheduledCard doesn't exist anymore.
      key = null;
      return;
    }
    try {
      level = int.parse(snapshotValue['level'].toString().substring(1));
    } on FormatException catch (e, stackTrace) {
      ErrorReporting.report('ScheduledCard', e, stackTrace);
      level = 0;
    }
    repeatAt = DateTime.fromMillisecondsSinceEpoch(snapshotValue['repeatAt']);
  }

  static Stream<ScheduledCardModel> next(DeckModel deck) =>
      FirebaseDatabase.instance
          .reference()
          .child('learning')
          .child(deck.uid)
          .child(deck.key)
          .orderByChild('repeatAt')
          // Need at least 2 because of how Firebase local cache works.
          // After we pick up the latest ScheduledCard and update it, it
          // triggers onValue twice: once with the updated ScheduledCard (most
          // likely triggered by local cache) and the second time with the next
          // ScheduledCard (fetched from the server). Doing keepSynced(true) on
          // the learning tree fixes this because local cache gets all entries.
          .limitToFirst(2)
          .onValue
          .transform(
              StreamTransformer.fromHandlers(handleData: (event, sink) async {
        if (event.snapshot.value == null) {
          // The deck is empty. Should we offer the user to re-sync?
          sink.close();
          return;
        }

        // TODO(dotdoom): remove sorting once Flutter Firebase issue is fixed.
        // Workaround for https://github.com/flutter/flutter/issues/19389.
        List<MapEntry> allEntries = event.snapshot.value.entries.toList();
        var latestScheduledCard = (allEntries
              ..sort((s1, s2) {
                var repeatAtComparison =
                    s1.value['repeatAt'].compareTo(s2.value['repeatAt']);
                // Sometimes repeatAt of 2 cards may be the same, which
                // will result in unstable order. Most often this is
                // happening to the newly added cards, which have
                // repeatAt = 0.
                // We mimic Firebase behavior here, which falls back to
                // sorting lexicographically by key.
                // TODO(dotdoom): do not set repeatAt = 0?
                if (repeatAtComparison == 0) {
                  return s1.key.compareTo(s2.key);
                }
                return repeatAtComparison;
              }))
            .first;

        var card = await CardModel.fetch(deck, latestScheduledCard.key);
        var scheduledCard = ScheduledCardModel.fromSnapshot(
            latestScheduledCard.value,
            card: card,
            uid: deck.uid);

        if (card.key == null) {
          // Card has been removed but we still have ScheduledCard for it.

          // card.key is used within ScheduledCard and must be set.
          card.key = latestScheduledCard.key;
          print('Removing dangling ScheduledCard ${scheduledCard.key}');
          (Transaction()..delete(scheduledCard)).commit();
          return;
        }

        sink.add(scheduledCard);
      }));

  @override
  String get rootPath => 'learning/$_uid/${card.deckKey}';

  @override
  Map<String, dynamic> toMap(bool isNew) => {
        'learning/$_uid/${card.deckKey}/$key': {
          'level': 'L$level',
          'repeatAt': repeatAt.toUtc().millisecondsSinceEpoch,
        }
      };

  CardViewModel answer(bool knows, bool learnBeyondHorizon) {
    var cv = CardViewModel(uid: _uid, card: card)
      ..reply = knows
      ..levelBefore = level;

    // if know==true and learnBeyondHorizon==true, the level stays the same
    if (knows && !learnBeyondHorizon) {
      level = min(level + 1, levelDurations.length - 1);
    }
    if (!knows) {
      level = 0;
    }
    repeatAt = DateTime.now().toUtc().add(levelDurations[level] + _getJitter());
    return cv;
  }

  static Stream<UnorderedListEvent<Iterable<ScheduledCardModel>>> listsForUser(
          String uid) =>
      _unorderedChildEventsStream(
          FirebaseDatabase.instance.reference().child('learning').child(uid),
          (scheduledCardsOfDeck) {
        final Map value = scheduledCardsOfDeck.value;
        // TODO(dotdoom): remove card.
        return value.entries.map((entry) => ScheduledCardModel.fromSnapshot(
            entry.value,
            card: CardModel(deckKey: scheduledCardsOfDeck.key)..key = entry.key,
            uid: uid));
      });
}
