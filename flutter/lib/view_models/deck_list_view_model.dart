import 'dart:async';

import 'package:meta/meta.dart';

import '../models/base/stream_muxer.dart';
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
  DeckAccess get access => _access;
  int get cardsToLearn => _cardsToLearn;

  Deck _deck;
  DeckAccess _access;
  int _cardsToLearn;

  final int maxNumberOfCards;

  final ViewModelsList<DeckListItemViewModel> _owner;
  StreamSubscription<StreamMuxerEvent<bool>> _internalUpdates;

  DeckListItemViewModel(this._owner, this._deck,
      {@required this.maxNumberOfCards})
      : assert(maxNumberOfCards != null),
        _access = DeckAccess(deck: _deck) {
    _internalUpdates = StreamMuxer({
      false: _access.updates,
      true: _deck.getNumberOfCardsToLearn(maxNumberOfCards + 1),
    }).listen((event) {
      if (event.stream) {
        this._cardsToLearn = event.value;
      }
      // Send event to the owner list so that it can find our index
      // and notify subscribers.
      _owner.childUpdated(this);
    });
  }

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
  @mustCallSuper
  void dispose() {
    _internalUpdates.cancel();
  }

  @override
  String toString() => '#$key ${deck?.name} [$access $cardsToLearn]';
}

class DeckListViewModel implements Disposable {
  final String uid;
  final int _maxNumberOfCards = 200;

  ViewModelsList<DeckListItemViewModel> _deckViewModels;
  ProxyKeyedList<DeckListItemViewModel> _decksProxy;

  ProxyKeyedList<DeckListItemViewModel> get decks =>
      _decksProxy ??= ProxyKeyedList(_deckViewModels);

  DeckListViewModel(this.uid) {
    _deckViewModels = ViewModelsList<DeckListItemViewModel>(() =>
        Deck.getDecks(uid).map((deckEvent) => deckEvent.map((deck) =>
            DeckListItemViewModel(_deckViewModels, deck,
                maxNumberOfCards: _maxNumberOfCards))));
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
