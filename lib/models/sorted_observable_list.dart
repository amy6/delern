import 'disposable.dart';
import 'observable_list.dart';

// TODO(dotdoom): make this list read-only.
// TODO(dotdoom): cancel subscription on dispose().
class SortedObservableList<T> extends ObservableList<T> implements Disposable {
  final ObservableList<T> _base;
  Comparator<T> _comparator;

  SortedObservableList(this._base) : super(_base.toList()) {
    _base.events.listen((event) {
      switch (event.eventType) {
        case ListEventType.moved:
          break;
        case ListEventType.removed:
          if (_comparator == null) {
            super.removeAt(event.index);
          } else {
            super.removeAt(indexOf(event.previousValue));
          }
          break;
        case ListEventType.added:
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
        case ListEventType.changed:
          _resort();
          // TODO(stazis): optimize that case
          break;
      }
    });
  }

  set comparator(Comparator<T> value) {
    _comparator = value;
    _resort();
  }

  void _resort() {
    // TODO(dotdoom): optimize.
    while (length > 0) {
      removeAt(0);
    }

    List<T> sorted = _base.toList()..sort(_comparator);
    addAll(sorted);
  }
}
