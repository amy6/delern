import 'dart:async';

import '../models/base/transaction.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../remote/analytics.dart';
import '../view_models/base/database_list_event_processor.dart';
import '../view_models/base/filtered_sorted_keyed_list_processor.dart';
import '../view_models/base/observable_keyed_list.dart';

class DeckAccessesViewModel {
  final DeckModel deck;

  DeckAccessesViewModel(this.deck) : assert(deck != null) {
    _processor = FilteredSortedKeyedListProcessor(
        DatabaseListEventProcessor(() => DeckAccessModel.getDeckAccesses(deck))
            .list)
      ..comparator = (c1, c2) => c1.access.index.compareTo(c2.access.index);
  }

  ObservableKeyedList<DeckAccessModel> get list => _processor.list;

  set filter(Filter<DeckAccessModel> newValue) => _processor.filter = newValue;
  Filter<DeckAccessModel> get filter => _processor.filter;

  FilteredSortedKeyedListProcessor<DeckAccessModel> _processor;

  static Future<void> shareDeck(DeckAccessModel access) async {
    logShare(access.deck.key);
    var tr = Transaction();

    if (access.access == null) {
      return (tr..delete(access)).commit();
    }

    tr.save(access);
    if ((await DeckAccessModel.fetch(access.deck, access.uid)).key == null) {
      // If there's no DeckAccess, assume the deck hasn't been shared yet.
      tr.save(DeckModel(access.key)
        ..name = access.deck.name
        ..accepted = false
        ..markdown = access.deck.markdown
        ..type = access.deck.type
        ..category = access.deck.category
        ..access = access.access
        ..key = access.deck.key);
    } else {
      access.updateAccessFieldInDeck = true;
    }

    return tr.commit();
  }
}
