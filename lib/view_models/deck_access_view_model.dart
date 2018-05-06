import 'dart:async';

import 'package:meta/meta.dart';

import '../models/base/stream_demuxer.dart';
import '../models/deck_access.dart';
import '../models/user.dart';
import 'base/activatable.dart';
import 'base/proxy_keyed_list.dart';
import 'base/view_models_list.dart';

class DeckAccessViewModel implements ViewModel {
  String get key => _deckAccess?.key;
  AccessType get access => _deckAccess.access;
  User get user => _user;

  DeckAccess _deckAccess;
  User _user;

  final ViewModelsList<DeckAccessViewModel> _owner;
  StreamSubscription<StreamDemuxerEvent<String>> _internalUpdates;

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
    if (_internalUpdates != null) {
      // This item is already activated. This must normally be only a side
      // effect of updateWith -> childUpdated cycle.
      return;
    }

    _internalUpdates = new StreamDemuxer<String>({
      'user': _deckAccess.getUser(),
    }).listen((event) {
      switch (event.stream) {
        case 'user':
          this._user = event.value;
          break;
      }
      // Send event to the owner list so that it can find our index
      // and notify subscribers.
      _owner.childUpdated(this);
    });
  }

  @override
  @mustCallSuper
  void deactivate() {
    _internalUpdates?.cancel();
    _internalUpdates = null;
  }

  @override
  String toString() {
    return '#$key]';
  }
}

class DeckAccessesViewModel implements Activatable {
  final String deckId;

  ViewModelsList<DeckAccessViewModel> _deckAccessViewModels;
  ProxyKeyedList<DeckAccessViewModel> _deckAccessesProxy;

  ProxyKeyedList<DeckAccessViewModel> get deckAccesses =>
      _deckAccessesProxy ??= new ProxyKeyedList(_deckAccessViewModels);

  DeckAccessesViewModel(this.deckId) {
    _deckAccessViewModels = new ViewModelsList<DeckAccessViewModel>(() =>
        DeckAccess.getDeckAccesses(deckId).map((deckAccessEvent) =>
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
