import 'dart:async';

import 'package:meta/meta.dart';

import '../models/base/transaction.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../remote/analytics.dart';
import 'base/disposable.dart';
import 'base/proxy_keyed_list.dart';
import 'base/view_models_list.dart';

class DeckAccessesViewModel implements Disposable {
  final Deck deck;

  ViewModelsList<DeckAccess> _deckAccessViewModels;
  ProxyKeyedList<DeckAccess> _deckAccessesProxy;

  ProxyKeyedList<DeckAccess> get deckAccesses =>
      _deckAccessesProxy ??= ProxyKeyedList(_deckAccessViewModels);

  DeckAccessesViewModel(this.deck) {
    _deckAccessViewModels =
        ViewModelsList<DeckAccess>(() => DeckAccess.getDeckAccesses(deck));
  }

  @override
  @mustCallSuper
  void dispose() {
    _deckAccessViewModels.dispose();
    _deckAccessesProxy?.dispose();
  }

  static Future<void> shareDeck(DeckAccess access) async {
    logShare(access.deck.key);
    var tr = Transaction();

    if (access.access == null) {
      return (tr..delete(access)).commit();
    }

    tr.save(access);
    if ((await DeckAccess.fetch(access.deck, access.uid)).key == null) {
      // If there's no DeckAccess, assume the deck hasn't been shared yet.
      tr.save(Deck(
          uid: access.key,
          name: access.deck.name,
          accepted: false,
          markdown: access.deck.markdown,
          type: access.deck.type,
          category: access.deck.category,
          access: access.access)
        ..key = access.deck.key);
    } else {
      access.updateAccessFieldInDeck = true;
    }

    return tr.commit();
  }
}
