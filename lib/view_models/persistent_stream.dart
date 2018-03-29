import 'dart:async';

typedef Stream<T> StreamGetter<T>();

// TODO(dotdoom): tests
class PersistentStream<T> extends Stream<T> {
  final Duration dispositionDelay;
  final StreamGetter<T> source;

  StreamController<T> _controller;

  bool _latestEventAvailable;
  T _latestEvent;

  StreamSubscription<T> _sourceSubscription;
  Timer _sourceSubscriptionCancelTimer;

  PersistentStream(this.source,
      {this.dispositionDelay = const Duration(minutes: 2)}) {
    assert(source != null);
  }

  @override
  StreamSubscription<T> listen(void onData(T event),
      {Function onError, void onDone(), bool cancelOnError}) {
    assert(_controller == null, 'Invalid state: stream is already listened to');

    _controller = new StreamController<T>(
      // Although _onListen will be called immediately afterwards (within
      // _controller.stream.listen() statement below), it logically splits
      // well into a separate method.
      onListen: _onListen,
      onCancel: _onCancel,
      // TODO(dotdoom): add onPause / onResume.
    );

    return _controller.stream.listen(onData,
        onError: onError, onDone: onDone, cancelOnError: cancelOnError);
  }

  /// This method can only be called from within the listen() method above.
  void _onListen() {
    if (_sourceSubscription != null) {
      _sourceSubscriptionCancelTimer.cancel();
      _sourceSubscriptionCancelTimer = null;
      if (_latestEventAvailable) {
        // TODO(dotdoom): what about latestError?
        _controller.add(_latestEvent);
      }
      return;
    }

    // We create a new stream each time because the stream only survives one
    // subscription.
    _sourceSubscription = source().listen(
      (T event) {
        _latestEventAvailable = true;
        _latestEvent = event;
        if (_sourceSubscriptionCancelTimer == null) {
          _controller.add(event);
        }
      },
      onError: (error) {
        if (_sourceSubscriptionCancelTimer == null) {
          _controller.addError(error);
        }
      },
      onDone: _onSourceDone,
    );
  }

  /// Subscription is being abandoned.
  void _onCancel() {
    _controller.close();
    _controller = null;

    _sourceSubscriptionCancelTimer =
        new Timer(dispositionDelay, _cleanupSourceSubscription);
  }

  /// Underlying stream has ended or an abandoned subscription has expired.
  void _onSourceDone() {
    _controller.close();
    _controller = null;

    _cleanupSourceSubscription();
  }

  void _cleanupSourceSubscription() {
    _sourceSubscription.cancel();
    _sourceSubscription = null;

    if (_sourceSubscriptionCancelTimer != null) {
      // In case the underlying stream was done while we were keeping the
      // subscription around.
      _sourceSubscriptionCancelTimer.cancel();
      _sourceSubscriptionCancelTimer = null;
    }

    _latestEvent = null;
    _latestEventAvailable = false;
  }
}
