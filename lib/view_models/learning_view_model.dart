import 'dart:async';

import '../models/card.dart';
import '../models/deck.dart';
import '../models/base/stream_demuxer.dart';
import '../models/scheduled_card.dart';

class LearningViewModel {
  Card _card;
  Card get card => _card;

  final Deck deck;

  LearningViewModel(this.deck);

  Stream<void> get updates => StreamDemuxer({
        0: deck.updates,
        1: ScheduledCard.next(deck.key, deck.uid).transform(StreamTransformer
            .fromHandlers(handleData: (sc, EventSink<void> sink) async {
          if (sc.key == null) {
            // No more learning!
            // TODO(dotdoom): does this close the original stream, too?
            sink.close();
            return;
          }

          _card = await Card.fetch(deck.key, sc.cardId);
          if (_card.key == null) {
            // TODO(dotdoom): delete dangling 'learning'. Also brings next.
          } else {
            sink.add(null);
          }
        }))
      });
}
