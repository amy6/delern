import 'dart:async';

import 'package:delern_flutter/view_models/base/database_list_event_processor.dart';
import 'package:delern_flutter/view_models/base/keyed_list_event_processor.dart';

import '../models/base/transaction.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../remote/analytics.dart';

class DeckListViewModel {
  final String uid;

  DeckListViewModel(this.uid) {
    _deckProcessor =
        DatabaseListEventProcessor<DeckModel>(() => DeckModel.getDecks(uid));
  }

  KeyedListEventProcessor<DeckModel, dynamic> _deckProcessor;

  KeyedListEventProcessor<DeckModel, dynamic> get deckProcessor =>
      _deckProcessor;

  static Future<void> createDeck(Deck deck, String email) {
    logDeckCreate();
    return (Transaction()
          ..save(deck..access = AccessType.owner)
          ..save(
              DeckAccess(deck: deck, access: AccessType.owner, email: email)))
        .commit();
  }
}
