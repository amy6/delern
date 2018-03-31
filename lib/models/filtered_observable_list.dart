import 'disposable.dart';
import 'observable_list.dart';

typedef bool Filter<T>(T item);

// TODO(dotdoom): make this list read-only.
// TODO(dotdoom): cancel subscription on dispose().
class FilteredObservableList<T> extends ObservableList<T>
    implements Disposable {
  final ObservableList<T> _base;
  Filter<T> _filter;

  FilteredObservableList(this._base) : super(_base.toList()) {
    _base.events.listen((event) {
      switch (event.eventType) {
        case ListEventType.added:
          var item = _base[event.index];
          if (_filter == null || _filter(item)) {
            super.add(item);
          }
          break;
        case ListEventType.removed:
          var index = indexOf(event.previousValue);
          if (index >= 0) {
            super.removeAt(index);
          }
          break;
        case ListEventType.moved:
          break;
        case ListEventType.changed:
          var item = _base[event.index];
          if (_filter == null || _filter(item)) {
            // TODO(dotdoom): what if we have non-unique items in the list?
            // TODO(dotdoom): indexOf(previousValue) will stop working for
            //                Persistable once we implement operator== for the
            //                items. Should we use Keyed instead?
            if (indexOf(event.previousValue) == -1) {
              super.add(item);
            }
          } else {
            var index = indexOf(event.previousValue);
            if (index >= 0) {
              super.removeAt(index);
            }
          }
          break;
      }
    });
  }

  set filter(final Filter<T> value) {
    _filter = value;
    var toRemove = [];
    _base.forEach((item) {
      if (indexOf(item) == -1) {
        // TODO(dotdoom): duplicates in the list are not handled.
        if (_filter == null || _filter(item)) {
          add(item);
        }
      }
      if (_filter != null && !_filter(item)) {
        toRemove.add(item);
      }
    });

    toRemove.forEach((x) {
      int index = indexOf(x);
      if (index != -1) {
        removeAt(indexOf(x));
      }
    });
  }
}
