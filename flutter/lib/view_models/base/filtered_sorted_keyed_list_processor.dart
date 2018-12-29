import 'package:meta/meta.dart';

import '../../models/base/events.dart';
import '../../models/base/keyed_list_item.dart';
import 'keyed_list_event_processor.dart';
import 'observable_keyed_list.dart';

typedef Filter<T> = bool Function(T item);

class FilteredSortedKeyedListProcessor<T extends KeyedListItem>
    extends KeyedListEventProcessor<T, ListEvent<T>> {
  final ObservableKeyedList<T> _source;

  FilteredSortedKeyedListProcessor(this._source) : super(() => _source.events) {
    if (_source.value != null) {
      // If initial value is already available, pick it up.
      _setAll(_source.value);
    }
  }

  @protected
  void processEvent(ListEvent<T> event) {
    switch (event.eventType) {
      case ListEventType.itemAdded:
        _add(event.index);
        break;
      case ListEventType.itemRemoved:
        final index = list.indexOfKey(event.previousValue.key);
        if (index >= 0) {
          _removeAt(index);
        }
        break;
      case ListEventType.itemMoved:
        _move(event.index);
        break;
      case ListEventType.itemChanged:
        assert(event.previousValue.key == list.value[event.index].key,
            'Item change modifies item key.');
        _update(event.index);
        break;
      case ListEventType.setAll:
        _setAll();
        break;
    }
  }

  Filter<T> _filter;
  Filter<T> get filter => _filter;
  set filter(final Filter<T> value) {
    _filter = value;

    if (_source.value == null) {
      // No data available yet.
      return;
    }

    for (var srcIndex = 0; srcIndex < _source.value.length; ++srcIndex) {
      var item = _source.value[srcIndex];
      var currentIndex = list.indexOfKey(item.key);
      var newIndex = _indexForNewItem(srcIndex);

      if (currentIndex >= 0 && newIndex < 0) {
        _removeAt(currentIndex);
      } else if (currentIndex < 0 && newIndex >= 0) {
        _insert(_indexForNewItem(srcIndex), item);
      }
    }
  }

  Comparator<T> _comparator;
  // Getter is not super useful for this property.
  // ignore: avoid_setters_without_getters
  set comparator(Comparator<T> value) {
    _comparator = value;

    if (_source.value == null) {
      // No data available yet.
      return;
    }

    if (_comparator == null) {
      Iterable<T> newValue = _source.value;
      if (_filter != null) {
        newValue = newValue.where(_filter);
      }
      assert(newValue.length == list.value.length,
          'Sorting must not change the size of the list');
      _setAll(0, newValue);
    } else {
      _setAll(0, List<T>.from(this)..sort(_comparator));
    }
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
        if (this[index].key != item.key &&
            _comparator(this[index], item) >= 0) {
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

  void _baseItemChanged(int index) {
    var oldIndex = indexOfKey(_base[index].key);
    var newIndex = _indexForNewItem(index);

    // Update the item in case it was modified, and to notify subscribers.
    if (oldIndex >= 0) {
      setAt(oldIndex, _base[index]);
    }

    if (oldIndex != newIndex) {
      if (oldIndex >= 0) {
        if (newIndex >= 0) {
          move(oldIndex, newIndex);
        } else {
          removeAt(oldIndex);
        }
      } else if (newIndex >= 0) {
        insert(newIndex, _base[index]);
      }
    }
  }

  void _baseItemMoved(int newBaseIndex) {
    if (_comparator == null) {
      var newIndex = _indexForNewItem(newBaseIndex);
      // Check that it passes the filter.
      if (newIndex >= 0) {
        var oldIndex = indexOfKey(_base[newBaseIndex].key);
        assert(oldIndex >= 0, 'Item move adds it to the list.');
        move(oldIndex, newIndex);
      }
    }
  }

  void _baseSetAll() {
    // We rely heavily on ViewModelsList being a _base. It has to control its
    // children carefully to activate / deactivate them timely. Therefore, it
    // does not call setAll.
    // TODO(dotdoom): force _base to be ViewModelsList?
    assert(!changed, 'ProxyKeyedList supports "set" only for initializing.');

    var items = _base.toList(growable: false);

    if (_filter != null) {
      items = items.where(_filter).toList(growable: false);
    }

    if (_comparator != null) {
      items.sort(_comparator);
    }

    // Here we can simply use setAll without caring about UI, because UI will
    // not display the list widget until 'changed' is true.
    setAll(0, items);
  }

  @mustCallSuper
  void dispose() {
    _baseEventsSubscription.cancel();
    super.dispose();
  }
}
