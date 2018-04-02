import 'package:meta/meta.dart';

abstract class Disposable {
  @mustCallSuper
  void dispose();
}
