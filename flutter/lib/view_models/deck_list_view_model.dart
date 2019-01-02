import 'dart:async';

import 'package:delern_flutter/models/base/transaction.dart';
import 'package:delern_flutter/models/deck_access_model.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/remote/analytics.dart';
import 'package:delern_flutter/view_models/base/database_list_event_processor.dart';
import 'package:delern_flutter/view_models/base/filtered_sorted_keyed_list_processor.dart';
import 'package:delern_flutter/view_models/base/observable_keyed_list.dart';

class DeckListViewModel {
  final String uid;

  DeckListViewModel(this.uid) {
    _processor = FilteredSortedKeyedListProcessor(
        DatabaseListEventProcessor(() => DeckModel.getList(uid: uid)).list)
      ..comparator = (d1, d2) => d1.key.compareTo(d2.key);
  }

  ObservableKeyedList<DeckModel> get list => _processor.list;

  set filter(Filter<DeckModel> newValue) => _processor.filter = newValue;
  Filter<DeckModel> get filter => _processor.filter;

  FilteredSortedKeyedListProcessor<DeckModel> _processor;

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
}
