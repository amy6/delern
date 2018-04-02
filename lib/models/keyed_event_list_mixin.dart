import 'package:meta/meta.dart';

import 'observable_list.dart';

abstract class KeyedListItem {
  String get key;
}

class KeyedListEvent<T extends KeyedListItem> {
  final ListEventType eventType;
  final T value;
  final String previousSiblingKey;

  KeyedListEvent({
    @required this.eventType,
    @required this.previousSiblingKey,
    this.value,
  });

  String toString() {
    return '$eventType #$previousSiblingKey ($value)';
  }
}

abstract class KeyedEventListMixin<T extends KeyedListItem>
    implements ObservableList<T> {
  int _indexOfKey(String key) => indexWhere((item) => item.key == key);

  void processKeyedEvent(KeyedListEvent<T> event) {
    switch (event.eventType) {
      case ListEventType.added:
        // With Firebase, we subscribe to onValue, which delivers all data,
        // and then onChild* events, which are also initially delivered for
        // every child. We must therefore skip keys that we already got.
        var index = _indexOfKey(event.value.key);
        if (index < 0) {
          insert(_indexOfKey(event.previousSiblingKey) + 1, event.value);
        }
        /* TODO(dotdoom): fix (order issues?) or remove this assert
        else {
          assert((index == 0 && event.previousSiblingKey == null) ||
              (this[index - 1].key == event.previousSiblingKey));
        }*/
        break;
      case ListEventType.removed:
        removeAt(_indexOfKey(event.value.key));
        break;
      case ListEventType.changed:
        // With Firebase, some events may be delivered twice - by different
        // listeners. E.g. "remove(X)", "change(X, null)". We should ignore it.
        var index = _indexOfKey(event.value.key);
        if (index >= 0) {
          setAt(_indexOfKey(event.value.key), event.value);
        }
        break;
      case ListEventType.moved:
        move(_indexOfKey(event.value.key),
            _indexOfKey(event.previousSiblingKey) + 1);
        break;
    }
  }
}
