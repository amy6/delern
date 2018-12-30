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
  final T value;
  final String previousSiblingKey;
  final Iterable<T> fullListValueForSet;

  const DatabaseListEvent({
    @required this.eventType,
    this.previousSiblingKey,
    this.value,
    this.fullListValueForSet,
  });

  String toString() => '$eventType #$previousSiblingKey ($value)';
}

// Linter bug: https://github.com/dart-lang/linter/issues/1162.
// ignore: avoid_annotating_with_dynamic
typedef SnapshotParser<T> = T Function(String key, dynamic value);

/// Subscribe to onChildAdded/Removed/Moved/Changed events (but not the onValue
/// event) of [query] and mux them into appropriate [DatabaseListEvent], parsing
/// values with [snapshotParser].
Stream<DatabaseListEvent<T>> childEventsStream<T>(
        Query query, SnapshotParser snapshotParser) =>
    StreamMuxer<ListEventType>({
      ListEventType.itemAdded: query.onChildAdded,
      ListEventType.itemRemoved: query.onChildRemoved,
      ListEventType.itemMoved: query.onChildMoved,
      ListEventType.itemChanged: query.onChildChanged,
    }).map((muxerEvent) {
      Event dbEvent = muxerEvent.value;
      return DatabaseListEvent(
        eventType: muxerEvent.key,
        value: snapshotParser(dbEvent.snapshot.key, dbEvent.snapshot.value),
        previousSiblingKey: dbEvent.previousSiblingKey,
      );
    });

Stream<DatabaseListEvent<T>> fullThenChildEventsStream<T>(
    Query query, SnapshotParser snapshotParser) async* {
  Map initialValue = (await query.onValue.first).snapshot.value ?? {};
  yield DatabaseListEvent(
      eventType: ListEventType.setAll,
      fullListValueForSet: initialValue.entries
          .map((item) => snapshotParser(item.key, item.value)));
  yield* childEventsStream(query, snapshotParser);
}
