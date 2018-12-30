import 'package:meta/meta.dart';

import '../models/card.dart';
import '../view_models/base/database_list_event_processor.dart';
import '../view_models/base/filtered_sorted_keyed_list_processor.dart';
import '../view_models/base/observable_keyed_list.dart';

class CardListViewModel {
  final String deckKey;

  ObservableKeyedList<CardModel> get list => _processor.list;

  set filter(Filter<CardModel> newValue) => _processor.filter = newValue;
  Filter<CardModel> get filter => _processor.filter;

  FilteredSortedKeyedListProcessor<CardModel> _processor;

  CardListViewModel({@required this.deckKey}) : assert(deckKey != null) {
    _processor = FilteredSortedKeyedListProcessor(
        DatabaseListEventProcessor(() => CardModel.getList(deckKey: deckKey))
            .list)
      ..comparator =
          (c1, c2) => c1.front.toLowerCase().compareTo(c2.front.toLowerCase());
  }
}
