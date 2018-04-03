import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';

import 'observable_list.dart';
import 'keyed_event_list_mixin.dart';
import 'stream_demuxer.dart';

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
