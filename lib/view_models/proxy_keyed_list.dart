import 'dart:async';

import 'package:meta/meta.dart';

import '../models/keyed_list.dart';
import '../models/observable_list.dart';

typedef bool Filter<T>(T item);

// TODO(dotdoom): make list interface readonly.
class ProxyKeyedList<T extends KeyedListItem> extends ObservableList<T>
    with KeyedListMixin<T> {
  final ObservableList<T> _base;
  StreamSubscription<ListEvent<T>> _baseEventsSubscription;
  Filter<T> _filter;
  Comparator<T> _comparator;

  ProxyKeyedList(this._base) {
    if (_base.changed) {
      setAll(0, _base);
    }
    _baseEventsSubscription = _base.events.listen((event) {
      switch (event.eventType) {
        case ListEventType.itemAdded:
          _baseItemAdded(event.index);
          break;
        case ListEventType.itemRemoved:
          var index = indexOfKey(event.previousValue.key);
          if (index >= 0) {
            removeAt(index);
          }
          break;
        case ListEventType.itemMoved:
          _baseItemMoved(event.index);
          break;
        case ListEventType.itemChanged:
          _baseItemChanged(event);
          break;
        case ListEventType.set:
          _baseSet();
          break;
      }
    });
  }

  set filter(final Filter<T> value) {
    _filter = value;
    _refilter();
  }

  set comparator(Comparator<T> value) {
    _comparator = value;
    _resort();
  }

  int _indexForNewItem(int baseIndex) {
    var item = _base[baseIndex];
    if (_filter != null && !_filter(item)) {
      return -1;
    }

    if (_comparator == null) {
      for (++baseIndex; baseIndex < _base.length; ++baseIndex) {
        var index = indexOfKey(_base[baseIndex].key);
        if (index >= 0) {
          return index;
        }
      }
    } else {
      for (var index = 0; index < length; ++index) {
        if (_comparator(this[index], item) >= 0) {
          return index;
        }
      }
    }
    return length;
  }

  void _baseItemAdded(int baseIndex) {
    var index = _indexForNewItem(baseIndex);
    if (index >= 0) {
      insert(index, _base[baseIndex]);
    }
  }

  void _baseItemChanged(ListEvent<T> event) {
    var oldIndex = indexOfKey(event.previousValue.key);
    var newIndex = _indexForNewItem(event.index);

    if (oldIndex == newIndex) {
      return;
    }

    if (oldIndex >= 0) {
      if (newIndex >= 0) {
        move(oldIndex, newIndex);
      } else {
        removeAt(oldIndex);
      }
    } else if (newIndex >= 0) {
      insert(newIndex, _base[event.index]);
    }
  }

  void _baseItemMoved(int newBaseIndex) {
    if (_comparator == null) {
      // TODO(dotdoom): test that this works.
      move(indexOfKey(_base[newBaseIndex].key), _indexForNewItem(newBaseIndex));
    }
  }

  void _baseSet() {
    _refilter();
    _resort();
  }

  void _refilter() {
    for (var baseIndex = 0; baseIndex < _base.length; ++baseIndex) {
      var item = _base[baseIndex];
      var currentIndex = indexOfKey(item.key);
      var newIndex = _indexForNewItem(baseIndex);

      if (currentIndex >= 0 && newIndex < 0) {
        removeAt(currentIndex);
      } else if (currentIndex < 0 && newIndex >= 0) {
        insert(_indexForNewItem(baseIndex), item);
      }
    }
  }

  void _resort() {
    if (_comparator == null) {
      if (_filter == null) {
        setAll(0, _base);
      } else {
        setAll(0, _base.where(_filter));
      }
    } else {
      setAll(0, new List<T>.from(this)..sort(_comparator));
    }
  }

  @mustCallSuper
  void dispose() {
    _baseEventsSubscription.cancel();
    super.dispose();
  }
}
