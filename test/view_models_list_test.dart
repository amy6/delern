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
  dynamic data;

  TestFixture(this.key);

  @override
  void activate() {
    // TODO: implement activate
  }

  @override
  void deactivate() {
    // TODO: implement deactivate
  }

  @override
  ViewModel<ViewModelsList<TestFixture>> updateWith(
      ViewModel<ViewModelsList<TestFixture>> value) {
    return value;
  }

  @override
  bool operator ==(other) =>
      (other is TestFixture) && key == other.key && data == other.data;

  @override
  int get hashCode => key.hashCode ^ data.hashCode;

  @override
  String toString() => '#$key [$data]';
}

void main() {
  test('key operations', () async {
    var list = new ViewModelsList<TestFixture>(() => _listToStream([
          new KeyedListEvent(
            eventType: ListEventType.set,
            fullListValueForSet: [new TestFixture('1')],
          ),
          // 1
          new KeyedListEvent(
              eventType: ListEventType.itemAdded,
              previousSiblingKey: '1',
              value: new TestFixture('2')),
          // 1 2
          new KeyedListEvent(
              eventType: ListEventType.itemAdded,
              previousSiblingKey: '1',
              value: new TestFixture('2')),
          // 1 2
          new KeyedListEvent(
            eventType: ListEventType.itemAdded,
            previousSiblingKey: '2',
            value: new TestFixture('3'),
          ),
          // 1 2 3
          new KeyedListEvent(
              eventType: ListEventType.itemRemoved,
              value: new TestFixture('2')),
          // 1 3
          new KeyedListEvent(
              eventType: ListEventType.itemMoved, value: new TestFixture('3')),
          new KeyedListEvent(
              eventType: ListEventType.itemChanged,
              value: new TestFixture('1')..data = 'foo'),
        ]));

    list.activate();

    // Wait for all microtasks (listen()) to complete.
    await new Future(() {});

    list.childUpdated(new TestFixture('3')..data = 'bar');

    expect(
        list,
        equals([
          new TestFixture('3')..data = 'bar',
          new TestFixture('1')..data = 'foo',
        ]));
  });
}
