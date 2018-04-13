import 'dart:async';

import '../models/keyed_list.dart';
import '../models/observable_list.dart';
import 'attachable.dart';

abstract class ViewModel<T> implements KeyedListItem, Attachable<T> {
  String get key;
  ViewModel<T> updateWith(covariant ViewModel<T> value);
}

class ViewModelsList<T extends ViewModel<ViewModelsList<T>>>
    extends ObservableList<T>
    with KeyedListMixin<T>
    implements Attachable<Stream<KeyedListEvent<T>>> {
  StreamSubscription<KeyedListEvent<T>> _subscription;

  @override
  void attachTo(Stream<KeyedListEvent<T>> stream) {
    _subscription?.cancel();
    forEach((item) => item.attachTo(this));
    _subscription = stream.listen(_processEvent);
  }

  void childUpdated(T child) {
    _processEvent(new KeyedListEvent(
      eventType: ListEventType.itemChanged,
      value: child,
    ));
  }

  void _processEvent(KeyedListEvent<T> event) {
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
        removeAt(indexOfKey(event.value.key));
        break;
      case ListEventType.itemChanged:
        // With Firebase, some events may be delivered twice - by different
        // listeners. E.g. "remove(X)" then "change(X, null)", in which case
        // the item will no longer exist by the time "change" arrives.
        var index = indexOfKey(event.value.key);
        if (index >= 0) {
          setAt(indexOfKey(event.value.key), event.value);
        }
        break;
      case ListEventType.itemMoved:
        move(indexOfKey(event.value.key),
            indexOfKey(event.previousSiblingKey) + 1);
        break;
      case ListEventType.set:
        setAll(0, event.fullListValueForSet ?? []);
        break;
    }
  }

  T _addWithKey(String previousKey, T value) {
    insert(indexOfKey(previousKey) + 1, value);
    return value;
  }

  @override
  void detach() {
    _subscription?.cancel();
    _subscription = null;
    forEach((item) => item.detach());
  }

  @override
  void setAll(int index, Iterable<T> newValue) {
    if (index != 0) {
      throw new UnsupportedError('setAll can only set at index 0');
    }

    if (isEmpty) {
      // Shortcut (also beneficial for the UI).
      super.setAll(index, newValue..forEach((e) => e.attachTo(this)));
      return;
    }

    // setAll is called when we receive onValue, which can be initial
    // data or an update after detach + attachTo cycle.
    // For the update, we have to merge, retaining as much of the existing
    // data as possible, so that we display some (maybe stale) data to the
    // user and then update it as soon as the new data arrives.

    for (var index = 0; index < length; ++index) {
      if (!newValue.any((e) => e.key == this[index].key)) {
        this[index].detach();
        removeAt(index);
      }
    }

    var previousKey;
    for (var element in newValue) {
      var index = indexOfKey(element.key);
      if (index < 0) {
        _addWithKey(previousKey, element).attachTo(this);
      } else {
        this[index].updateWith(element).attachTo(this);
      }
      previousKey = element.key;
    }
  }

  @override
  void setAt(int index, T value) {
    super.setAt(index, this[index].updateWith(value)..attachTo(this));
  }

  @override
  void insert(int index, T element) {
    super.insert(index, element..attachTo(this));
  }

  @override
  T removeAt(int index) {
    this[index].detach();
    return super.removeAt(index);
  }
}
