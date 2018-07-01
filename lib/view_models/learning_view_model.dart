import 'dart:async';

import '../models/card.dart';
import '../models/deck.dart';
import '../models/scheduled_card.dart';
import '../models/base/stream_demuxer.dart';

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
}
