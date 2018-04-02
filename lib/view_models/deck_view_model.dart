import 'dart:async';

import '../models/deck.dart';
import '../models/stream_demuxer.dart';
import '../models/disposable.dart';
import '../models/observable_list.dart';
import '../models/keyed_event_list_mixin.dart';
import 'view_model.dart';

class DeckViewModel implements PersistableKeyedItem<DeckViewModel> {
  final String key;
  String name;
  String access;
  int cardsToLearn;
  Deck deck;

  StreamSubscription<DeckViewModel> _subscription;

  DeckViewModel.forStream(
      DeckViewModel model, StreamDemuxerEvent<String> update)
      : key = model.key,
        name = model.name,
        access = update.stream == 'access' ? update.value : null,
        cardsToLearn = update.stream == 'cardsToLearn' ? update.value : null;

  DeckViewModel(this.deck)
      : key = deck?.key,
        name = deck?.name;

  @override
  DeckViewModel absorb(DeckViewModel value) {
    // TODO(dotdoom): duplicating efforts?
    name = value.name ?? name;
    cardsToLearn = value.cardsToLearn ?? cardsToLearn;
    access = value.access ?? access;
    return this;
  }

  @override
  void dispose() {
    if (_subscription != null) {
      _subscription.cancel();
    }
  }

  void own(owner) {
    if (owner == null) {
      // Shortcut.
      // TODO(dotdoom): poor design.
      return;
    }

    // TODO(dotdoom): why sending item to owner (list) just to get it back?
    _subscription = new StreamDemuxer<String>({
      'access': deck.getAccess(),
      'cardsToLearn': deck.getNumberOfCardsToLearn(),
    }).map((evt) => new DeckViewModel.forStream(this, evt)).listen(
        (dvm) => owner.processKeyedEvent(new KeyedListEvent<DeckViewModel>(
              eventType: ListEventType.changed,
              value: dvm,
              previousSiblingKey: null,
            )));
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

    _deckViewModels.subscribeToKeyedEvents(
        Deck.getDecksEvents(uid).map((deckEvent) => new KeyedListEvent(
              eventType: deckEvent.eventType,
              previousSiblingKey: deckEvent.previousSiblingKey,
              // TODO(dotdoom): optimize (creating even for null?)
              value: new DeckViewModel(deckEvent.value)
                ..own(
                  deckEvent.eventType == ListEventType.added
                      ? _deckViewModels
                      : null,
                ),
              // TODO(dotdoom): KeyedList must manage subscription itself.
            )));

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
