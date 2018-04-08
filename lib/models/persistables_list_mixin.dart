import 'package:meta/meta.dart';

import 'attachable.dart';
import 'observable_list.dart';

abstract class Persistable<T> extends Attachable<T> {
  Persistable<T> absorb(@checked Persistable<T> value);
}

abstract class PersistablesListMixin<
        T extends Persistable<PersistablesListMixin<T>>>
    implements ObservableList<T> {
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

  @override
  void detach() {
    forEach((item) => item.detach());
    super.detach();
  }
}
