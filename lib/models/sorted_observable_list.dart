import 'dart:async';

import 'observable_list.dart';

// TODO(dotdoom): make this list read-only.
class SortedObservableList<T> extends ObservableList<T> {
  final ObservableList<T> _base;
  StreamSubscription<ListEvent<T>> _baseEventsSubscription;
  Comparator<T> _comparator;

  SortedObservableList(this._base) {
    _baseEventsSubscription = _base.events.listen((event) {
      switch (event.eventType) {
        case ListEventType.itemMoved:
          break;
        case ListEventType.itemRemoved:
          if (_comparator == null) {
            super.removeAt(event.index);
          } else {
            super.removeAt(indexOf(event.previousValue));
          }
          break;
        case ListEventType.itemAdded:
          if (_comparator == null) {
            super.insert(event.index, _base[event.index]);
          } else {
            var item = _base[event.index];
            var index;
            for (index = 0; index < length; ++index) {
              if (_comparator(item, this[index]) >= 0) {
                break;
              }
            }
            super.insert(index, item);
          }
          break;
        case ListEventType.itemChanged:
          _resort();
          // TODO(stazis): optimize that case
          break;
        case ListEventType.set:
          _resort();
          break;
      }
    });
  }

  set comparator(Comparator<T> value) {
    _comparator = value;
    _resort();
  }

  void _resort() {
    setAll(0, new List<T>.from(_base)..sort(_comparator));
  }

  void dispose() => _baseEventsSubscription.cancel();
}
