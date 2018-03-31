import 'disposable.dart';
import 'observable_list.dart';

abstract class Persistable<T> extends Disposable {
  T absorb(T value);
}

class PersistablesList<T extends Persistable<T>> extends ObservableList<T> {
  PersistablesList(List<T> base) : super(base);

  @override
  void set(int index, T value) {
    super.set(index, this[index].absorb(value));
  }

  @override
  T removeAt(int index) {
    this[index].dispose();
    return super.removeAt(index);
  }
}
