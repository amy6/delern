import 'dart:async';

import '../models/deck.dart';
import '../models/stream_demuxer.dart';
import 'attachable.dart';
import 'proxy_keyed_list.dart';
import 'view_models_list.dart';

class DeckViewModel implements ViewModel<ViewModelsList<DeckViewModel>> {
  // TODO(dotdoom): remove name in favor of deck ?
  String get key => _deck?.key;
  Deck get deck => _deck;
  String get name => _deck?.name;
  String get access => _access;
  int get cardsToLearn => _cardsToLearn;

  Deck _deck;
  String _access;
  int _cardsToLearn;

  StreamSubscription<StreamDemuxerEvent<String>> _internalUpdates;

  DeckViewModel(this._deck);

  @override
  DeckViewModel updateWith(DeckViewModel value) {
    if (this == value) {
      // This will happen when we sent an internal update event to the owner.
      return this;
    }

    assert(_deck.key == value._deck.key,
        'Attempting to absorb a deck with a different key');
    _deck = value._deck;
    return this;
  }

  @override
  void attachTo(ViewModelsList<DeckViewModel> owner) {
    if (_internalUpdates != null) {
      // This item is already attached - can assert that the owner is the same.
      // This must normally be only a side effect of absorb().
      return;
    }

    _internalUpdates = new StreamDemuxer<String>({
      'access': deck.getAccess(),
      'cardsToLearn': deck.getNumberOfCardsToLearn(),
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
      owner.childUpdated(this);
    });
  }

  @override
  void detach() {
    _internalUpdates?.cancel();
    _internalUpdates = null;
  }

  @override
  String toString() {
    return '#$key $name [$access $cardsToLearn]';
  }
}

class DecksViewModel implements Attachable<String> {
  String _uid;
  final ViewModelsList<DeckViewModel> _deckViewModels =
      new ViewModelsList<DeckViewModel>();
  ProxyKeyedList<DeckViewModel> _decksProxy;

  ProxyKeyedList<DeckViewModel> get decks =>
      _decksProxy ??= new ProxyKeyedList(_deckViewModels);

  String get uid => _uid;

  @override
  void detach() {
    _deckViewModels.detach();
  }

  @override
  void attachTo(uid) {
    _uid = uid;
    _deckViewModels.attachTo(Deck.getDecks(uid).map((deckEvent) {
      return new ViewModelsListEvent(
        eventType: deckEvent.eventType,
        previousSiblingKey: deckEvent.previousSiblingKey,
        value: new DeckViewModel(deckEvent.value),
        fullListValueForSet: deckEvent.fullListValueForSet
            ?.map((deck) => new DeckViewModel(deck)),
      );
    }));
  }

  void dispose() {
    detach();
    _decksProxy?.dispose();
  }
}
