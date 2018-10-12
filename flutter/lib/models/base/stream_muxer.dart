import 'dart:async';

class StreamMuxerEvent<T> {
  final T stream;
  final dynamic value;

  StreamMuxerEvent(this.stream, this.value);

  @override
  String toString() => '[muxed stream "$stream"]: $value';
}

class StreamMuxer<T> extends Stream<StreamMuxerEvent<T>> {
  final Map<T, Stream> streams;

  StreamController<StreamMuxerEvent<T>> _controller;
  Map<T, StreamSubscription> _subscriptions;

  StreamMuxer(this.streams) {
    _controller = StreamController<StreamMuxerEvent<T>>(
      onCancel: _onCancel,
      onListen: _onListen,
      onPause: () => _subscriptions.values.forEach((s) => s.pause()),
      onResume: () => _subscriptions.values.forEach((s) => s.resume()),
    );
  }

  @override
  StreamSubscription<StreamMuxerEvent<T>> listen(
          void Function(StreamMuxerEvent<T> event) onData,
          {Function onError,
          void Function() onDone,
          bool cancelOnError}) =>
      _controller.stream.listen(onData,
          onError: onError, onDone: onDone, cancelOnError: cancelOnError);

  void _onListen() {
    _subscriptions = streams.map((key, stream) => MapEntry(
        key,
        stream.listen(
          (evt) => _controller.add(StreamMuxerEvent<T>(key, evt)),
          // TODO(dotdoom): should we cancel only when all of them are done?
          onDone: _onCancel,
          onError: (err, stackTrace) =>
              _controller.addError(StreamMuxerEvent<T>(key, err), stackTrace),
        )));
  }

  void _onCancel() {
    _subscriptions.values.forEach((sub) => sub.cancel());
    _controller.close();
  }
}
