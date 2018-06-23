import 'dart:async';

import 'package:meta/meta.dart';

import '../../models/base/keyed_list.dart';
import '../../models/base/observable_list.dart';

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
          assert(event.previousValue.key == _base[event.index].key,
              'Item change modifies item key.');
          _baseItemChanged(event.index);
          break;
        case ListEventType.set:
          _baseSet();
          break;
      }
    });
  }

  set filter(final Filter<T> value) {
    _filter = value;
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

  set comparator(Comparator<T> value) {
    _comparator = value;
    if (isEmpty) {
      // Shortcut to avoid flipping 'changed' before any data has arrived.
      return;
    }
    if (_comparator == null) {
      Iterable<T> newValue = _base;
      if (_filter != null) {
        newValue = _base.where(_filter);
      }
      assert(newValue.length == length,
          'Sorting must not change the size of the list');
      setAll(0, newValue);
    } else {
      setAll(0, new List<T>.from(this)..sort(_comparator));
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

  void _baseSet() {
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
