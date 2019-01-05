import 'package:delern_flutter/models/base/delayed_initialization.dart';
import 'package:delern_flutter/models/card_model.dart';
import 'package:delern_flutter/view_models/base/filtered_sorted_observable_list.dart';
import 'package:meta/meta.dart';

class CardListViewModel {
  final String deckKey;

  DelayedInitializationObservableList<CardModel> get list => _list;
  final FilteredSortedObservableList<CardModel> _list;

  set filter(Filter<CardModel> newValue) => _list.filter = newValue;
  Filter<CardModel> get filter => _list.filter;

  CardListViewModel({@required this.deckKey})
      : assert(deckKey != null),
        _list =
            // Analyzer bug: https://github.com/dart-lang/sdk/issues/35577.
            // ignore: unnecessary_parenthesis
            (FilteredSortedObservableList(CardModel.getList(deckKey: deckKey))
              ..comparator = (c1, c2) =>
                  c1.front.toLowerCase().compareTo(c2.front.toLowerCase()));
}
