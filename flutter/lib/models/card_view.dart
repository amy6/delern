import 'dart:core';

import 'package:meta/meta.dart';

import 'base/model.dart';
import 'card.dart';

class CardViewModel implements Model {
  String key;
  String uid;
  int levelBefore;
  bool reply;
  DateTime timestamp;

  CardModel card;

  CardViewModel(
      {@required this.uid,
      @required this.card,
      this.levelBefore,
      this.reply,
      this.timestamp})
      : assert(card != null) {
    timestamp ??= DateTime.now();
  }

  @override
  String get rootPath => 'views/$uid/${card.deckKey}/${card.key}';

  @override
  Map<String, dynamic> toMap(bool isNew) => {
        'views/$uid/${card.deckKey}/${card.key}/$key': {
          'levelBefore': 'L$levelBefore',
          'reply': reply ? 'Y' : 'N',
          'timestamp': timestamp.toUtc().millisecondsSinceEpoch,
        },
      };
}
