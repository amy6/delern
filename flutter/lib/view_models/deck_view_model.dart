import 'dart:async';

import 'package:delern_flutter/models/base/transaction.dart';
import 'package:delern_flutter/models/card_model.dart';
import 'package:delern_flutter/models/card_reply_model.dart';
import 'package:delern_flutter/models/deck_access_model.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/models/scheduled_card_model.dart';
import 'package:delern_flutter/remote/analytics.dart';

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
          CardReplyModel(uid: deck.uid, deckKey: deck.key, cardKey: null));
    await t.commit();
  }

  Future<void> save() => (Transaction()..save(deck)).commit();
}
