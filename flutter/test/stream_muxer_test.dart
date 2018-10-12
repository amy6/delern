import 'dart:async';

import 'package:delern_flutter/models/base/stream_muxer.dart';
import 'package:test/test.dart';

void main() {
  test('empty stream', () async {
    var muxer = StreamMuxer({
      'test': const Stream.empty(),
    });

    expect(await muxer.isEmpty, true);
  });

  test('works with a single stream', () async {
    final muxer = StreamMuxer({
      'test': () async* {
        yield 'passed';
      }(),
    });

    final first = await muxer.first;
    expect(first.stream, 'test');
    expect(first.value, 'passed');
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
    } on StreamMuxerEvent catch (e, stackTrace) {
      expect(stackTrace, isNotNull);
      expect(e.stream, 'test');
      expect(e.value.toString(), 'Exception: nope!');
      caught = true;
    }
    expect(caught, true, reason: 'Expected exception not raised');
  });
}
