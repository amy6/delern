import 'package:observable/observable.dart';

/// Interface for objects where initialization is done lazily, e.g. outside the
/// constructor or in response to an external event, such as a database event.
abstract class DelayedInitializationObject {
  /// Returns a one-off Future which completes when initialization of this
  /// object is complete. Returns null if this instance of the object can not
  /// determine whether its initialization is complete.
  Future<void> get initializationComplete;
}

abstract class DelayedInitializationObservableList<T>
    implements DelayedInitializationObject, ObservableList<T> {}
