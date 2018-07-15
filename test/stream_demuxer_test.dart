import 'dart:async';

import 'package:test/test.dart';

import '../lib/models/base/stream_demuxer.dart';

void main() {
  test('empty stream', () async {
    var demuxer = new StreamDemuxer({
      'test': new Stream.empty(),
    });

    expect(await demuxer.isEmpty, true);
  });

  test('error stack trace forwarding', () {
    var demuxer = new StreamDemuxer({
      'test': () async* {
        throw Error();
      }(),
    });

    expect(demuxer.first, throwsA((e) => e.stackTrace != null));
  });

  test('error stack trace null when not error', () {
    var demuxer = new StreamDemuxer({
      'test': () async* {
        throw 'nothing';
      }(),
    });

    expect(
        demuxer.first,
        throwsA((e) =>
            e.stackTrace == null &&
            e.toString() == '[muxed stream "test"]: nothing'));
  });
}
