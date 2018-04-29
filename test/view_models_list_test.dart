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

class TestFixture extends ViewModel {
  final String key;
  dynamic data;
  bool _active = false;

  bool get active => _active;

  TestFixture(this.key);

  @override
  void activate() => _active = true;

  @override
  void deactivate() => _active = false;

  @override
  ViewModel updateWith(ViewModel value) {
    deactivate();
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

  test('setAll merging', () async {
    var list = new ViewModelsList<TestFixture>(() => _listToStream([
          new KeyedListEvent(
            eventType: ListEventType.set,
            fullListValueForSet: [
              new TestFixture('1')..data = 'replaced',
              new TestFixture('2')
            ],
          ),
          // 1 2
          new KeyedListEvent(
            eventType: ListEventType.set,
            fullListValueForSet: [
              new TestFixture('1')..data = 'replaced again',
              new TestFixture('3')
            ],
          ),
          // 1 3
          new KeyedListEvent(
            eventType: ListEventType.set,
            fullListValueForSet: [
              new TestFixture('1')..data = 'preserved',
              new TestFixture('3'),
              new TestFixture('4')
            ],
          ),
        ]));

    list.activate();

    // Wait for all microtasks (listen()) to complete.
    await new Future(() {});

    expect(
        list,
        equals([
          new TestFixture('1')..data = 'preserved',
          new TestFixture('3'),
          new TestFixture('4'),
        ]));
  });

  test('read only interface', () {
    var list = new ViewModelsList(null);

    expect(() => list.setAt(0, null),
        throwsA(const isInstanceOf<UnsupportedError>()));
    expect(
        () => list.move(0, 1), throwsA(const isInstanceOf<UnsupportedError>()));
    expect(() => list.removeAt(0),
        throwsA(const isInstanceOf<UnsupportedError>()));
    expect(() => list.insert(0, null),
        throwsA(const isInstanceOf<UnsupportedError>()));
    expect(
        () => list[0] = null, throwsA(const isInstanceOf<UnsupportedError>()));
    expect(() => list.setAll(0, []),
        throwsA(const isInstanceOf<UnsupportedError>()));
  });

  test('activates items', () async {
    var testFixtures = [
      new TestFixture('1')..data = 'replaced',
      new TestFixture('2')..data = 'preserved',
    ];

    var addedTestFixture = new TestFixture('1')..data = 'preserved';

    var list = new ViewModelsList<TestFixture>(() => _listToStream([
          new KeyedListEvent(
            eventType: ListEventType.set,
            fullListValueForSet: testFixtures,
          ),
          new KeyedListEvent(
              eventType: ListEventType.itemChanged,
              previousSiblingKey: null,
              value: addedTestFixture),
        ]));

    expect(testFixtures[0].active, false);
    expect(testFixtures[1].active, false);
    expect(addedTestFixture.active, false);

    list.activate();

    // Wait for all microtasks (listen()) to complete.
    await new Future(() {});

    expect(testFixtures[0].active, false);
    expect(testFixtures[1].active, true);
    expect(addedTestFixture.active, true);

    list.deactivate();
    expect(testFixtures[0].active, false);
    expect(testFixtures[1].active, false);
    expect(addedTestFixture.active, false);

    list.dispose();
  });
}
