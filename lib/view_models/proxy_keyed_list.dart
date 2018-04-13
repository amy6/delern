import 'dart:async';

import '../models/keyed_list.dart';
import '../models/observable_list.dart';

typedef bool Filter<T>(T item);

class ProxyKeyedList<T extends KeyedListItem> extends ObservableList<T>
    with KeyedListMixin<T> {
  final ObservableList<T> _base;
  StreamSubscription<ListEvent<T>> _baseEventsSubscription;
  Filter<T> _filter;
  Comparator<T> _comparator;

  ProxyKeyedList(this._base) {
    if (_base.changed) {
      super.setAll(0, _base);
    }
    _baseEventsSubscription = _base.events.listen((event) {
      switch (event.eventType) {
        case ListEventType.itemAdded:
          _baseItemAdded(_base[event.index]);
          break;
        case ListEventType.itemRemoved:
          var index = indexOfKey(event.previousValue.key);
          if (index >= 0) {
            super.removeAt(index);
          }
          break;
        case ListEventType.itemMoved:
          break;
        case ListEventType.itemChanged:
          _baseItemChanged(event);
          break;
        case ListEventType.set:
          _refilter();
          break;
      }
    });
  }

  void _baseItemAdded(T item) {
    if (_filter != null && !_filter(item)) {
      return;
    }

    if (_comparator == null) {
      super.add(item);
    } else {
      var index;
      for (index = 0; index < length; ++index) {
        if (_comparator(this[index], item) >= 0) {
          break;
        }
      }
      super.insert(index, item);
    }
  }

  void _baseItemChanged(ListEvent<T> event) {
    var item = _base[event.index];
    var index = indexOfKey(event.previousValue.key);
    if (_filter == null || _filter(item)) {
      if (index >= 0) {
        super.setAt(index, item);
      } else {
        super.add(item);
      }
    } else {
      if (index >= 0) {
        super.removeAt(index);
      }
    }
    _sortAndSet(this);
  }

  set filter(final Filter<T> value) {
    _filter = value;
    if (_base.isNotEmpty) {
      _refilter();
    }
  }

  set comparator(Comparator<T> value) {
    _comparator = value;
    if (isNotEmpty) {
      _sortAndSet(this);
    }
  }

  void _sortAndSet(Iterable<T> items) {
    // This won't work with UI. Once the list is constructred, we can only use
    // add / remove to change its size.
    assert(!changed || items.length == length,
        'Attempt to change list size from $length to ${items.length}');
    if (_comparator == null) {
      super.setAll(0, items);
    } else {
      super.setAll(0, new List<T>.from(items)..sort(_comparator));
    }
  }

  void _refilter() {
    if (_filter == null) {
      _sortAndSet(_base);
    } else {
      _sortAndSet(_base.where(_filter));
    }
  }

  void dispose() => _baseEventsSubscription.cancel();

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
  T removeAt(int index) {
    _throwReadOnly();
    return null;
  }

  void _throwReadOnly() {
    throw new UnsupportedError('ProxyKeyedList interface is read-only');
  }
}
