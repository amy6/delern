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

// TODO(dotdoom): make list interface readonly.
class ViewModelsList<T extends ViewModel> extends ObservableList<T>
    with KeyedListMixin<T>
    implements Activatable {
  final StreamGetter<KeyedListEvent<T>> _stream;
  StreamSubscription<KeyedListEvent<T>> _subscription;

  ViewModelsList(this._stream);

  @override
  @mustCallSuper
  void activate() {
    if (_subscription == null) {
      forEach((item) => item.activate());
      _subscription = _stream().listen(_processListEvent);
    }
  }

  @override
  @mustCallSuper
  void deactivate() {
    _subscription?.cancel();
    _subscription = null;
    forEach((item) => item.deactivate());
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
        if (indexOfKey(event.value.key) < 0) {
          _add(event.previousSiblingKey, event.value);
        } else {
          // With Firebase, we subscribe to onValue, which delivers all data,
          // and then onChild* events, which are also initially delivered for
          // every child. We must therefore skip keys that we already got.
          assert(event.previousSiblingKey == null ||
              indexOfKey(event.previousSiblingKey) >= 0);
        }
        break;
      case ListEventType.itemRemoved:
        _remove(indexOfKey(event.value.key));
        break;
      case ListEventType.itemChanged:
        // With Firebase, some events may be delivered twice - by different
        // listeners. E.g. "remove(X)" then "change(X -> null)", in which case
        // the item will no longer exist by the time "change(...)" arrives.
        var index = indexOfKey(event.value.key);
        if (index >= 0) {
          _update(index, event.value);
        }
        break;
      case ListEventType.itemMoved:
        move(indexOfKey(event.value.key),
            indexOfKey(event.previousSiblingKey) + 1);
        break;
      case ListEventType.set:
        _setAll(event.fullListValueForSet);
        break;
    }
  }

  void _add(String previousKey, T value) =>
      insert(indexOfKey(previousKey) + 1, value..activate());

  void _remove(int index) {
    this[index].deactivate();
    removeAt(index);
  }

  void _update(int index, T newValue) =>
      setAt(index, this[index].updateWith(newValue)..activate());

  void _setAll(Iterable<T> newValue) {
    if (!changed) {
      // Shortcut (also beneficial for the UI).
      setAll(0, newValue);
      forEach((e) => e.activate());
      return;
    }

    // setAll is called when we receive onValue, which can be initial
    // data or an update after deactivate + activate cycle.
    // For the update, we have to merge, retaining as much of the existing
    // data as possible, so that we display some (maybe stale) data to the
    // user and then update it as soon as the new data arrives.

    for (var index = length - 1; index >= 0; --index) {
      if (!newValue.any((e) => e.key == this[index].key)) {
        _remove(index);
      }
    }

    var index = 0;
    for (var element in newValue) {
      var existingIndex = indexOfKey(element.key);
      if (existingIndex < 0) {
        insert(index, element..activate());
      } else {
        if (existingIndex != index) {
          assert(existingIndex > index,
              'ViewModelsList missed an item at re-arrangement');
          move(existingIndex, index);
        }
        _update(index, element);
      }
      ++index;
    }
  }
}
