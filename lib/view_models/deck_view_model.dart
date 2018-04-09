import 'dart:async';

import '../models/deck.dart';
import '../models/stream_demuxer.dart';
import '../models/attachable.dart';
import '../models/observable_list.dart';
import '../models/models_list.dart';

class DeckViewModel implements Model<ModelsList<DeckViewModel>> {
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

  @override
  void attachTo(ModelsList<DeckViewModel> owner) {
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
      owner.processKeyedEvent(new ModelsListEvent<DeckViewModel>(
        eventType: ListEventType.itemChanged,
        value: this,
      ));
    });
  }

  @override
  void detach() {
    _internalUpdates?.cancel();
    _internalUpdates = null;
  }
}

class DecksViewModel implements Attachable<String> {
  ModelsList<DeckViewModel> _deckViewModels = new ModelsList<DeckViewModel>();

  // TODO(dotdoom): sort / filter
  ObservableList<DeckViewModel> get decks => _deckViewModels;

  @override
  void detach() {
    _deckViewModels.detach();
  }

  @override
  void attachTo(String uid) {
    _deckViewModels.attachTo(Deck.getDecks(uid).map((deckEvent) {
      return new ModelsListEvent(
        eventType: deckEvent.eventType,
        previousSiblingKey: deckEvent.previousSiblingKey,
        value: new DeckViewModel(deckEvent.value),
        fullListValueForSet: deckEvent.fullListValueForSet
            ?.map((deck) => new DeckViewModel(deck)),
      );
    }));
  }
}
