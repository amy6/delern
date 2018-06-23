import 'dart:async';

import '../models/card.dart';
import '../models/deck.dart';
import '../models/base/stream_demuxer.dart';

class CardViewModel {
  Deck get deck => _deck;
  Card get card => _card;

  final Deck _deck;
  Card _card;

  CardViewModel(this._deck, [this._card]) {
    _card ??= Card(_deck.key);
  }

  Stream<void> get updates => _card.key == null
      ? _deck.updates
      : StreamDemuxer({
          0: _deck.updates,
          1: _card.updates,
        });

  @override
  String toString() => (_card.key == null ? _deck : _card).toString();

  Future<void> saveCard(bool addReverse) async {
    assert(_card.deckId == _deck.key);
    await _card.save(_deck.uid);
    if (addReverse) {
      await Card(_deck.key, front: _card.back, back: _card.front)
          .save(_deck.uid);
    }
  }

  Future<void> deleteCard() => _card.delete(_deck.uid);
}
