import 'dart:async';

import '../models/base/stream_muxer.dart';
import '../models/base/transaction.dart';
import '../models/card.dart';
import '../models/scheduled_card.dart';

class CardViewModel {
  Card get card => _card;
  Card _card;

  CardViewModel(this._card) : assert(card != null);

  Stream<void> get updates => _card.key == null
      ? _card.deck.updates
      : StreamMuxer({
          0: _card.deck.updates,
          1: _card.updates,
        });

  @override
  String toString() => (_card.key == null ? _card.deck : _card).toString();

  Future<void> saveCard(bool addReverse) {
    var t = Transaction();
    t.save(_card);
    t.save(ScheduledCard(_card));
    if (addReverse) {
      var reverse = Card(
        _card.deck,
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
