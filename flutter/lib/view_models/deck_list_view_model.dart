import 'dart:async';

import 'package:meta/meta.dart';

import '../models/base/transaction.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../remote/analytics.dart';
import 'base/disposable.dart';
import 'base/proxy_keyed_list.dart';
import 'base/view_models_list.dart';

class DeckListViewModel implements Disposable {
  final String uid;

  ViewModelsList<DeckModel> _deckViewModels;
  ProxyKeyedList<DeckModel> _decksProxy;

  ProxyKeyedList<DeckModel> get decks =>
      _decksProxy ??= ProxyKeyedList(_deckViewModels);

  DeckListViewModel(this.uid) {
    _deckViewModels = ViewModelsList<DeckModel>(() => Deck.getDecks(uid).map(
        (deckEvent) =>
            deckEvent.map((deck) => DeckModel.copyFromLegacy(deck))));
  }

  @override
  @mustCallSuper
  void dispose() {
    _deckViewModels.dispose();
    _decksProxy?.dispose();
  }

  static Future<void> createDeck(Deck deck, String email) {
    logDeckCreate();
    return (Transaction()
          ..save(deck..access = AccessType.owner)
          ..save(
              DeckAccess(deck: deck, access: AccessType.owner, email: email)))
        .commit();
  }
}
