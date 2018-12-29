import 'package:meta/meta.dart';

import '../models/card.dart';
import '../view_models/base/database_list_event_processor.dart';
import '../view_models/base/keyed_list_event_processor.dart';

class CardListViewModel {
  String deckKey;

  CardListViewModel({@required this.deckKey}) : assert(deckKey != null) {
    _cardProcessor =
        DatabaseListEventProcessor(() => CardModel.getCards(deckKey));
  }

  KeyedListEventProcessor<CardModel, dynamic> _cardProcessor;

  KeyedListEventProcessor<CardModel, dynamic> get cardProcessor =>
      _cardProcessor;
}
