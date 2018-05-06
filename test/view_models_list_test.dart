import 'dart:async';

import 'package:test/test.dart';

import '../lib/models/base/keyed_list.dart';
import '../lib/models/base/observable_list.dart';
import '../lib/view_models/base/view_models_list.dart';
import 'helpers.dart';

void main() {
  test('initial setAll, add, remove, move, change', () async {
    ViewModelsList<TestFixture> list;
    list = new ViewModelsList<TestFixture>(() async* {
      // Initial set.
      yield new KeyedListEvent(
        eventType: ListEventType.set,
        fullListValueForSet: [new TestFixture('1')],
      );
      expect(list, equals([new TestFixture('1')]));

      // Add '1' again (should be ignored).
      yield new KeyedListEvent(
          eventType: ListEventType.itemAdded,
          previousSiblingKey: null,
          value: new TestFixture('1')..data = 'test');
      expect(list, equals([new TestFixture('1')]));

      // Add '3' after '1'.
      yield new KeyedListEvent(
          eventType: ListEventType.itemAdded,
          previousSiblingKey: '1',
          value: new TestFixture('3'));
      expect(list, equals([new TestFixture('1'), new TestFixture('3')]));

      // Add '2' after '1'.
      yield new KeyedListEvent(
        eventType: ListEventType.itemAdded,
        previousSiblingKey: '1',
        value: new TestFixture('2'),
      );
      expect(
          list,
          equals([
            new TestFixture('1'),
            new TestFixture('2'),
            new TestFixture('3')
          ]));

      // Swap '2' and '3'.
      yield new KeyedListEvent(
        eventType: ListEventType.itemMoved,
        value: new TestFixture('2'),
        previousSiblingKey: '3',
      );
      expect(
          list,
          equals([
            new TestFixture('1'),
            new TestFixture('3'),
            new TestFixture('2')
          ]));

      // Swap '2' and '3' again (backwards).
      yield new KeyedListEvent(
        eventType: ListEventType.itemMoved,
        value: new TestFixture('2'),
        previousSiblingKey: '1',
      );
      expect(
          list,
          equals([
            new TestFixture('1'),
            new TestFixture('2'),
            new TestFixture('3')
          ]));

      // Remove '2'.
      yield new KeyedListEvent(
          eventType: ListEventType.itemRemoved, value: new TestFixture('2'));
      expect(list, equals([new TestFixture('1'), new TestFixture('3')]));

      // Add data 'foo' to '1'.
      yield new KeyedListEvent(
          eventType: ListEventType.itemChanged,
          value: new TestFixture('1', data: 'foo'));
    })
      ..activate();

    await new Future<Null>(() {});
    expect(
        list,
        equals([
          new TestFixture('1', data: 'foo', updateCount: 1),
          new TestFixture('3')
        ]));
    list.dispose();
  });

  test('setAll', () async {
    ViewModelsList<TestFixture> list;
    list = new ViewModelsList<TestFixture>(() async* {
      yield new KeyedListEvent(
        eventType: ListEventType.set,
        fullListValueForSet: [
          new TestFixture('1', data: 'to be updated'),
          new TestFixture('2', data: 'to be removed'),
        ],
      );
      expect(
          list,
          equals([
            new TestFixture('1', data: 'to be updated'),
            new TestFixture('2', data: 'to be removed'),
          ]));

      yield new KeyedListEvent(
        eventType: ListEventType.set,
        fullListValueForSet: [
          new TestFixture('1', data: 'still to be updated'),
          new TestFixture('3', data: 'also to be removed'),
        ],
      );
      expect(
          list,
          equals([
            new TestFixture('1', data: 'still to be updated', updateCount: 1),
            new TestFixture('3', data: 'also to be removed'),
          ]));

      yield new KeyedListEvent(
        eventType: ListEventType.set,
        fullListValueForSet: [
          new TestFixture('1', data: 'no more changes'),
          new TestFixture('3'),
          new TestFixture('4'),
        ],
      );
    })
      ..activate();

    expect(
        list.events,
        emitsInOrder([
          // Apparently events are matched only at the end. Since ViewModelsList
          // reuses the objects, by the time of verification the data is final.
          eventMatcher(ListEventType.set, 0),
          eventMatcher(ListEventType.itemRemoved, 1,
              new TestFixture('2', data: 'to be removed')),
          eventMatcher(ListEventType.itemChanged, 0,
              new TestFixture('1', data: 'no more changes', updateCount: 2)),
          eventMatcher(ListEventType.itemAdded, 1),
          eventMatcher(ListEventType.itemChanged, 0,
              new TestFixture('1', data: 'no more changes', updateCount: 2)),
          eventMatcher(ListEventType.itemChanged, 1,
              new TestFixture('3', updateCount: 1)),
          eventMatcher(ListEventType.itemAdded, 2),
          emitsDone,
        ]));

    await new Future<Null>(() {});
    expect(
        list,
        equals([
          new TestFixture('1', data: 'no more changes', updateCount: 2),
          new TestFixture('3', updateCount: 1),
          new TestFixture('4'),
        ]));
    list.dispose();
  });

  test('activates / deactivates items', () async {
    var testFixtures = [
      new TestFixture('1', data: 'replaced'),
      new TestFixture('2', data: 'preserved'),
    ];

    var changedTestFixture = new TestFixture('1', data: 'preserved');
    var addedTestFixture = new TestFixture('3');

    ViewModelsList<TestFixture> list;
    list = new ViewModelsList<TestFixture>(() async* {
      yield new KeyedListEvent(
        eventType: ListEventType.set,
        fullListValueForSet: testFixtures,
      );
      expect(
          list,
          equals([
            new TestFixture('1', data: 'replaced'),
            new TestFixture('2', data: 'preserved'),
          ]));
      expect(testFixtures[0].active, true);
      expect(testFixtures[1].active, true);
      expect(changedTestFixture.active, false);
      expect(addedTestFixture.active, false);

      yield new KeyedListEvent(
          eventType: ListEventType.itemChanged, value: changedTestFixture);
      expect(
          list,
          equals([
            new TestFixture('1', data: 'preserved', updateCount: 1),
            new TestFixture('2', data: 'preserved'),
          ]));
      expect(testFixtures[0].active, true);
      expect(testFixtures[0],
          new TestFixture('1', data: 'preserved', updateCount: 1));
      expect(testFixtures[1].active, true);
      expect(changedTestFixture.active, false);
      expect(addedTestFixture.active, false);

      yield new KeyedListEvent(
          eventType: ListEventType.itemAdded,
          value: addedTestFixture,
          previousSiblingKey: '2');
      expect(
          list,
          equals([
            new TestFixture('1', data: 'preserved', updateCount: 1),
            new TestFixture('2', data: 'preserved'),
            new TestFixture('3'),
          ]));
      expect(testFixtures[0].active, true);
      expect(testFixtures[1].active, true);
      expect(changedTestFixture.active, false);
      expect(addedTestFixture.active, true);

      var childUpdatedTestFixture = new TestFixture('3', data: 'updated');
      list.childUpdated(childUpdatedTestFixture);
      expect(childUpdatedTestFixture.active, false);
      expect(addedTestFixture,
          new TestFixture('3', data: 'updated', updateCount: 1));

      yield new KeyedListEvent(
          eventType: ListEventType.itemRemoved, value: new TestFixture('1'));
      expect(
          list,
          equals([
            new TestFixture('2', data: 'preserved'),
            new TestFixture('3', data: 'updated', updateCount: 1),
          ]));
      expect(testFixtures[0].active, false);
      expect(testFixtures[1].active, true);
      expect(changedTestFixture.active, false);
      expect(addedTestFixture.active, true);
    })
      ..activate();

    expect(testFixtures[0].active, false);
    expect(testFixtures[1].active, false);
    expect(changedTestFixture.active, false);
    expect(addedTestFixture.active, false);

    await new Future<Null>(() {});
    list.deactivate();
    expect(
        list,
        equals([
          new TestFixture('2', data: 'preserved'),
          new TestFixture('3', data: 'updated', updateCount: 1),
        ]));
    expect(testFixtures[0].active, false);
    expect(testFixtures[1].active, false);
    expect(changedTestFixture.active, false);
    expect(addedTestFixture.active, false);
    list.dispose();
  });
}
