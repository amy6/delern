import 'dart:async';

import 'package:delern_flutter/models/base/database_list_event.dart';
import 'package:delern_flutter/models/base/transaction.dart';
import 'package:delern_flutter/models/deck_access_model.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/models/scheduled_card_model.dart';
import 'package:delern_flutter/remote/analytics.dart';
import 'package:delern_flutter/view_models/base/database_list_event_processor.dart';
import 'package:delern_flutter/view_models/base/filtered_sorted_keyed_list_processor.dart';
import 'package:delern_flutter/view_models/base/observable_keyed_list.dart';

class NumberOfCardsDue {
  int get value => _value;
  int _value = 0;

  Stream<int> get stream => _controller.stream;
  final _controller = StreamController<int>.broadcast();

  Timer _refreshTimer;

  NumberOfCardsDue._();

  void _addValue(int newValue) {
    _value = newValue;
    _controller.add(newValue);
  }

  void _dispose() {
    _controller.close();
    _refreshTimer?.cancel();
  }
}

class DecksListBloc {
  final String uid;

  DecksListBloc(this.uid) {
    _decksProcessor = FilteredSortedKeyedListProcessor(
        DatabaseListEventProcessor(() => DeckModel.getList(uid: uid)).list)
      ..comparator = (d1, d2) => d1.key.compareTo(d2.key);

    // Delay initial data load. In case we have a significant amount of
    // ScheduledCards, loading them slows down decks list, because of the
    // MethodChannel bottleneck.
    Future.delayed(const Duration(milliseconds: 100), _loadScheduledCards);
  }

  ObservableKeyedList<DeckModel> get decksList => _decksProcessor.list;

  Filter<DeckModel> get decksListFilter => _decksProcessor.filter;
  set decksListFilter(Filter<DeckModel> newValue) =>
      _decksProcessor.filter = newValue;

  FilteredSortedKeyedListProcessor<DeckModel> _decksProcessor;

  static Future<void> createDeck(DeckModel deck, String email) {
    logDeckCreate();
    return (Transaction()
          ..save(deck..access = AccessType.owner)
          ..save(DeckAccessModel(deckKey: deck.key)
            ..key = deck.uid
            ..access = AccessType.owner
            ..email = email))
        .commit();
  }

  void _loadScheduledCards() {
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
            // TODO(dotdoom): consider using _processor to find out when a deck
            //                is removed and resources can be released.
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
      final refreshTimerInterval = nextRepeatAt.difference(now) + _timerDelay;
      print('Setting deck $deckKey refresh timer for $refreshTimerInterval');
      // Set timer to re-run this method when next repeatAt is due.
      cardsDue._refreshTimer = Timer(
          refreshTimerInterval, () => _scheduledCardsChanged(deckKey, value));
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
      _numberOfCardsDue.putIfAbsent(deckKey, () => NumberOfCardsDue._());
  final _numberOfCardsDue = <String, NumberOfCardsDue>{};

  /// Close all streams and release associated timer resources.
  // TODO(dotdoom): consider self-disposing map elements for onCancel of stream.
  void dispose() {
    _numberOfCardsDue.values.forEach((c) => c._dispose());
  }
}
