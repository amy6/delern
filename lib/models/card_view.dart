import 'dart:core';

import 'base/model.dart';
import 'card.dart';

class CardView implements Model {
  String key;
  int levelBefore;
  bool reply;
  DateTime timestamp;

  Card card;
  // TODO(dotdoom): User.
  String uid;

  CardView(this.card, this.uid,
      {this.levelBefore, this.reply, this.timestamp}) {
    timestamp ??= DateTime.now();
  }

  @override
  String get rootPath => 'views/$uid/${card.deckId}/${card.key}';

  @override
  Map<String, dynamic> toMap(bool isNew) => {
        'views/$uid/${card.deckId}/${card.key}/$key': {
          'levelBefore': 'L$levelBefore',
          'reply': reply ? 'Y' : 'N',
          'timestamp': timestamp.toUtc().millisecondsSinceEpoch,
        },
      };
}
