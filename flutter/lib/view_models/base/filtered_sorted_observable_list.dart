import 'dart:async';

import 'package:delern_flutter/models/base/delayed_initialization.dart';
import 'package:delern_flutter/models/base/keyed_list_item.dart';
import 'package:observable/observable.dart';

typedef Filter<T> = bool Function(T item);

class FilteredSortedObservableList<T extends KeyedListItem>
    extends ObservableList<T>
    with KeyedListMixin<T>
    implements DelayedInitializationObservableList<T> {
  DelayedInitializationObservableList<T> _source;
  StreamSubscription<List<ListChangeRecord<T>>> _sourceChangesSubscription;

  FilteredSortedObservableList(this._source) : super.from(_source);

  Future<void> get initializationComplete => _source.initializationComplete;

  Filter<T> _filter;
  Filter<T> get filter => _filter;
  set filter(final Filter<T> value) {
    _filter = value;

    for (var srcIndex = 0; srcIndex < _source.length; ++srcIndex) {
      var item = _source[srcIndex];
      var currentIndex = indexOfKey(item.key);
      var newIndex = _indexForNewItem(srcIndex);

      if (currentIndex >= 0 && newIndex < 0) {
        removeAt(currentIndex);
      } else if (currentIndex < 0 && newIndex >= 0) {
        insert(newIndex, item);
      }
    }
  }

  Comparator<T> _comparator;
  Comparator<T> get comparator => _comparator;
  set comparator(Comparator<T> value) {
    _comparator = value;

    if (_comparator == null) {
      Iterable<T> newValue = _source;
      if (_filter != null) {
        newValue = newValue.where(_filter);
      }
      assert(newValue.length == length,
          'Sorting must not change the size of the list');
      // TODO(dotdoom): apply diff instead of replaceRange, also in other places
      replaceRange(0, length, newValue);
    } else {
      sort(_comparator);
    }
  }

  void listObserved() {
    super.listObserved();
    _sourceChangesSubscription = _source.listChanges.listen(_applyChanges);
  }

  void listUnobserved() {
    super.listUnobserved();
    _sourceChangesSubscription?.cancel();
    _sourceChangesSubscription = null;
  }

  void _applyChanges(List<ListChangeRecord<T>> changes) {
    changes.forEach((change) {
      change.removed.forEach((removedElement) {
        final removedIndex = indexOfKey(removedElement.key);
        if (removedIndex >= 0) {
          removeAt(removedIndex);
        }
      });

      for (var sourceIndex = change.index;
          sourceIndex < change.index + change.addedCount;
          ++sourceIndex) {
        final insertIndex = _indexForNewItem(sourceIndex);
        if (insertIndex >= 0) {
          insert(insertIndex, _source[sourceIndex]);
        }
      }
    });
  }

  int _indexForNewItem(int srcIndex) {
    var item = _source[srcIndex];

    if (_filter != null && !_filter(item)) {
      return -1;
    }

    if (_comparator == null) {
      // Find where in our list the next item from _source is.
      for (++srcIndex; srcIndex < _source.length; ++srcIndex) {
        var index = indexOfKey(_source[srcIndex].key);
        if (index >= 0) {
          return index;
        }
      }
    } else {
      // Scan through our list and find the right position.
      // TODO(dotdoom): binary search.
      for (var index = 0; index < length; ++index) {
        if (_comparator(this[index], item) >= 0) {
          return index;
        }
      }
    }
    return length;
  }
}
