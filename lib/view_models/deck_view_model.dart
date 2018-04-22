import 'dart:async';

import 'package:meta/meta.dart';

import '../models/deck.dart';
import '../models/keyed_list.dart';
import '../models/stream_demuxer.dart';
import 'activatable.dart';
import 'proxy_keyed_list.dart';
import 'view_models_list.dart';

class DeckViewModel implements ViewModel {
  String get key => _deck?.key;
  Deck get deck => _deck;
  String get name => _deck?.name;
  String get access => _access;
  int get cardsToLearn => _cardsToLearn;

  Deck _deck;
  String _access;
  int _cardsToLearn;

  final ViewModelsList<DeckViewModel> _owner;
  StreamSubscription<StreamDemuxerEvent<String>> _internalUpdates;

  DeckViewModel(this._owner, this._deck);

  @override
  DeckViewModel updateWith(DeckViewModel value) {
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
    return '#$key $name [$access $cardsToLearn]';
  }
}

class DecksViewModel implements Activatable {
  final String uid;

  ViewModelsList<DeckViewModel> _deckViewModels;
  ProxyKeyedList<DeckViewModel> _decksProxy;

  ProxyKeyedList<DeckViewModel> get decks =>
      _decksProxy ??= new ProxyKeyedList(_deckViewModels);

  DecksViewModel(this.uid) {
    _deckViewModels = new ViewModelsList<DeckViewModel>(
        () => Deck.getDecks(uid).map((deckEvent) {
              return new KeyedListEvent(
                eventType: deckEvent.eventType,
                previousSiblingKey: deckEvent.previousSiblingKey,
                value: new DeckViewModel(_deckViewModels, deckEvent.value),
                fullListValueForSet: deckEvent.fullListValueForSet
                    ?.map((deck) => new DeckViewModel(_deckViewModels, deck)),
              );
            }));
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
