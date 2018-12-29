import 'dart:async';

import 'package:meta/meta.dart';

import '../models/base/transaction.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../remote/analytics.dart';
import 'base/disposable.dart';
import 'base/proxy_keyed_list.dart';
import 'base/view_models_list.dart';

class DeckListItemViewModel implements ListItemViewModel {
  String get key => _deck?.key;
  Deck get deck => _deck;

  Deck _deck;

  DeckListItemViewModel(this._deck);

  @override
  DeckListItemViewModel updateWith(DeckListItemViewModel value) {
    if (identical(this, value)) {
      // This will happen when we sent an internal update event to the owner.
      return this;
    }

    assert(_deck.key == value._deck.key,
        'Attempting to absorb a deck with a different key');
    _deck = value._deck;
    return this;
  }

  @override
  String toString() => '#$key ${deck?.name} [${deck?.access}]';
}

class DeckListViewModel implements Disposable {
  final String uid;

  ViewModelsList<DeckListItemViewModel> _deckViewModels;
  ProxyKeyedList<DeckListItemViewModel> _decksProxy;

  ProxyKeyedList<DeckListItemViewModel> get decks =>
      _decksProxy ??= ProxyKeyedList(_deckViewModels);

  DeckListViewModel(this.uid) {
    _deckViewModels = ViewModelsList<DeckListItemViewModel>(() =>
        Deck.getDecks(uid).map((deckEvent) =>
            deckEvent.map((deck) => DeckListItemViewModel(deck))));
  }

  @override
  @mustCallSuper
  void dispose() {
    _deckViewModels.dispose();
    _decksProxy?.dispose();
  }

  static Future<void> createDeck(Deck deck, String email) {
    logDeckCreate();
    return (Transaction()
          ..save(deck..access = AccessType.owner)
          ..save(
              DeckAccess(deck: deck, access: AccessType.owner, email: email)))
        .commit();
  }
}
