import 'dart:async';

import '../models/card.dart';
import '../models/deck.dart';
import '../models/base/stream_demuxer.dart';

class CardViewModel {
  String get key => card?.key;
  String get front => card?.front;
  String get back => card?.back;
  Deck get deck => _deck;

  // TODO(dotdoom): make this readonly, fill in on creation, introduce 'exists' method.
  Card card;
  Deck _deck;

  CardViewModel(this._deck, [this.card]);

  Stream<void> get updates => StreamDemuxer(Map.fromIterable([
        _deck?.updates,
        card?.updates,
      ].where((stream) => stream != null)));

  @override
  String toString() => (card ?? _deck).toString();

  Future<void> saveCard(bool addReverse) async {
    assert(card.deckId == _deck.key);
    await card.save(_deck.uid);
    if (addReverse) {
      await Card(_deck.key, front: card.back, back: card.front).save(_deck.uid);
    }
  }

  Future<void> deleteCard() => card.delete(_deck.uid);
}
