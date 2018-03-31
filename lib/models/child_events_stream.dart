import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';

import 'observable_list.dart';
import 'keyed_event_list_mixin.dart';
import 'stream_demuxer.dart';

Stream<KeyedListEvent<T>> childEventsStream<T extends KeyedListItem>(
    Query query, T snapshotParser(DataSnapshot s)) {
  return new StreamDemuxer<ListEventType>({
    ListEventType.added: query.onChildAdded,
    ListEventType.removed: query.onChildRemoved,
    ListEventType.moved: query.onChildMoved,
    ListEventType.changed: query.onChildChanged,
  }).map((demuxerEvent) {
    Event dbEvent = demuxerEvent.value;
    return new KeyedListEvent(
      eventType: demuxerEvent.stream,
      value: snapshotParser(dbEvent.snapshot),
      previousSiblingKey: dbEvent.previousSiblingKey,
    );
  });
}
