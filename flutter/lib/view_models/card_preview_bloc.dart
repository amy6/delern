import 'dart:async';

import 'package:meta/meta.dart';

import '../models/base/transaction.dart';
import '../models/card.dart';
import '../models/deck.dart';
import '../models/scheduled_card.dart';

class CardViewModel {
  CardModel card;
  DeckModel deck;

  CardViewModel({this.card, @required this.deck}) : assert(deck != null) {
    card ??= CardModel();
  }

  CardViewModel._copyFrom(CardViewModel other)
      : card = CardModel.copyFrom(other.card),
        deck = DeckModel.copyFrom(other.deck);
}

class CardPreviewBloc {
  CardPreviewBloc({@required CardModel card, @required DeckModel deck})
      : assert(card != null),
        assert(deck != null),
        deckNameValue = deck.name {
    _cardValue = CardViewModel(card: card, deck: deck);
    _deleteCardController.stream.listen(_deleteCard);
  }

  final String deckNameValue;
  // TODO(dotdoom): add deckNameStream.

  CardViewModel _cardValue;
  CardViewModel get cardValue => CardViewModel._copyFrom(_cardValue);
  Stream<CardViewModel> get cardStream =>
      // TODO(dotdoom): mux in DeckModel updates stream, too.
      CardModel.get(deckKey: _cardValue.card.deckKey, key: _cardValue.card.key)
          .map((cardModel) => CardViewModel._copyFrom(_cardValue =
              CardViewModel(card: cardModel, deck: _cardValue.deck)));

  final StreamController<void> _deleteCardController = StreamController<void>();
  Sink<void> get deleteCard => _deleteCardController.sink;

  void _deleteCard(void _) {
    // TODO(dotdoom): move to models?
    final card =
        Card(deck: Deck(uid: _cardValue.deck.uid)..key = _cardValue.deck.key);
    (Transaction()..delete(_cardValue.card)..delete(ScheduledCard(card: card)))
        .commit();
  }
}
