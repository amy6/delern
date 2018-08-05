import 'dart:core';

import 'package:meta/meta.dart';

import 'base/model.dart';
import 'card.dart';

class CardView implements Model {
  String key;
  int levelBefore;
  bool reply;
  DateTime timestamp;

  Card card;

  CardView({@required this.card, this.levelBefore, this.reply, this.timestamp})
      : assert(card != null) {
    timestamp ??= DateTime.now();
  }

  @override
  String get rootPath => 'views/${card.deck.uid}/${card.deck.key}/${card.key}';

  @override
  Map<String, dynamic> toMap(bool isNew) => {
        'views/${card.deck.uid}/${card.deck.key}/${card.key}/$key': {
          'levelBefore': 'L$levelBefore',
          'reply': reply ? 'Y' : 'N',
          'timestamp': timestamp.toUtc().millisecondsSinceEpoch,
        },
      };
}
