import 'dart:async';

import 'package:meta/meta.dart';

import '../models/base/stream_demuxer.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../models/base/transaction.dart';
import 'base/activatable.dart';
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

  final ViewModelsList<DeckListItemViewModel> _owner;
  StreamSubscription<StreamDemuxerEvent<bool>> _internalUpdates;

  DeckListItemViewModel(this._owner, this._deck) {
    _access = DeckAccess(_deck)..key = _deck.uid;
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
  void activate() {
    if (_internalUpdates != null) {
      // This item is already activated. This must normally be only a side
      // effect of updateWith -> childUpdated cycle.
      return;
    }

    _internalUpdates = StreamDemuxer({
      false: _access.updates,
      true: _deck.getNumberOfCardsToLearn(),
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

  static Future<void> createDeck(Deck deck) {
    var t = Transaction();
    t.save(deck);
    t.save(DeckAccess(deck, access: AccessType.owner)..key = deck.uid);
    return t.commit();
  }
}
