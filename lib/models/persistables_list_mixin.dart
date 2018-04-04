import 'disposable.dart';
import 'observable_list.dart';

abstract class Persistable<T> extends Disposable {
  T absorb(T value);
  // TODO(dotdoom): move own() into Disposable?
  void own(PersistablesListMixin owner);
}

abstract class PersistablesListMixin<T extends Persistable<T>>
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
    super.setAll(index, newValue..forEach((e) => e.own(this)));
  }

  @override
  void setAt(int index, T value) {
    super.setAt(index, this[index].absorb(value)..own(this));
  }

  @override
  void insert(int index, T element) {
    super.insert(index, element..own(this));
  }

  @override
  T removeAt(int index) {
    this[index].dispose();
    return super.removeAt(index);
  }

  @override
  void dispose() {
    forEach((item) => item.dispose());
    super.dispose();
  }
}
