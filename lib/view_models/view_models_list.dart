import 'dart:async';

import 'package:meta/meta.dart';

import '../models/keyed_list.dart';
import '../models/observable_list.dart';
import 'activatable.dart';

abstract class ViewModel implements KeyedListItem, Activatable {
  String get key;
  ViewModel updateWith(covariant ViewModel value);
}

typedef Stream<T> StreamGetter<T>();

class ViewModelsList<T extends ViewModel> extends ObservableList<T>
    with KeyedListMixin<T>
    implements Activatable {
  final StreamGetter<KeyedListEvent<T>> _stream;
  StreamSubscription<KeyedListEvent<T>> _subscription;

  ViewModelsList(this._stream);

  @override
  void activate() {
    if (_subscription == null) {
      forEach((item) => item.activate());
      _subscription = _stream().listen(_processListEvent);
    }
  }

  void childUpdated(T child) {
    _processListEvent(new KeyedListEvent(
      eventType: ListEventType.itemChanged,
      value: child,
    ));
  }

  void _processListEvent(KeyedListEvent<T> event) {
    switch (event.eventType) {
      case ListEventType.itemAdded:
        // With Firebase, we subscribe to onValue, which delivers all data,
        // and then onChild* events, which are also initially delivered for
        // every child. We must therefore skip keys that we already got.
        var index = indexOfKey(event.value.key);
        if (index < 0) {
          _addWithKey(event.previousSiblingKey, event.value);
        } else {
          assert(event.previousSiblingKey == null ||
              indexOfKey(event.previousSiblingKey) >= 0);
        }
        break;
      case ListEventType.itemRemoved:
        _removeAt(indexOfKey(event.value.key));
        break;
      case ListEventType.itemChanged:
        // With Firebase, some events may be delivered twice - by different
        // listeners. E.g. "remove(X)" then "change(X -> null)", in which case
        // the item will no longer exist by the time "change" arrives.
        var index = indexOfKey(event.value.key);
        if (index >= 0) {
          super.setAt(index, this[index].updateWith(event.value)..activate());
        }
        break;
      case ListEventType.itemMoved:
        super.move(indexOfKey(event.value.key),
            indexOfKey(event.previousSiblingKey) + 1);
        break;
      case ListEventType.set:
        _setAll(event.fullListValueForSet ?? []);
        break;
    }
  }

  T _addWithKey(String previousKey, T value) {
    super.insert(indexOfKey(previousKey) + 1, value..activate());
    return value;
  }

  void _removeAt(int index) {
    this[index].deactivate();
    super.removeAt(index);
  }

  @override
  @mustCallSuper
  void deactivate() {
    _subscription?.cancel();
    _subscription = null;
    forEach((item) => item.deactivate());
  }

  void _setAll(Iterable<T> newValue) {
    if (isEmpty) {
      // Shortcut (also beneficial for the UI).
      super.setAll(0, newValue..forEach((e) => e.activate()));
      return;
    }

    // setAll is called when we receive onValue, which can be initial
    // data or an update after deactivate + activate cycle.
    // For the update, we have to merge, retaining as much of the existing
    // data as possible, so that we display some (maybe stale) data to the
    // user and then update it as soon as the new data arrives.

    for (var index = length - 1; index >= 0; --index) {
      if (!newValue.any((e) => e.key == this[index].key)) {
        _removeAt(index);
      }
    }

    var previousKey;
    for (var element in newValue) {
      var index = indexOfKey(element.key);
      if (index < 0) {
        _addWithKey(previousKey, element).activate();
      } else {
        super.setAt(index, this[index].updateWith(element)..activate());
      }
      previousKey = element.key;
    }
  }

  @override
  void insert(int index, T value) {
    _throwReadOnly();
  }

  @override
  void move(int index1, int index2) {
    _throwReadOnly();
  }

  @override
  void setAll(int index, Iterable<T> value) {
    _throwReadOnly();
  }

  @override
  void setAt(int index, T value) {
    _throwReadOnly();
  }

  @override
  T removeAt(int index) {
    _throwReadOnly();
    return null;
  }

  void _throwReadOnly() {
    throw new UnsupportedError('ViewModelsList interface is read-only');
  }
}
