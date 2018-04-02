import 'disposable.dart';
import 'observable_list.dart';

abstract class Persistable<T> extends Disposable {
  T absorb(T value);
}

abstract class PersistablesListMixin<T extends Persistable<T>>
    implements ObservableList<T> {
  @override
  void setAt(int index, T value) {
    super.setAt(index, this[index].absorb(value));
  }

  @override
  T removeAt(int index) {
    this[index].dispose();
    return super.removeAt(index);
  }
}
