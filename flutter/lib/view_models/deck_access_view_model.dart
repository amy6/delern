import 'dart:async';

import 'package:delern_flutter/models/base/transaction.dart';
import 'package:delern_flutter/models/deck.dart';
import 'package:delern_flutter/models/deck_access.dart';
import 'package:delern_flutter/remote/analytics.dart';
import 'package:delern_flutter/view_models/base/database_list_event_processor.dart';
import 'package:delern_flutter/view_models/base/filtered_sorted_keyed_list_processor.dart';
import 'package:delern_flutter/view_models/base/observable_keyed_list.dart';

class DeckAccessesViewModel {
  final DeckModel deck;

  DeckAccessesViewModel(this.deck) : assert(deck != null) {
    _processor = FilteredSortedKeyedListProcessor(DatabaseListEventProcessor(
        () => DeckAccessModel.getList(deckKey: deck.key)).list)
      ..comparator = (c1, c2) => c1.access.index.compareTo(c2.access.index);
  }

  ObservableKeyedList<DeckAccessModel> get list => _processor.list;

  set filter(Filter<DeckAccessModel> newValue) => _processor.filter = newValue;
  Filter<DeckAccessModel> get filter => _processor.filter;

  FilteredSortedKeyedListProcessor<DeckAccessModel> _processor;

  static Future<void> shareDeck(DeckAccessModel access, DeckModel deck) async {
    assert(deck.key == access.deckKey);

    logShare(access.deckKey);
    var tr = Transaction();

    if (access.access == null) {
      return (tr..delete(access)).commit();
    }

    tr.save(access);
    if ((await DeckAccessModel.get(deckKey: access.deckKey, key: access.key)
                .first)
            .key ==
        null) {
      // If there's no DeckAccess, assume the deck hasn't been shared yet.
      tr.save(DeckModel.copyFrom(deck)
        ..uid = access.key
        ..accepted = false
        ..access = access.access);
    }

    return tr.commit();
  }
}
