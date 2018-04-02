import 'disposable.dart';
import 'observable_list.dart';

abstract class Persistable<T> extends Disposable {
  T absorb(T value);
  void own(PersistablesListMixin owner);
}

abstract class PersistablesListMixin<T extends Persistable<T>>
    implements ObservableList<T> {
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
}
