import 'dart:async';

import 'package:delern_flutter/models/base/stream_muxer.dart';
import 'package:test/test.dart';

void main() {
  test('empty stream', () async {
    final muxer = StreamMuxer({
      'test': const Stream.empty(),
    });

    expect(await muxer.isEmpty, true);
  });

  test('works with multiple streams', () async {
    final muxer = StreamMuxer({
      'foo': () async* {
        yield 'bar';
        yield 'baz';
      }(),
      1: () async* {
        yield 42.0;
      }(),
    });

    final events = await muxer.toList();
    expect(events[0].key, 'foo');
    expect(events[0].value, 'bar');
    expect(events[1].key, 1);
    expect(events[1].value, 42.0);
    expect(events[2].toString(), 'MapEntry(foo: baz)');
  });

  test('processes errors', () async {
    final muxer = StreamMuxer({
      'test': () async* {
        throw Exception('nope!');
      }(),
    });

    var caught = false;
    try {
      await muxer.first;
    } on MapEntry catch (e, stackTrace) {
      expect(stackTrace, isNotNull);
      expect(e.key, 'test');
      expect(e.value.toString(), 'Exception: nope!');
      caught = true;
    }
    expect(caught, true, reason: 'Expected exception not raised');
  });

  test('works with pause / resume', () async {
    final muxer = StreamMuxer({
      'numbers': () async* {
        yield 0;
        // pause() is called here.
        yield 1;
        yield 2;
      }(),
    });

    final data = [];
    StreamSubscription subscription;
    subscription = muxer.listen((event) {
      data.add(event.value);
      if (event.value == 0) {
        subscription.pause();
      }
    });

    await Future<void>(() {});
    expect(data.length, 1);
    expect(data[0], 0);

    subscription.resume();

    await Future<void>(() {});
    expect(data.length, 3);
    expect(data[1], 1);
    expect(data[2], 2);

    subscription.cancel();
  });
}
