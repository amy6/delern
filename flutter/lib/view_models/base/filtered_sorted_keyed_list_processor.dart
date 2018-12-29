import 'package:meta/meta.dart';

import '../../models/base/database_list_event.dart';
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
      _setAll();
    }
  }

  @protected
  void processEvent(ListEvent<T> event) {
    switch (event.eventType) {
      case ListEventType.itemAdded:
        _insert(event.index);
        break;
      case ListEventType.itemRemoved:
        final index = list.indexOfKey(event.previousValue.key);
        if (index >= 0) {
          list.removeAt(index);
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
        list.removeAt(currentIndex);
      } else if (currentIndex < 0 && newIndex >= 0) {
        list.insert(_indexForNewItem(srcIndex), item);
      }
    }
  }

  Comparator<T> _comparator;
  Comparator<T> get comparator => _comparator;
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
      list.setAll(newValue);
    } else {
      list.setAll(List<T>.from(list.value)..sort(_comparator));
    }
  }

  int _indexForNewItem(int srcIndex) {
    var item = _source.value[srcIndex];
    if (_filter != null && !_filter(item)) {
      return -1;
    }

    if (_comparator == null) {
      for (++srcIndex; srcIndex < _source.value.length; ++srcIndex) {
        var index = list.indexOfKey(_source.value[srcIndex].key);
        if (index >= 0) {
          return index;
        }
      }
    } else {
      for (var index = 0; index < list.value.length; ++index) {
        if (list.value[index].key != item.key &&
            _comparator(list.value[index], item) >= 0) {
          return index;
        }
      }
    }
    return list.value.length;
  }

  void _insert(int srcIndex) {
    var index = _indexForNewItem(srcIndex);
    if (index >= 0) {
      list.insert(index, _source.value[srcIndex]);
    }
  }

  void _update(int srcIndex) {
    var oldIndex = list.indexOfKey(_source.value[srcIndex].key);
    var newIndex = _indexForNewItem(srcIndex);

    // Update the item in case it was modified, and to notify subscribers.
    if (oldIndex >= 0) {
      list.setAt(srcIndex, _source.value[srcIndex]);
    }

    if (oldIndex != newIndex) {
      if (oldIndex >= 0) {
        if (newIndex >= 0) {
          list.move(oldIndex, newIndex);
        } else {
          list.removeAt(oldIndex);
        }
      } else if (newIndex >= 0) {
        list.insert(newIndex, _source.value[srcIndex]);
      }
    }
  }

  void _move(int newSrcIndex) {
    if (_comparator == null) {
      var newIndex = _indexForNewItem(newSrcIndex);
      // Check that it passes the filter.
      if (newIndex >= 0) {
        var oldIndex = list.indexOfKey(_source.value[newSrcIndex].key);
        assert(oldIndex >= 0, 'Item move adds it to the list.');
        list.move(oldIndex, newIndex);
      }
    }
  }

  void _setAll() {
    var items = _source.value.toList(growable: false);

    if (_filter != null) {
      items = items.where(_filter).toList(growable: false);
    }

    if (_comparator != null) {
      items.sort(_comparator);
    }

    list.setAll(items);
  }
}
