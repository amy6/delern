import 'dart:async';

import '../models/base/transaction.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../remote/analytics.dart';
import 'base/database_list_event_processor.dart';
import 'base/keyed_list_event_processor.dart';

class DeckListViewModel {
  final String uid;

  DeckListViewModel(this.uid) {
    _deckProcessor =
        DatabaseListEventProcessor<DeckModel>(() => DeckModel.getDecks(uid));
  }

  KeyedListEventProcessor<DeckModel, dynamic> _deckProcessor;

  KeyedListEventProcessor<DeckModel, dynamic> get deckProcessor =>
      _deckProcessor;

  static Future<void> createDeck(DeckModel deck, String email) {
    logDeckCreate();
    return (Transaction()
          ..save(deck..access = AccessType.owner)
          ..save(DeckAccessModel(
              deck: deck, access: AccessType.owner, email: email)))
        .commit();
  }
}
