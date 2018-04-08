import 'package:meta/meta.dart';

abstract class Disposable<T> {
  @mustCallSuper
  void detach();

  @mustCallSuper
  void attachTo(T owner);
}
