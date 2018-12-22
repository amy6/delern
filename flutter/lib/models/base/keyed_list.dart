import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import 'observable_list.dart';
import 'stream_muxer.dart';

abstract class KeyedListItem {
  String get key;
}

class KeyedListEvent<T extends KeyedListItem> {
  final ListEventType eventType;
  final T value;
  final String previousSiblingKey;
  final Iterable<T> fullListValueForSet;

  KeyedListEvent({
    @required this.eventType,
    this.previousSiblingKey,
    this.value,
    this.fullListValueForSet,
  });

  /// Map [value] or [fullListValueForSet] (whichever is applicable) of this
  /// event using [mapper], then pack them back into a new [KeyedListEvent] with
  /// the same parameters.
  KeyedListEvent<T2> map<T2 extends KeyedListItem>(T2 mapper(T value)) =>
      KeyedListEvent<T2>(
        eventType: eventType,
        previousSiblingKey: previousSiblingKey,
        value: value == null ? null : mapper(value),
        fullListValueForSet: fullListValueForSet?.map(mapper),
      );

  String toString() => '$eventType #$previousSiblingKey ($value)';
}

abstract class KeyedListMixin<T extends KeyedListItem> implements List<T> {
  int indexOfKey(String key) => indexWhere((item) => item.key == key);
}

/// Subscribe to onChildAdded/Removed/Moved/Changed events (but not the onValue
/// event) of [query] and mux them into appropriate [KeyedListEvent], parsing
/// values with [snapshotParser].
Stream<KeyedListEvent<T>> childEventsStream<T extends KeyedListItem>(
        Query query, T snapshotParser(DataSnapshot s)) =>
    StreamMuxer<ListEventType>({
      ListEventType.itemAdded: query.onChildAdded,
      ListEventType.itemRemoved: query.onChildRemoved,
      ListEventType.itemMoved: query.onChildMoved,
      ListEventType.itemChanged: query.onChildChanged,
    }).map((muxerEvent) {
      Event dbEvent = muxerEvent.value;
      return KeyedListEvent(
        eventType: muxerEvent.stream,
        value: snapshotParser(dbEvent.snapshot),
        previousSiblingKey: dbEvent.previousSiblingKey,
      );
    });
