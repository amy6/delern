import 'dart:async';

import '../models/deck.dart';
import '../models/stream_demuxer.dart';
import '../models/disposable.dart';
import '../models/observable_list.dart';
import '../models/keyed_event_list_mixin.dart';
import 'view_model.dart';

class DeckViewModel
    implements PersistableKeyedItem<PersistableKeyedItemsList<DeckViewModel>> {
  String get key => _deck.key;
  Deck get deck => _deck;
  String get name => _deck.name;
  String get access => _access;
  int get cardsToLearn => _cardsToLearn;

  Deck _deck;
  String _access;
  int _cardsToLearn;

  StreamSubscription<StreamDemuxerEvent<String>> _internalUpdates;

  DeckViewModel(this._deck);

  @override
  DeckViewModel absorb(DeckViewModel value) {
    if (this == value) {
      // This will happen when we sent an internal update event to the owner.
      return this;
    }

    assert(_deck.key == value._deck.key,
        'Attempting to absorb a deck with a different key');
    _deck = value._deck;
    return this;
  }

  void attachTo(owner) {
    if (_internalUpdates != null) {
      // This item is already owned - can assert that the owner is the same.
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
      owner.processKeyedEvent(new KeyedListEvent<DeckViewModel>(
        eventType: ListEventType.itemChanged,
        value: this,
      ));
    });
  }

  @override
  void detach() {
    if (_internalUpdates != null) {
      _internalUpdates.cancel();
    }
  }
}

class DecksViewModel implements Disposable<String> {
  PersistableKeyedItemsList<DeckViewModel> _deckViewModels =
      new PersistableKeyedItemsList<DeckViewModel>();

  // TODO(dotdoom): sort / filter
  ObservableList<DeckViewModel> get decks => _deckViewModels;

  @override
  void detach() {
    _deckViewModels.detach();
  }

  @override
  void attachTo(String uid) {
    _deckViewModels.subscribeToKeyedEvents(Deck.getDecks(uid).map((deckEvent) {
      return new KeyedListEvent(
        eventType: deckEvent.eventType,
        previousSiblingKey: deckEvent.previousSiblingKey,
        value: new DeckViewModel(deckEvent.value),
        fullListValueForSet: deckEvent.fullListValueForSet
            ?.map((deck) => new DeckViewModel(deck)),
      );
    }));
  }
}
