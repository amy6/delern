import 'dart:async';

import '../models/base/stream_muxer.dart';
import '../models/base/transaction.dart';
import '../models/card.dart';
import '../models/deck.dart';
import '../models/scheduled_card.dart';

class CardViewModel {
  Deck get deck => _deck;
  Card get card => _card;

  final Deck _deck;
  Card _card;

  CardViewModel(this._deck, [this._card]) {
    _card ??= Card(_deck);
  }

  Stream<void> get updates => _card.key == null
      ? _deck.updates
      : StreamMuxer({
          0: _deck.updates,
          1: _card.updates,
        });

  @override
  String toString() => (_card.key == null ? _deck : _card).toString();

  Future<void> saveCard(bool addReverse) {
    // TODO(dotdoom): this won't be necessary once we s/_deck/_card.deck/.
    assert(_card.deck.key == _deck.key);

    var t = Transaction();
    t.save(_card);
    t.save(ScheduledCard(_card));
    if (addReverse) {
      var reverse = Card(
        _deck,
        front: _card.back,
        back: _card.front,
      );
      t.save(reverse);
      t.save(ScheduledCard(reverse));
    }

    return t.commit();
  }

  Future<void> deleteCard() =>
      (Transaction()..delete(_card)..delete(ScheduledCard(_card))).commit();
}
