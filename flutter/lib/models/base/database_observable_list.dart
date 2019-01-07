import 'dart:async';
import 'dart:core';

import 'package:delern_flutter/models/base/delayed_initialization.dart';
import 'package:delern_flutter/models/base/keyed_list_item.dart';
import 'package:delern_flutter/models/base/stream_muxer.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';
import 'package:observable/observable.dart';

/// Parse snapshot value into an object (value is usually a Map).
// Linter bug: https://github.com/dart-lang/linter/issues/1162.
// ignore: avoid_annotating_with_dynamic
typedef SnapshotParser<T> = T Function(String key, dynamic value);

enum _DatabaseEventType {
  itemAdded,
  itemRemoved,
  itemMoved,
  itemChanged,
}

/// A list maintaining up-to-date state with the database as long as there's at
/// least one listener on [listChanges] stream.
// TODO(dotdoom): how about removing KeyedListItem requirement and instead keep
//                track of the key internally to the list? Could help if the
//                models are mutable like they are right now.
class DatabaseObservableList<T extends KeyedListItem> extends ObservableList<T>
    with KeyedListMixin<T>
    implements DelayedInitializationObservableList<T> {
  /// Whether the list is ordered by the database. Currently broken by
  /// https://github.com/flutter/flutter/issues/19389.
  final bool ordered;

  /// A Future that completes when the list is completely populated with initial
  /// data. The list does not start populating unless [fetchFullValue] is called
  /// or [listChanges] are listened to, so waiting for this Future will block.
  /// If `fetchFullValueFirst` is not set to "true" in constructor parameters,
  /// this property is `null`.
  Future<void> get initializationComplete => _fullValueArrived?.future;
  Completer<void> _fullValueArrived;

  final SnapshotParser<T> _snapshotParser;
  final Query _query;
  final bool _fetchFullValueFirst;
  StreamSubscription<MapEntry<_DatabaseEventType, dynamic>>
      _childEventsSubscription;

  /// Create an instance that self-populates from [query] (assuming it fetches
  /// key->value pairs) and parses the values with [snapshotParser]. When
  /// [ordered] is true, it also subscribes to [Query.onChildMoved] event to
  /// track element moves in response to database updates. If
  /// [fetchFullValueFirst] is set, fetches full value via [Query.onValue] every
  /// time [listChanges] get the first observer.
  /// The list is completely inert if there are no observers on [listChanges].
  DatabaseObservableList({
    @required SnapshotParser<T> snapshotParser,
    @required Query query,
    this.ordered = true,
    bool fetchFullValueFirst = true,
  })  : assert(snapshotParser != null),
        assert(query != null),
        _snapshotParser = snapshotParser,
        _query = query,
        _fetchFullValueFirst = fetchFullValueFirst {
    if (_fetchFullValueFirst) {
      _fullValueArrived = Completer<void>();
    }
  }

  /// Fetch all data from the database regardless of the current state. When
  /// `fetchFullValueFirst` is set in the constructor, this method also
  /// completes [initializationComplete] future, if not completed yet.
  Future<void> fetchFullValue() async {
    Map fullValue = (await _query.onValue.first).snapshot.value ?? {};
    replaceRange(0, length,
        fullValue.entries.map((item) => _snapshotParser(item.key, item.value)));
    if (_fullValueArrived?.isCompleted == false) {
      _fullValueArrived.complete();
    }
  }

  @override
  @protected
  void listObserved() async {
    super.listObserved();

    if (_fetchFullValueFirst) {
      await fetchFullValue();
    } else {
      // If we already hold a value that we fetched for previous observers, it
      // is possible that some elements are no longer in a database. We will not
      // receive onChildRemoved event for them, and we also do not update
      // existing items on onChildAdded events, so the data, except new items,
      // will be stale.
      clear();
    }

    final updateStreams = {
      _DatabaseEventType.itemAdded: _query.onChildAdded,
      _DatabaseEventType.itemRemoved: _query.onChildRemoved,
      _DatabaseEventType.itemChanged: _query.onChildChanged,
    };
    if (ordered) {
      updateStreams[_DatabaseEventType.itemMoved] = _query.onChildMoved;
    }

    // Since we have "await" above, we might already have a subscription active
    // by the time we get here. There's very little harm (performance penalty)
    // in re-subscribing, but this should happen very rarely.
    _childEventsSubscription?.cancel();
    _childEventsSubscription = StreamMuxer<_DatabaseEventType>(updateStreams)
        .listen((muxerEvent) =>
            _handleChildEvent(muxerEvent.key, muxerEvent.value));
  }

  void _handleChildEvent(_DatabaseEventType eventType, Event event) {
    switch (eventType) {
      case _DatabaseEventType.itemAdded:
        if (indexOfKey(event.snapshot.key) < 0) {
          insert(indexOfKey(event.previousSiblingKey) + 1,
              _snapshotParser(event.snapshot.key, event.snapshot.value));
        }
        break;
      case _DatabaseEventType.itemRemoved:
        removeAt(indexOfKey(event.snapshot.key));
        break;
      case _DatabaseEventType.itemChanged:
        this[indexOfKey(event.snapshot.key)] =
            _snapshotParser(event.snapshot.key, event.snapshot.value);
        break;
      case _DatabaseEventType.itemMoved:
        // Not useful because of a bug:
        // https://github.com/flutter/flutter/issues/19389.
        // TODO(dotdoom): implement this method once Firebase bug is fixed.
        break;
    }
  }

  @override
  @protected
  void listUnobserved() {
    super.listUnobserved();
    _childEventsSubscription?.cancel();
    _childEventsSubscription = null;
  }
}
