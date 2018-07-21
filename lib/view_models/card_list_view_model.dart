import 'dart:async';

import 'package:meta/meta.dart';

import '../models/card.dart';
import '../models/deck.dart';
import 'base/activatable.dart';
import 'base/proxy_keyed_list.dart';
import 'base/view_models_list.dart';

class CardListItemViewModel implements ListItemViewModel {
  String get key => _card?.key;
  Card get card => _card;
  Card _card;

  CardListItemViewModel(this._card);

  @override
  CardListItemViewModel updateWith(CardListItemViewModel value) => value;

  @override
  @mustCallSuper
  void activate() {}

  @override
  @mustCallSuper
  void deactivate() {}

  @override
  String toString() => _card?.toString();
}

class CardListViewModel implements Activatable {
  final Deck deck;

  ViewModelsList<CardListItemViewModel> _cardViewModels;
  ProxyKeyedList<CardListItemViewModel> _cardsProxy;

  ProxyKeyedList<CardListItemViewModel> get cards =>
      _cardsProxy ??= new ProxyKeyedList(_cardViewModels);

  CardListViewModel(this.deck) {
    _cardViewModels = new ViewModelsList<CardListItemViewModel>(() => Card
        .getCards(deck)
        .map((cardEvent) =>
            cardEvent.map((card) => new CardListItemViewModel(card))));
  }

  @override
  @mustCallSuper
  void deactivate() => _cardViewModels.deactivate();

  @override
  @mustCallSuper
  void activate() {
    deactivate();
    _cardViewModels.activate();
  }

  @mustCallSuper
  void dispose() {
    deactivate();
    _cardsProxy?.dispose();
  }

  Stream<void> get updates => deck.updates;
}
