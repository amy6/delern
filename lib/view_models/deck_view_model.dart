import 'dart:async';

import '../models/deck.dart';
import '../models/card.dart';
import '../models/deck_access.dart';
import '../models/scheduled_card.dart';
import '../models/base/transaction.dart';
import '../models/base/stream_demuxer.dart';

class DeckViewModel {
  final Deck deck;
  final DeckAccess access;

  DeckViewModel(this.deck, this.access) {
    assert(deck != null);
    assert(access != null);
  }

  Stream<void> get updates => StreamDemuxer({
        0: deck.updates,
        1: access.updates,
      });

  Future<void> delete() async {
    var t = Transaction();
    t.delete(deck);
    var card = Card(deck.key);
    if (access.access == AccessType.owner) {
      (await DeckAccess.getDeckAccesses(deck).first)
          .fullListValueForSet
          .forEach((a) => t.delete(Deck(a.key)..key = deck.key));
      t.deleteAll(DeckAccess(deck));
      t.deleteAll(card);
      // TODO(dotdoom): delete other users' ScheduledCard and Views?
    }
    t.deleteAll(ScheduledCard(uid: deck.uid, card: card));
    // TODO(dotdoom): delete views.
    await t.commit();
  }
}
