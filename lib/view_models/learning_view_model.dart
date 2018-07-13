import 'dart:async';

import '../models/base/stream_demuxer.dart';
import '../models/base/transaction.dart';
import '../models/card.dart';
import '../models/card_view.dart';
import '../models/deck.dart';
import '../models/scheduled_card.dart';

class LearningViewModel {
  ScheduledCard _scheduledCard;
  Card get card => _scheduledCard?.card;

  final Deck deck;

  LearningViewModel(this.deck);

  Stream<void> get updates => StreamDemuxer({
        0: deck.updates,
        1: ScheduledCard
            .next(deck.key, deck.uid)
            .map((sc) => _scheduledCard = sc),
      });

  Future<void> answer(bool knows) {
    var cv = _scheduledCard.answer(knows);
    return (Transaction()..save(_scheduledCard)..save(cv)).commit();
  }

  //TODO(ksheremet): Throws error. Check it
  Future<void> deleteCard() => (Transaction()
        ..delete(card)
        ..delete(_scheduledCard)
        ..deleteAll(CardView(card, deck.uid)))
      .commit();
}
