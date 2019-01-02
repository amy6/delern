import 'dart:async';
import 'dart:core';

import 'package:delern_flutter/models/base/stream_muxer.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

enum ListEventType {
  itemAdded,
  itemRemoved,
  itemMoved,
  itemChanged,
  setAll,
}

@immutable
class DatabaseListEvent<T> {
  final ListEventType eventType;

  /// Parsed value of the snapshot. Can be null, for example in
  /// [ListEventType.itemRemoved] or [ListEventType.itemChanged] events.
  /// Mutually exclusive with [fullListValueForSet].
  final T value;

  /// The of the snapshot. Mutually exclusive with [fullListValueForSet].
  final String key;

  /// The key of the previous sibling, only useful for ordered queries. Mutually
  /// exclusive with [fullListValueForSet].
  final String previousSiblingKey;

  /// Set only for [ListEventType.setAll] event (initial data arrival). Empty
  /// when the query returns no value.
  final Iterable<T> fullListValueForSet;

  const DatabaseListEvent._({
    @required this.eventType,
    this.key,
    this.previousSiblingKey,
    this.value,
    this.fullListValueForSet,
  });

  String toString() => '$eventType #$key ($value)';
}

// Linter bug: https://github.com/dart-lang/linter/issues/1162.
// ignore: avoid_annotating_with_dynamic
typedef SnapshotParser<T> = T Function(String key, dynamic value);

/// Subscribe to onChildAdded/Removed/Moved/Changed events (but not the onValue
/// event) of [query] and mux them into appropriate [DatabaseListEvent], parsing
/// values with [snapshotParser]. When [ordered] is set, it also respects the
/// order changes, subscribing to [Query.onChildMoved].
// TODO(dotdoom): do not parse snapshots here, that's too wasteful. Do it later
//                in the list when we know that we really need objects (i.e. on
//                reassignment most of the values will be wasted.
Stream<DatabaseListEvent<T>> childEventsStream<T>(
    Query query, SnapshotParser snapshotParser,
    {bool ordered = true}) {
  final subscriptions = {
    ListEventType.itemAdded: query.onChildAdded,
    ListEventType.itemRemoved: query.onChildRemoved,
    ListEventType.itemChanged: query.onChildChanged,
  };
  if (ordered) {
    subscriptions[ListEventType.itemMoved] = query.onChildMoved;
  }
  return StreamMuxer<ListEventType>(subscriptions).map((muxerEvent) {
    Event dbEvent = muxerEvent.value;
    return DatabaseListEvent._(
      eventType: muxerEvent.key,
      key: dbEvent.snapshot.key,
      value: snapshotParser(dbEvent.snapshot.key, dbEvent.snapshot.value),
      previousSiblingKey: dbEvent.previousSiblingKey,
    );
  });
}

Stream<DatabaseListEvent<T>> fullThenChildEventsStream<T>(
    Query query, SnapshotParser snapshotParser,
    {bool ordered = true}) async* {
  Map initialValue = (await query.onValue.first).snapshot.value ?? {};
  yield DatabaseListEvent._(
      eventType: ListEventType.setAll,
      fullListValueForSet: initialValue.entries
          .map((item) => snapshotParser(item.key, item.value)));
  yield* childEventsStream(query, snapshotParser, ordered: ordered);
}
