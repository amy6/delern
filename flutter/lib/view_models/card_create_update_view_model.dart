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
    final sCard = ScheduledCardModel(card: card, uid: uid);

    var t = Transaction()..save(card)..save(sCard);
    if (addReverse) {
      var reverse = CardModel.copyFrom(card)
        ..front = card.back
        ..back = card.front;
      var reverseScCard = ScheduledCardModel(card: reverse, uid: uid);
      t..save(reverse)..save(reverseScCard);
    }
    return t.commit();
  }
}
