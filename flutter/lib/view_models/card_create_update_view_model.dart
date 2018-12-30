import 'dart:async';

import 'package:meta/meta.dart';

import '../models/base/transaction.dart';
import '../models/card.dart';
import '../models/scheduled_card.dart';
import '../remote/analytics.dart';

class CardCreateUpdateViewModel {
  static Future<void> saveCard(
      {@required CardModel card,
      @required String uid,
      bool addReverse = false}) {
    logCardCreate(card.deckKey);

    var t = Transaction()..save(card);
    final sCard =
        ScheduledCardModel(key: card.key, deckKey: card.deckKey, uid: uid);
    t.save(sCard);

    if (addReverse) {
      var reverse = CardModel.copyFrom(card)
        ..key = null
        ..front = card.back
        ..back = card.front;
      t.save(reverse);
      var reverseScCard = ScheduledCardModel(
          key: reverse.key, deckKey: reverse.deckKey, uid: uid);
      t.save(reverseScCard);
    }
    return t.commit();
  }
}
