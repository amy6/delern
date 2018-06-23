import 'dart:async';

import 'package:meta/meta.dart';

import '../models/base/stream_demuxer.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import 'base/activatable.dart';
import 'base/proxy_keyed_list.dart';
import 'base/view_models_list.dart';

class DeckListItemViewModel implements ListItemViewModel {
  String get key => _deck?.key;
  Deck get deck => _deck;
  AccessType get access => _access;
  int get cardsToLearn => _cardsToLearn;

  Deck _deck;
  AccessType _access;
  int _cardsToLearn;

  final ViewModelsList<DeckListItemViewModel> _owner;
  StreamSubscription<StreamDemuxerEvent<String>> _internalUpdates;

  DeckListItemViewModel(this._owner, this._deck);

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
  void activate() {
    if (_internalUpdates != null) {
      // This item is already activated. This must normally be only a side
      // effect of updateWith -> childUpdated cycle.
      return;
    }

    _internalUpdates = new StreamDemuxer<String>({
      'access': _deck.getAccess(),
      'cardsToLearn': _deck.getNumberOfCardsToLearn(),
    }).listen((event) {
      switch (event.stream) {
        case 'access':
          this._access = event.value;
          break;
        case 'cardsToLearn':
          this._cardsToLearn = event.value;
          break;
      }
      // Send event to the owner list so that it can find our index
      // and notify subscribers.
      _owner.childUpdated(this);
    });
  }

  @override
  @mustCallSuper
  void deactivate() {
    _internalUpdates?.cancel();
    _internalUpdates = null;
  }

  @override
  String toString() {
    return '#$key ${deck?.name} [$access $cardsToLearn]';
  }
}

class DeckListViewModel implements Activatable {
  final String uid;

  ViewModelsList<DeckListItemViewModel> _deckViewModels;
  ProxyKeyedList<DeckListItemViewModel> _decksProxy;

  ProxyKeyedList<DeckListItemViewModel> get decks =>
      _decksProxy ??= new ProxyKeyedList(_deckViewModels);

  DeckListViewModel(this.uid) {
    _deckViewModels = new ViewModelsList<DeckListItemViewModel>(() => Deck
        .getDecks(uid)
        .map((deckEvent) => deckEvent
            .map((deck) => new DeckListItemViewModel(_deckViewModels, deck))));
  }

  @override
  @mustCallSuper
  void deactivate() => _deckViewModels.deactivate();

  @override
  @mustCallSuper
  void activate() {
    deactivate();
    _deckViewModels.activate();
  }

  @mustCallSuper
  void dispose() {
    deactivate();
    _decksProxy?.dispose();
  }
}
