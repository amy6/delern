import 'package:meta/meta.dart';

abstract class Attachable<T> {
  // TODO(dotdoom): annotate @mustCallSuper where (and only where) necessary

  @mustCallSuper
  void detach();

  @mustCallSuper
  void attachTo(T owner);
}
