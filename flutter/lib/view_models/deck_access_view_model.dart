import 'dart:async';

import 'package:meta/meta.dart';

import '../models/base/transaction.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../models/user.dart';
import '../remote/analytics.dart';
import 'base/activatable.dart';
import 'base/proxy_keyed_list.dart';
import 'base/view_models_list.dart';

class DeckAccessViewModel implements ListItemViewModel {
  String get key => _deckAccess.key;
  DeckAccess get deckAccess => _deckAccess;
  User get user => _user;

  DeckAccess _deckAccess;
  User _user;

  final ViewModelsList<DeckAccessViewModel> _owner;
  StreamSubscription<User> _userUpdates;

  DeckAccessViewModel(this._owner, this._deckAccess);

  @override
  DeckAccessViewModel updateWith(DeckAccessViewModel value) {
    if (identical(this, value)) {
      // This will happen when we sent an internal update event to the owner.
      return this;
    }

    assert(_deckAccess.key == value._deckAccess.key,
        'Attempting to absorb a deck with a different key');
    _deckAccess = value._deckAccess;
    return this;
  }

  @override
  @mustCallSuper
  void activate() {
    if (_userUpdates != null) {
      // This item is already activated. This must normally be only a side
      // effect of updateWith -> childUpdated cycle.
      return;
    }

    _userUpdates = _deckAccess.getUser().listen((user) {
      this._user = user;

      // Send event to the owner list so that it can find our index
      // and notify subscribers.
      _owner.childUpdated(this);
    });
  }

  @override
  @mustCallSuper
  void deactivate() {
    _userUpdates?.cancel();
    _userUpdates = null;
  }

  @override
  String toString() => '#$key ${_deckAccess.access} $_user';
}

class DeckAccessesViewModel implements Activatable {
  final Deck deck;

  ViewModelsList<DeckAccessViewModel> _deckAccessViewModels;
  ProxyKeyedList<DeckAccessViewModel> _deckAccessesProxy;

  ProxyKeyedList<DeckAccessViewModel> get deckAccesses =>
      _deckAccessesProxy ??= ProxyKeyedList(_deckAccessViewModels);

  DeckAccessesViewModel(this.deck) {
    _deckAccessViewModels = ViewModelsList<DeckAccessViewModel>(() =>
        DeckAccess.getDeckAccesses(deck).map((deckAccessEvent) =>
            deckAccessEvent.map((deckAccess) =>
                DeckAccessViewModel(_deckAccessViewModels, deckAccess))));
  }

  @override
  @mustCallSuper
  void deactivate() => _deckAccessViewModels.deactivate();

  @override
  @mustCallSuper
  void activate() {
    deactivate();
    _deckAccessViewModels.activate();
  }

  @mustCallSuper
  void dispose() {
    deactivate();
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
    }

    return tr.commit();
  }
}
