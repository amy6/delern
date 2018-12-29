import 'dart:async';

import '../models/base/transaction.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../remote/analytics.dart';
import 'base/database_list_event_processor.dart';
import 'base/keyed_list_event_processor.dart';

class DeckAccessesViewModel {
  final DeckModel deck;

  DeckAccessesViewModel(this.deck) {
    _deckAccessProcessor =
        DatabaseListEventProcessor(() => DeckAccessModel.getDeckAccesses(deck));
  }

  KeyedListEventProcessor<DeckAccessModel, dynamic> _deckAccessProcessor;

  KeyedListEventProcessor<DeckAccessModel, dynamic> get deckAccessProcessor =>
      _deckAccessProcessor;

  static Future<void> shareDeck(DeckAccessModel access) async {
    logShare(access.deck.key);
    var tr = Transaction();

    if (access.access == null) {
      return (tr..delete(access)).commit();
    }

    tr.save(access);
    if ((await DeckAccessModel.fetch(access.deck, access.uid)).key == null) {
      // If there's no DeckAccess, assume the deck hasn't been shared yet.
      tr.save(DeckModel()
        ..uid = access.key
        ..name = access.deck.name
        ..accepted = false
        ..markdown = access.deck.markdown
        ..type = access.deck.type
        ..category = access.deck.category
        ..access = access.access
        ..key = access.deck.key);
    } else {
      access.updateAccessFieldInDeck = true;
    }

    return tr.commit();
  }
}
