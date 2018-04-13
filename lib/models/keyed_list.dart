import 'dart:async';
import 'dart:core';

import 'package:meta/meta.dart';
import 'package:firebase_database/firebase_database.dart';

import 'observable_list.dart';
import 'stream_demuxer.dart';

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

  String toString() {
    return '$eventType #$previousSiblingKey ($value)';
  }
}

abstract class KeyedListMixin<T extends KeyedListItem> implements List<T> {
  int indexOfKey(String key) => indexWhere((item) => item.key == key);
}

Stream<KeyedListEvent<T>> childEventsStream<T extends KeyedListItem>(
    Query query, T snapshotParser(DataSnapshot s)) {
  return new StreamDemuxer<ListEventType>({
    ListEventType.itemAdded: query.onChildAdded,
    ListEventType.itemRemoved: query.onChildRemoved,
    ListEventType.itemMoved: query.onChildMoved,
    ListEventType.itemChanged: query.onChildChanged,
  }).map((demuxerEvent) {
    Event dbEvent = demuxerEvent.value;
    return new KeyedListEvent(
      eventType: demuxerEvent.stream,
      value: snapshotParser(dbEvent.snapshot),
      previousSiblingKey: dbEvent.previousSiblingKey,
    );
  });
}
