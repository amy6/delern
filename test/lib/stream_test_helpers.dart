import 'dart:async';

Stream<T> listToStream<T>(List<T> list) {
  var controller = new StreamController<T>(sync: true);
  controller.onListen = () {
    list.forEach((e) => controller.add(e));
    controller.close();
  };
  return controller.stream;
}
