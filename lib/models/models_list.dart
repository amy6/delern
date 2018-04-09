import 'dart:async';

import 'package:meta/meta.dart';

import 'keyed_list_event.dart';
import 'observable_list.dart';
import 'attachable.dart';

abstract class Model<T> implements KeyedListItem, Attachable<T> {
  String get key;
  Model<T> absorb(@checked Model<T> value);
}

class ModelsListEvent<T extends Model> extends KeyedListEvent<T> {
  final ListEventType eventType;
  final T value;
  final String previousSiblingKey;
  final Iterable<T> fullListValueForSet;

  ModelsListEvent({
    @required this.eventType,
    this.previousSiblingKey,
    this.value,
    this.fullListValueForSet,
  });

  String toString() {
    return '$eventType #$previousSiblingKey ($value)';
  }
}

class ModelsList<T extends Model<ModelsList<T>>> extends ObservableList<T>
    implements Attachable<Stream<ModelsListEvent<T>>> {
  StreamSubscription<ModelsListEvent<T>> _subscription;

  int indexOfKey(String key) => indexWhere((item) => item.key == key);

  @override
  void attachTo(Stream<ModelsListEvent<T>> stream) {
    _subscription?.cancel();
    forEach((item) => item.attachTo(this));
    _subscription = stream.listen(processKeyedEvent);
  }

  // TODO(dotdoom): this must be private.
  void processKeyedEvent(ModelsListEvent<T> event) {
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
    super.setAt(index, this[index].absorb(value)..attachTo(this));
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
