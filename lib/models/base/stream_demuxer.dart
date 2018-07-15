import 'dart:async';

class StreamDemuxerEvent<T> implements Error {
  final T stream;
  final dynamic value;

  StreamDemuxerEvent(this.stream, this.value);

  @override
  String toString() {
    return '[muxed stream "$stream"]: $value';
  }

  // Forward to 'value' to provide stack trace to error reporting facilities.
  @override
  StackTrace get stackTrace {
    if (value is Error) {
      return value.stackTrace;
    }
    return null;
  }
}

class StreamDemuxer<T> extends Stream<StreamDemuxerEvent<T>> {
  final Map<T, Stream> streams;

  StreamController<StreamDemuxerEvent<T>> _controller;
  Map<T, StreamSubscription> _subscriptions;

  StreamDemuxer(this.streams) {
    _controller = new StreamController<StreamDemuxerEvent<T>>(
      onCancel: _onCancel,
      onListen: _onListen,
      onPause: () => _subscriptions.values.forEach((s) => s.pause()),
      onResume: () => _subscriptions.values.forEach((s) => s.resume()),
    );
  }

  @override
  StreamSubscription<StreamDemuxerEvent<T>> listen(
      void Function(StreamDemuxerEvent<T> event) onData,
      {Function onError,
      void Function() onDone,
      bool cancelOnError}) {
    return _controller.stream.listen(onData,
        onError: onError, onDone: onDone, cancelOnError: cancelOnError);
  }

  void _onListen() {
    _subscriptions = streams.map((key, stream) => new MapEntry(
        key,
        stream.listen(
          (evt) => _controller.add(new StreamDemuxerEvent<T>(key, evt)),
          // TODO(dotdoom): should we cancel only when all of them are done?
          onDone: _onCancel,
          onError: (err) =>
              _controller.addError(new StreamDemuxerEvent<T>(key, err)),
        )));
  }

  void _onCancel() {
    _subscriptions.values.forEach((sub) => sub.cancel());
    _controller.close();
  }
}
