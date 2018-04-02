import 'disposable.dart';
import 'observable_list.dart';

abstract class Persistable<T> extends Disposable {
  T absorb(T value);
}

class PersistablesList<T extends Persistable<T>> extends ObservableList<T> {
  PersistablesList(List<T> base) : super(base);

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
