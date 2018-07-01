import 'dart:async';

import 'package:meta/meta.dart';

import '../models/deck_access.dart';
import '../models/user.dart';
import '../models/deck.dart';
import 'base/activatable.dart';
import 'base/proxy_keyed_list.dart';
import 'base/view_models_list.dart';

class DeckAccessViewModel implements ListItemViewModel {
  String get key => _deckAccess.key;
  AccessType get access => _deckAccess.access;
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
  String toString() {
    return '#$key $access $_user';
  }
}

class DeckAccessesViewModel implements Activatable {
  final Deck deck;

  ViewModelsList<DeckAccessViewModel> _deckAccessViewModels;
  ProxyKeyedList<DeckAccessViewModel> _deckAccessesProxy;

  ProxyKeyedList<DeckAccessViewModel> get deckAccesses =>
      _deckAccessesProxy ??= new ProxyKeyedList(_deckAccessViewModels);

  DeckAccessesViewModel(this.deck) {
    _deckAccessViewModels = new ViewModelsList<DeckAccessViewModel>(() =>
        DeckAccess.getDeckAccesses(deck).map((deckAccessEvent) =>
            deckAccessEvent.map((deckAccess) =>
                new DeckAccessViewModel(_deckAccessViewModels, deckAccess))));
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
}
