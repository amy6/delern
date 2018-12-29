import 'dart:async';

import 'package:meta/meta.dart';

import '../models/card.dart';

class CardListBlock {
  String deckKey;
  List<CardModel> _cardListValue;
  List<CardModel> get cardValue => _cardListValue;

  CardListBlock({@required this.deckKey}) : assert(deckKey != null) {
    _cardListValue = [];
  }

  Stream<List<CardModel>> get cardStream => CardModel.getCards(deckKey)
      .map((cardsList) => _cardListValue = cardsList);
}
