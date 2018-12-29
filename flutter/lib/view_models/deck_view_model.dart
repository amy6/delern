import 'dart:async';

import '../models/base/transaction.dart';
import '../models/card.dart';
import '../models/card_view.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../models/scheduled_card.dart';
import '../remote/analytics.dart';

class DeckViewModel {
  final Deck deck;

  DeckViewModel(this.deck) : assert(deck != null);

  Stream<void> get updates => deck.updates;

  Future<void> delete() async {
    logDeckDelete(deck.key);
    var t = Transaction()..delete(deck);
    var card = Card(deck: deck);
    if (deck.access == AccessType.owner) {
      (await DeckAccess.getDeckAccesses(deck).first)
          .fullListValueForSet
          .forEach((a) => t.delete(Deck(uid: a.key)..key = deck.key));
      t..deleteAll(DeckAccess(deck: deck)..key = null)..deleteAll(card);
      // TODO(dotdoom): delete other users' ScheduledCard and Views?
    }
    t..deleteAll(ScheduledCard(card: card))..deleteAll(CardView(card: card));
    await t.commit();
  }

  Future<void> save() => (Transaction()..save(deck)).commit();
}
