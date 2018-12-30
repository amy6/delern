import 'dart:async';

import 'package:delern_flutter/models/base/transaction.dart';
import 'package:delern_flutter/models/card.dart';
import 'package:delern_flutter/models/scheduled_card.dart';
import 'package:delern_flutter/remote/analytics.dart';
import 'package:meta/meta.dart';

class CardCreateUpdateViewModel {
  static Future<void> saveCard(
      {@required CardModel card,
      @required String uid,
      bool addReverse = false}) {
    logCardCreate(card.deckKey);

    var t = Transaction()..save(card);
    final sCard = ScheduledCardModel(deckKey: card.deckKey, uid: uid)
      ..key = card.key;
    t.save(sCard);

    if (addReverse) {
      var reverse = CardModel.copyFrom(card)
        ..key = null
        ..front = card.back
        ..back = card.front;
      t.save(reverse);
      var reverseScCard = ScheduledCardModel(deckKey: reverse.deckKey, uid: uid)
        ..key = reverse.key;
      t.save(reverseScCard);
    }
    return t.commit();
  }
}
