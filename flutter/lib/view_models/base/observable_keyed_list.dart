import 'dart:async';

import 'package:meta/meta.dart';

import '../../models/base/events.dart';
import '../../models/base/keyed_list_item.dart';

typedef StreamGetter<T> = Stream<T> Function();

@immutable
class ListEvent<T> {
  final ListEventType eventType;
  final Type eventSource;
  final int index;
  final T previousValue;

  const ListEvent({
    @required this.eventType,
    this.index,
    this.eventSource,
    this.previousValue,
  });

  String toString() => '$eventType #$index ($previousValue)';
}

class ObservableKeyedList<T extends KeyedListItem> {
  StreamController<ListEvent<T>> _events;
  Stream<ListEvent<T>> get events => _events.stream;

  List<T> _value;
  List<T> _externalValue;
  List<T> get value => _externalValue;

  ObservableKeyedList(this._events);

  int indexOfKey(String key) => _value.indexWhere((item) => item.key == key);

  void _notify(ListEvent<T> event) {
    _externalValue = List.unmodifiable(_value);
    // TODO(dotdoom): factor out _events and _notify into a separate class.
    _events.add(event);
  }

  void move(int takeFromIndex, int insertBeforeIndex, [Type eventSource]) {
    // Adjust because once we move, the index will decrease.
    if (insertBeforeIndex > takeFromIndex) {
      --insertBeforeIndex;
    }
    if (takeFromIndex == insertBeforeIndex) {
      return;
    }
    _value.insert(insertBeforeIndex, _value.removeAt(takeFromIndex));
    _notify(ListEvent(
      eventType: ListEventType.itemMoved,
      eventSource: eventSource,
      index: insertBeforeIndex,
    ));
  }

  void removeAt(int index, [Type eventSource]) => _notify(ListEvent(
      eventType: ListEventType.itemRemoved,
      eventSource: eventSource,
      index: index,
      previousValue: _value.removeAt(index)));

  void insert(int beforeIndex, T value, [Type eventSource]) {
    _value.insert(beforeIndex, value);
    _notify(ListEvent(
        eventType: ListEventType.itemAdded,
        eventSource: eventSource,
        index: beforeIndex));
  }

  void update(int index, T value, [Type eventSource]) {
    _value[index] = value;
    _notify(ListEvent(
        eventType: ListEventType.itemChanged,
        eventSource: eventSource,
        index: index));
  }

  void setAll(Iterable<T> newValue, [Type eventSource]) {
    if (_value == null) {
      // Initial data arrival. We were waiting for you!
      _value = newValue;
      _notify(
          ListEvent(eventType: ListEventType.setAll, eventSource: eventSource));
      return;
    }

    // setAll is called when we receive onValue, which can be initial data or an
    // update after listening stream is closed and reopened. For the update, we
    // have to merge, so that the UI shows the old state transitioning to the
    // new state in a slick way.

    for (var index = _value.length - 1; index >= 0; --index) {
      if (!newValue.any((e) => e.key == _value[index].key)) {
        removeAt(index, eventSource);
      }
    }

    var index = 0;
    for (var element in newValue) {
      var existingIndex = indexOfKey(element.key);
      if (existingIndex < 0) {
        insert(index, element, eventSource);
      } else {
        if (existingIndex != index) {
          assert(existingIndex > index,
              'DatabaseListEventProcessor missed an item at re-arrangement');
          move(existingIndex, index, eventSource);
        }
        update(index, element, eventSource);
      }
      ++index;
    }
  }
}
