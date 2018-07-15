import 'dart:async';

import 'package:test/test.dart';

import '../lib/models/base/stream_muxer.dart';

void main() {
  test('empty stream', () async {
    var muxer = new StreamMuxer({
      'test': new Stream.empty(),
    });

    expect(await muxer.isEmpty, true);
  });

  test('error stack trace forwarding', () {
    var muxer = new StreamMuxer({
      'test': () async* {
        throw Error();
      }(),
    });

    expect(muxer.first, throwsA((e) => e.stackTrace != null));
  });

  test('error stack trace null when not error', () {
    var muxer = new StreamMuxer({
      'test': () async* {
        throw 'nothing';
      }(),
    });

    expect(
        muxer.first,
        throwsA((e) =>
            e.stackTrace == null &&
            e.toString() == '[muxed stream "test"]: nothing'));
  });
}
