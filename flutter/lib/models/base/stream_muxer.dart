import 'dart:async';

class StreamMuxer<T> extends Stream<MapEntry<T, dynamic>> {
  final Map<T, Stream> streams;

  StreamController<MapEntry<T, dynamic>> _controller;
  Map<T, StreamSubscription> _subscriptions;

  StreamMuxer(this.streams) {
    _controller = StreamController<MapEntry<T, dynamic>>(
      onCancel: _onCancel,
      onListen: _onListen,
      onPause: () => _subscriptions.values.forEach((s) => s.pause()),
      onResume: () => _subscriptions.values.forEach((s) => s.resume()),
    );
  }

  @override
  StreamSubscription<MapEntry<T, dynamic>> listen(
          void Function(MapEntry<T, dynamic> event) onData,
          {Function onError,
          void Function() onDone,
          bool cancelOnError}) =>
      _controller.stream.listen(onData,
          onError: onError, onDone: onDone, cancelOnError: cancelOnError);

  void _onListen() {
    _subscriptions = streams.map((key, stream) => MapEntry(
        key,
        stream.listen(
          (evt) => _controller.add(MapEntry(key, evt)),
          // TODO(dotdoom): should we cancel only when all of them are done?
          onDone: _onCancel,
          onError: (err, stackTrace) =>
              _controller.addError(MapEntry(key, err), stackTrace),
        )));
  }

  void _onCancel() {
    _subscriptions.values.forEach((sub) => sub.cancel());
    _controller.close();
  }
}
