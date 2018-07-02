import 'dart:async';

import '../models/deck.dart';
import '../models/card.dart';
import '../models/deck_access.dart';
import '../models/scheduled_card.dart';
import '../models/base/transaction.dart';
import '../models/base/stream_demuxer.dart';

class DeckViewModel {
  final Deck deck;
  // TODO(dotdoom): DeckAccess.
  AccessType _access;

  DeckViewModel(this.deck, this._access) {
    assert(deck != null);
    assert(_access != null);
  }

  Stream<void> get updates => StreamDemuxer({
        0: deck.updates,
        1: deck.getAccess().map((a) => _access = a),
      });

  Future<void> delete() {
    var t = Transaction();
    t.delete(deck);
    var card = Card(deck.key);
    if (_access == AccessType.owner) {
      t.deleteAll(card);
      // TODO(dotdoom): also delete deck from other users.
    }
    t.deleteAll(ScheduledCard(uid: deck.uid, card: card));
    // TODO(dotdoom): delete views.
    return t.commit();
  }
}
