import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import 'stream_muxer.dart';

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

/// Subscribe to onChildAdded/Removed/Moved/Changed events (but not the onValue
/// event) of [query] and mux them into appropriate [DatabaseListEvent], parsing
/// values with [snapshotParser].
Stream<DatabaseListEvent<T>> childEventsStream<T>(
        Query query, T snapshotParser(DataSnapshot s)) =>
    StreamMuxer<ListEventType>({
      ListEventType.itemAdded: query.onChildAdded,
      ListEventType.itemRemoved: query.onChildRemoved,
      ListEventType.itemMoved: query.onChildMoved,
      ListEventType.itemChanged: query.onChildChanged,
    }).map((muxerEvent) {
      Event dbEvent = muxerEvent.value;
      return DatabaseListEvent(
        eventType: muxerEvent.stream,
        value: snapshotParser(dbEvent.snapshot),
        previousSiblingKey: dbEvent.previousSiblingKey,
      );
    });
