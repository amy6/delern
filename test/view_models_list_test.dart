import 'dart:async';

import 'package:test/test.dart';

import '../lib/models/keyed_list.dart';
import '../lib/models/observable_list.dart';
import '../lib/view_models/view_models_list.dart';

Stream<T> _listToStream<T>(List<T> list) {
  var controller = new StreamController<T>(sync: true);
  controller.onListen = () {
    list.forEach((e) => controller.add(e));
    controller.close();
  };
  return controller.stream;
}

class TestFixture extends ViewModel<ViewModelsList<TestFixture>> {
  final String key;

  TestFixture(this.key);

  @override
  void attachTo(ViewModelsList<TestFixture> owner) {
    // TODO: implement attachTo
  }

  @override
  void detach() {
    // TODO: implement detach
  }

  @override
  ViewModel<ViewModelsList<TestFixture>> updateWith(
      ViewModel<ViewModelsList<TestFixture>> value) {
    return value;
  }
}

void main() {
  test('key insertions', () async {
    var list = new ViewModelsList<TestFixture>();

    list.attachTo(_listToStream([
      new KeyedListEvent(
          eventType: ListEventType.itemAdded,
          previousSiblingKey: null,
          value: new TestFixture('1')),
      new KeyedListEvent(
          eventType: ListEventType.itemAdded,
          previousSiblingKey: '1',
          value: new TestFixture('2')),
      new KeyedListEvent(
          eventType: ListEventType.itemAdded,
          previousSiblingKey: '1',
          value: new TestFixture('2')),
    ]));

    // Wait for all microtasks (listen()) to complete.
    await new Future(() {});

    expect(list.length, equals(2));
  });
}
