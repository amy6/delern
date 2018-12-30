import 'dart:async';

import '../models/base/database_list_event.dart';
import '../models/scheduled_card.dart';

class ScheduledCardsBloc {
  final String uid;

  ScheduledCardsBloc(this.uid) {
    ScheduledCardModel.listsForUser(uid).listen((event) {
      switch (event.eventType) {
        case ListEventType.itemAdded:
        case ListEventType.itemChanged:
          _scheduledCardsChanged(event.key, event.value);
          break;
        case ListEventType.itemRemoved:
          _numberOfCardsTimers.remove(event.key)?.cancel();
          _numberOfCardsValues.remove(event.key);
          // Do not close the stream. itemRemoved occurs when we do not have any
          // cards left in a deck; once the user adds another card, we will have
          // to notify our subscribers, which will be gone if we call close().
          // TODO(dotdoom): consider listening to /decks/$uid to find out when
          //                a deck is removed and resources can be released.
          _numberOfCardsControllers[event.key]?.add(0);
          break;
        default:
      }
    });
  }

  /// A delay between next scheduled card and our timer trigger, to avoid time
  /// computation uncertainties, and also avoid timer restart churn if multiple
  /// cards come with a small interval between them.
  static const _timerDelay = Duration(seconds: 30);

  void _scheduledCardsChanged(
      String deckKey, Iterable<ScheduledCardModel> value) {
    final now = DateTime.now();
    final notYetDue = value.where((sc) => sc.repeatAt.isAfter(now));

    final numberOfCardsDue =
        _numberOfCardsValues[deckKey] = value.length - notYetDue.length;
    _numberOfCardsControllers[deckKey]?.add(numberOfCardsDue);

    _numberOfCardsTimers.remove(deckKey)?.cancel();
    if (notYetDue.isNotEmpty) {
      // Find the closest (minimum) repeatAt.
      final nextRepeatAt = notYetDue
          .reduce((m1, m2) => m1.repeatAt.isBefore(m2.repeatAt) ? m1 : m2)
          .repeatAt;
      // Set timer to re-run this method when next repeatAt is due.
      _numberOfCardsTimers[deckKey] = Timer(
          nextRepeatAt.difference(now) + _timerDelay,
          () => _scheduledCardsChanged(deckKey, value));
    }
  }

  final _numberOfCardsValues = <String, int>{};
  final _numberOfCardsControllers = <String, StreamController<int>>{};
  final _numberOfCardsTimers = <String, Timer>{};

  /// Current number of cards due for the Deck [deckKey].
  int numberOfCardsValue(String deckKey) => _numberOfCardsValues[deckKey];

  /// A stream of cards for the Deck [deckKey].
  Stream<int> numberOfCardsStream(String deckKey) =>
      // Put StreamController in place even if we don't have data for this deck
      // yet. Later when we get information about this deck, we will push new
      // data directly to a subscriber.
      // Do not remove this controller from the list in onCancel, because there
      // may be more references to it, which can re-subscribe in future.
      (_numberOfCardsControllers[deckKey] ??= StreamController<int>.broadcast())
          .stream;

  /// Close all streams and release associated timer resources.
  void dispose() {
    _numberOfCardsControllers.values.forEach((c) => c.close());
    _numberOfCardsTimers.values.forEach((t) => t.cancel());
  }
}
