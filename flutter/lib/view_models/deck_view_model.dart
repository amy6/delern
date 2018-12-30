import 'dart:async';

import '../models/base/transaction.dart';
import '../models/card.dart';
import '../models/card_view.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../models/scheduled_card.dart';
import '../remote/analytics.dart';

class DeckViewModel {
  final DeckModel deck;

  DeckViewModel(this.deck) : assert(deck != null);

  Future<void> delete() async {
    logDeckDelete(deck.key);
    var t = Transaction()..delete(deck);
    var card = CardModel(deckKey: deck.key);
    if (deck.access == AccessType.owner) {
      (await DeckAccessModel.getList(deckKey: deck.key).first)
          .fullListValueForSet
          .forEach((a) => t.delete(DeckModel(uid: a.key)..key = deck.key));
      t..deleteAll(DeckAccessModel(deckKey: deck.key))..deleteAll(card);
      // TODO(dotdoom): delete other users' ScheduledCard and Views?
    }
    t
      ..deleteAll(ScheduledCardModel(deckKey: deck.key, uid: deck.uid))
      ..deleteAll(
          CardViewModel(uid: deck.uid, deckKey: deck.key, cardKey: null));
    await t.commit();
  }

  Future<void> save() => (Transaction()..save(deck)).commit();
}
