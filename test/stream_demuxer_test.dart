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
}
