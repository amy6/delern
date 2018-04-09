import 'dart:async';

import 'observable_list.dart';
import 'keyed_list_event.dart';

typedef bool Filter<T>(T item);

// TODO(dotdoom): make this list read-only.
class ProxyKeyedList<T extends KeyedListItem> extends ObservableList<T>
    with KeyedListMixin<T> {
  final ObservableList<T> _base;
  StreamSubscription<ListEvent<T>> _baseEventsSubscription;
  Filter<T> _filter;
  Comparator<T> _comparator;

  ProxyKeyedList(this._base) {
    _baseEventsSubscription = _base.events.listen((event) {
      switch (event.eventType) {
        case ListEventType.itemAdded:
          _baseItemAdded(_base[event.index]);
          break;
        case ListEventType.itemRemoved:
          var index = indexOfKey(event.previousValue.key);
          if (index >= 0) {
            _baseItemRemoved(index);
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
        // TODO(dotdoom): is this the right comparison?
        if (_comparator(item, this[index]) >= 0) {
          break;
        }
      }
      super.insert(index, item);
    }
  }

  void _baseItemRemoved(int index) {
    super.removeAt(index);
  }

  void _baseItemChanged(ListEvent<T> event) {
    var item = _base[event.index];
    if (_filter == null || _filter(item)) {
      if (indexOfKey(event.previousValue.key) == -1) {
        super.add(item);
      }
    } else {
      var index = indexOfKey(event.previousValue.key);
      if (index >= 0) {
        super.removeAt(index);
      }
    }
    _resort();
  }

  set filter(final Filter<T> value) {
    _filter = value;
    _refilter();
  }

  set comparator(Comparator<T> value) {
    _comparator = value;
    _resort();
  }

  void _resort() {
    setAll(0, new List<T>.from(_base)..sort(_comparator));
  }

  void _refilter() {
    if (_filter == null) {
      setAll(0, _base);
    } else {
      setAll(0, _base.where(_filter));
    }
    _resort();
  }

  void dispose() => _baseEventsSubscription.cancel();
}
