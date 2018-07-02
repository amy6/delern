import 'dart:async';

import '../models/deck.dart';
import '../models/card.dart';
import '../models/scheduled_card.dart';
import '../models/base/transaction.dart';

class DeckViewModel {
  final Deck deck;

  DeckViewModel(this.deck);

  Stream<void> get updates => deck.updates;

  Future<void> delete() {
    var t = Transaction();
    t.delete(deck);
    var card = Card(deck.key);
    t.deleteAll(card);
    t.deleteAll(ScheduledCard(uid: deck.uid, card: card));
    // TODO(dotdoom): delete views.
    return t.commit();
  }
}
