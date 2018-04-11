import 'dart:async';

import 'package:meta/meta.dart';

import '../models/keyed_list_event.dart';
import '../models/observable_list.dart';
import 'attachable.dart';

abstract class ViewModel<T> implements KeyedListItem, Attachable<T> {
  String get key;
  ViewModel<T> updateWith(@checked ViewModel<T> value);
}

class ViewModelsListEvent<T extends ViewModel> extends KeyedListEvent<T> {
  final ListEventType eventType;
  final T value;
  final String previousSiblingKey;
  final Iterable<T> fullListValueForSet;

  ViewModelsListEvent({
    @required this.eventType,
    this.previousSiblingKey,
    this.value,
    this.fullListValueForSet,
  });

  String toString() {
    return '$eventType #$previousSiblingKey ($value)';
  }
}

class ViewModelsList<T extends ViewModel<ViewModelsList<T>>>
    extends ObservableList<T>
    with KeyedListMixin<T>
    implements Attachable<Stream<ViewModelsListEvent<T>>> {
  StreamSubscription<ViewModelsListEvent<T>> _subscription;

  @override
  void attachTo(Stream<ViewModelsListEvent<T>> stream) {
    _subscription?.cancel();
    forEach((item) => item.attachTo(this));
    _subscription = stream.listen(_processEvent);
  }

  void childUpdated(T child) {
    _processEvent(new ViewModelsListEvent(
      eventType: ListEventType.itemChanged,
      value: child,
    ));
  }

  void _processEvent(ViewModelsListEvent<T> event) {
    switch (event.eventType) {
      case ListEventType.itemAdded:
        // With Firebase, we subscribe to onValue, which delivers all data,
        // and then onChild* events, which are also initially delivered for
        // every child. We must therefore skip keys that we already got.
        var index = indexOfKey(event.value.key);
        if (index < 0) {
          insert(indexOfKey(event.previousSiblingKey) + 1, event.value);
        } else {
          assert(event.previousSiblingKey == null ||
              indexOfKey(event.previousSiblingKey) >= 0);
        }
        break;
      case ListEventType.itemRemoved:
        removeAt(indexOfKey(event.value.key));
        break;
      case ListEventType.itemChanged:
        // With Firebase, some events may be delivered twice - by different
        // listeners. E.g. "remove(X)" then "change(X, null)", in which case
        // the item will no longer exist by the time "change" arrives.
        var index = indexOfKey(event.value.key);
        if (index >= 0) {
          setAt(indexOfKey(event.value.key), event.value);
        }
        break;
      case ListEventType.itemMoved:
        move(indexOfKey(event.value.key),
            indexOfKey(event.previousSiblingKey) + 1);
        break;
      case ListEventType.set:
        setAll(0, event.fullListValueForSet ?? []);
        break;
    }
  }

  @override
  void detach() {
    _subscription?.cancel();
    _subscription = null;
    forEach((item) => item.detach());
  }

  @override
  void setAll(int index, Iterable<T> newValue) {
    if (changed) {
      // TODO(dotdoom): support this case for widget's resume.
      throw new UnsupportedError('setAll can only be called once');
    }
    if (index != 0) {
      throw new UnsupportedError('setAll can only set at index 0');
    }
    super.setAll(index, newValue..forEach((e) => e.attachTo(this)));
  }

  @override
  void setAt(int index, T value) {
    super.setAt(index, this[index].updateWith(value)..attachTo(this));
  }

  @override
  void insert(int index, T element) {
    super.insert(index, element..attachTo(this));
  }

  @override
  T removeAt(int index) {
    this[index].detach();
    return super.removeAt(index);
  }
}
