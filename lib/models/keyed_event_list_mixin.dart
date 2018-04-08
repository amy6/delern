import 'dart:async';

import 'package:meta/meta.dart';

import 'observable_list.dart';

abstract class KeyedListItem {
  String get key;
}

class KeyedListEvent<T extends KeyedListItem> {
  final ListEventType eventType;
  final T value;
  final String previousSiblingKey;
  final Iterable<T> fullListValueForSet;

  KeyedListEvent({
    @required this.eventType,
    this.previousSiblingKey,
    this.value,
    this.fullListValueForSet,
  });

  String toString() {
    return '$eventType #$previousSiblingKey ($value)';
  }
}

abstract class KeyedEventListMixin<T extends KeyedListItem>
    implements ObservableList<T> {
  StreamSubscription<KeyedListEvent<T>> _subscription;

  int _indexOfKey(String key) => indexWhere((item) => item.key == key);

  void subscribeToKeyedEvents(Stream<KeyedListEvent<T>> stream) {
    if (_subscription != null) {
      _subscription.cancel();
    }
    _subscription = stream.listen(processKeyedEvent);
  }

  // TODO(dotdoom): this must be private.
  void processKeyedEvent(KeyedListEvent<T> event) {
    switch (event.eventType) {
      case ListEventType.itemAdded:
        // With Firebase, we subscribe to onValue, which delivers all data,
        // and then onChild* events, which are also initially delivered for
        // every child. We must therefore skip keys that we already got.
        var index = _indexOfKey(event.value.key);
        if (index < 0) {
          insert(_indexOfKey(event.previousSiblingKey) + 1, event.value);
        } else {
          assert(event.previousSiblingKey == null ||
              _indexOfKey(event.previousSiblingKey) >= 0);
        }
        break;
      case ListEventType.itemRemoved:
        removeAt(_indexOfKey(event.value.key));
        break;
      case ListEventType.itemChanged:
        // With Firebase, some events may be delivered twice - by different
        // listeners. E.g. "remove(X)" then "change(X, null)", in which case
        // the item will no longer exist by the time "change" arrives.
        var index = _indexOfKey(event.value.key);
        if (index >= 0) {
          setAt(_indexOfKey(event.value.key), event.value);
        }
        break;
      case ListEventType.itemMoved:
        move(_indexOfKey(event.value.key),
            _indexOfKey(event.previousSiblingKey) + 1);
        break;
      case ListEventType.set:
        setAll(0, event.fullListValueForSet ?? []);
        break;
    }
  }

  @override
  void detach() {
    if (_subscription != null) {
      _subscription.cancel();
    }
    super.detach();
  }
}
