import 'dart:async';

import 'package:delern_flutter/models/base/database_list_event.dart';
import 'package:delern_flutter/models/scheduled_card_model.dart';
import 'package:meta/meta.dart';

class NumberOfCardsDue {
  int get value => _value;
  int _value = 0;

  Stream<int> get stream => _controller.stream;
  final _controller = StreamController<int>.broadcast();

  Timer _refreshTimer;

  void _addValue(int newValue) {
    _value = newValue;
    _controller.add(newValue);
  }

  @mustCallSuper
  void _dispose() {
    _controller.close();
    _refreshTimer?.cancel();
  }
}

class DecksListBloc {
  final String uid;

  DecksListBloc(this.uid) {
    // Delay initial data load. In case we have a significant amount of
    // ScheduledCards, loading them slows down decks list, because of the
    // MethodChannel bottleneck.
    Future.delayed(const Duration(milliseconds: 200), _initialLoad);
  }

  void _initialLoad() {
    ScheduledCardModel.listsForUser(uid).listen((event) {
      switch (event.eventType) {
        case ListEventType.itemAdded:
        case ListEventType.itemChanged:
          _scheduledCardsChanged(event.key, event.value);
          break;
        case ListEventType.itemRemoved:
          if (_numberOfCardsDue.containsKey(event.key)) {
            // Do not close the stream. itemRemoved occurs when we do not have
            // any cards left in a deck; once the user adds another card, we
            // will have to notify our subscribers, which will be gone if we
            // call close().
            // TODO(dotdoom): consider listening to /decks/$uid to find out when
            //                a deck is removed and resources can be released.
            _numberOfCardsDue[event.key]
              .._refreshTimer?.cancel()
              .._addValue(0);
          }
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

    final cardsDue = numberOfCardsDue(deckKey)
      .._addValue(value.length - notYetDue.length);

    cardsDue._refreshTimer?.cancel();
    if (notYetDue.isNotEmpty) {
      // Find the closest (minimum) repeatAt.
      final nextRepeatAt = notYetDue
          .reduce((m1, m2) => m1.repeatAt.isBefore(m2.repeatAt) ? m1 : m2)
          .repeatAt;
      // Set timer to re-run this method when next repeatAt is due.
      cardsDue._refreshTimer = Timer(nextRepeatAt.difference(now) + _timerDelay,
          () => _scheduledCardsChanged(deckKey, value));
    }
  }

  /// Current value and a stream of values for the number of ScheduledCards due
  /// for learning. This method never returns null.
  NumberOfCardsDue numberOfCardsDue(String deckKey) =>
      // Put StreamController in place even if we don't have data for this deck
      // yet. Later, when we get information about this deck, we will push new
      // data directly to a subscriber.
      // Do not remove this controller from the list in
      // StreamController.onCancel, because there may be more references to it,
      // which can re-subscribe in future.
      _numberOfCardsDue.putIfAbsent(deckKey, () => NumberOfCardsDue());
  final _numberOfCardsDue = <String, NumberOfCardsDue>{};

  /// Close all streams and release associated timer resources.
  void dispose() {
    _numberOfCardsDue.values.forEach((c) => c._dispose());
  }
}
