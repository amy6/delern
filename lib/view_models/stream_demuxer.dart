import 'dart:async';

class StreamDemuxer extends Stream<Map> {
  final Map<dynamic, Stream> streams;
  final bool triggerEmpty;

  StreamController<Map> _controller;
  Map<dynamic, StreamSubscription> _subscriptions;
  Map _events = new Map();

  StreamDemuxer(this.streams, [this.triggerEmpty = true]) {
    _controller = new StreamController<Map>(
      // TODO(dotdoom): support onPause / onResume.
      onCancel: _onCancel,
      onListen: _onListen,
    );
  }

  @override
  StreamSubscription<Map> listen(void Function(Map event) onData,
      {Function onError, void Function() onDone, bool cancelOnError}) {
    return _controller.stream.listen(onData,
        onError: onError, onDone: onDone, cancelOnError: cancelOnError);
  }

  void _onData(key, evt) {
    _events[key] = evt;
    _controller.add(_events);
  }

  void _onError(key, err) {
    _controller.addError(err);
  }

  void _onListen() {
    _subscriptions = streams.map((key, stream) => new MapEntry(
        key,
        stream.listen(
          (evt) => _onData(key, evt),
          onError: (err) => _onError(key, err),
        )));
    if (triggerEmpty) {
      _controller.add(_events);
    }
  }

  void _onCancel() {
    _subscriptions.forEach((_, sub) => sub.cancel());
    _controller.close();
  }
}
