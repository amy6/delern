import 'dart:async';

import '../models/deck.dart';
import '../models/stream_demuxer.dart';
import '../models/disposable.dart';
import '../models/observable_list.dart';
import '../models/keyed_event_list_mixin.dart';
import 'view_model.dart';

class DeckViewModel implements PersistableKeyedItem<DeckViewModel> {
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

  void own(owner) {
    if (owner == null) {
      // Shortcut.
      // TODO(dotdoom): poor design.
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
      (owner as KeyedEventListMixin<DeckViewModel>)
          .processKeyedEvent(new KeyedListEvent<DeckViewModel>(
        eventType: ListEventType.changed,
        value: this,
      ));
    });
  }

  @override
  void dispose() {
    if (_internalUpdates != null) {
      _internalUpdates.cancel();
    }
    super.dispose();
  }
}

class DecksViewModel implements Disposable {
  PersistableKeyedItemsList<DeckViewModel> _deckViewModels;
  StreamSubscription<KeyedListEvent<DeckViewModel>> _sub;

  ObservableList<DeckViewModel> decks;

  DecksViewModel(Iterable<Deck> deckModels, String uid) {
    _deckViewModels = new PersistableKeyedItemsList<DeckViewModel>(
        deckModels.map((deck) => new DeckViewModel(deck)).toList());
    _deckViewModels.forEach((d) => d.own(_deckViewModels));

    _deckViewModels
        .subscribeToKeyedEvents(Deck.getDecksEvents(uid).map((deckEvent) {
      DeckViewModel model;
      if (deckEvent.eventType == ListEventType.added ||
          deckEvent.eventType == ListEventType.changed) {
        model = new DeckViewModel(deckEvent.value);
        if (deckEvent.eventType == ListEventType.added) {
          model.own(_deckViewModels);
        }
      }
      return new KeyedListEvent(
        eventType: deckEvent.eventType,
        previousSiblingKey: deckEvent.previousSiblingKey,
        // TODO(dotdoom): optimize (creating even for null?)
        value: model,
      );
    }));

    decks = _deckViewModels;
    /*new SortedObservableList<DeckViewModel>(
    new FilteredObservableList<DeckViewModel>(_deckViewModels)
      ..filter = (d) => d.cardsToLearn != null)
    ..comparator = (d1, d2) => d2.name.compareTo(d1.name);*/
  }

  static Future<DecksViewModel> getDecks(String uid) async {
    return new DecksViewModel(await Deck.getDecks(uid).first, uid);
  }

  @override
  void dispose() {
    _deckViewModels.dispose();
    _sub.cancel();
  }
}
