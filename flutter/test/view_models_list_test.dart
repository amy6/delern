import 'dart:async';

import 'package:delern_flutter/models/base/keyed_list.dart';
import 'package:delern_flutter/models/base/observable_list.dart';
import 'package:delern_flutter/view_models/base/view_models_list.dart';
import 'package:test/test.dart';

import 'helpers.dart';

void main() {
  test('initial setAll, add, remove, move, change', () async {
    ViewModelsList<TestFixture> list;
    list = ViewModelsList<TestFixture>(() async* {
      // Initial set.
      yield KeyedListEvent(
        eventType: ListEventType.setAll,
        fullListValueForSet: [TestFixture('1')],
      );
      expect(list, equals([TestFixture('1')]));

      // Add '1' again (should be ignored).
      yield KeyedListEvent(
          eventType: ListEventType.itemAdded,
          previousSiblingKey: null,
          value: TestFixture('1')..data = 'test');
      expect(list, equals([TestFixture('1')]));

      // Add '3' after '1'.
      yield KeyedListEvent(
          eventType: ListEventType.itemAdded,
          previousSiblingKey: '1',
          value: TestFixture('3'));
      expect(list, equals([TestFixture('1'), TestFixture('3')]));

      // Add '3' after '1' again (should be ignored).
      yield KeyedListEvent(
          eventType: ListEventType.itemAdded,
          previousSiblingKey: '1',
          value: TestFixture('3'));
      expect(list, equals([TestFixture('1'), TestFixture('3')]));

      // Add '2' after '1'.
      yield KeyedListEvent(
        eventType: ListEventType.itemAdded,
        previousSiblingKey: '1',
        value: TestFixture('2'),
      );
      expect(
          list, equals([TestFixture('1'), TestFixture('2'), TestFixture('3')]));

      // Swap '2' and '3'.
      yield KeyedListEvent(
        eventType: ListEventType.itemMoved,
        value: TestFixture('2'),
        previousSiblingKey: '3',
      );
      expect(
          list, equals([TestFixture('1'), TestFixture('3'), TestFixture('2')]));

      // Swap '2' and '3' again (backwards).
      yield KeyedListEvent(
        eventType: ListEventType.itemMoved,
        value: TestFixture('2'),
        previousSiblingKey: '1',
      );
      expect(
          list, equals([TestFixture('1'), TestFixture('2'), TestFixture('3')]));

      // Remove '2'.
      yield KeyedListEvent(
          eventType: ListEventType.itemRemoved, value: TestFixture('2'));
      expect(list, equals([TestFixture('1'), TestFixture('3')]));

      // Add data 'foo' to '1'.
      yield KeyedListEvent(
          eventType: ListEventType.itemChanged,
          value: TestFixture('1', data: 'foo'));
    })
      ..activate();

    await Future<Null>(() {});
    expect(
        list,
        equals(
            [TestFixture('1', data: 'foo', updateCount: 1), TestFixture('3')]));
    list.dispose();
  });

  test('setAll', () async {
    ViewModelsList<TestFixture> list;
    list = ViewModelsList<TestFixture>(() async* {
      yield KeyedListEvent(
        eventType: ListEventType.setAll,
        fullListValueForSet: [
          TestFixture('1', data: 'to be updated'),
          TestFixture('2', data: 'to be removed'),
        ],
      );
      expect(
          list,
          equals([
            TestFixture('1', data: 'to be updated'),
            TestFixture('2', data: 'to be removed'),
          ]));

      yield KeyedListEvent(
        eventType: ListEventType.setAll,
        fullListValueForSet: [
          TestFixture('1', data: 'still to be updated'),
          TestFixture('3', data: 'also to be removed'),
        ],
      );
      expect(
          list,
          equals([
            TestFixture('1', data: 'still to be updated', updateCount: 1),
            TestFixture('3', data: 'also to be removed'),
          ]));

      yield KeyedListEvent(
        eventType: ListEventType.setAll,
        fullListValueForSet: [
          TestFixture('1', data: 'no more changes'),
          TestFixture('3'),
          TestFixture('4'),
        ],
      );
    })
      ..activate();

    expect(
        list.events,
        emitsInOrder([
          // Apparently events are matched only at the end. Since ViewModelsList
          // reuses the objects, by the time of verification the data is final.
          eventMatcher(ListEventType.setAll, 0),
          eventMatcher(ListEventType.itemRemoved, 1,
              TestFixture('2', data: 'to be removed')),
          eventMatcher(ListEventType.itemChanged, 0,
              TestFixture('1', data: 'no more changes', updateCount: 2)),
          eventMatcher(ListEventType.itemAdded, 1),
          eventMatcher(ListEventType.itemChanged, 0,
              TestFixture('1', data: 'no more changes', updateCount: 2)),
          eventMatcher(
              ListEventType.itemChanged, 1, TestFixture('3', updateCount: 1)),
          eventMatcher(ListEventType.itemAdded, 2),
          emitsDone,
        ]));

    await Future<Null>(() {});
    expect(
        list,
        equals([
          TestFixture('1', data: 'no more changes', updateCount: 2),
          TestFixture('3', updateCount: 1),
          TestFixture('4'),
        ]));
    list.dispose();
  });

  test('activates / deactivates items', () async {
    var testFixtures = [
      TestFixture('1', data: 'replaced'),
      TestFixture('2', data: 'preserved'),
    ];

    var changedTestFixture = TestFixture('1', data: 'preserved');
    var addedTestFixture = TestFixture('3');

    ViewModelsList<TestFixture> list;
    list = ViewModelsList<TestFixture>(() async* {
      yield KeyedListEvent(
        eventType: ListEventType.setAll,
        fullListValueForSet: testFixtures,
      );
      expect(
          list,
          equals([
            TestFixture('1', data: 'replaced'),
            TestFixture('2', data: 'preserved'),
          ]));
      expect(testFixtures[0].active, true);
      expect(testFixtures[1].active, true);
      expect(changedTestFixture.active, false);
      expect(addedTestFixture.active, false);

      yield KeyedListEvent(
          eventType: ListEventType.itemChanged, value: changedTestFixture);
      expect(
          list,
          equals([
            TestFixture('1', data: 'preserved', updateCount: 1),
            TestFixture('2', data: 'preserved'),
          ]));
      expect(testFixtures[0].active, true);
      expect(
          testFixtures[0], TestFixture('1', data: 'preserved', updateCount: 1));
      expect(testFixtures[1].active, true);
      expect(changedTestFixture.active, false);
      expect(addedTestFixture.active, false);

      yield KeyedListEvent(
          eventType: ListEventType.itemAdded,
          value: addedTestFixture,
          previousSiblingKey: '2');
      expect(
          list,
          equals([
            TestFixture('1', data: 'preserved', updateCount: 1),
            TestFixture('2', data: 'preserved'),
            TestFixture('3'),
          ]));
      expect(testFixtures[0].active, true);
      expect(testFixtures[1].active, true);
      expect(changedTestFixture.active, false);
      expect(addedTestFixture.active, true);

      var childUpdatedTestFixture = TestFixture('3', data: 'updated');
      list.childUpdated(childUpdatedTestFixture);
      expect(childUpdatedTestFixture.active, false);
      expect(
          addedTestFixture, TestFixture('3', data: 'updated', updateCount: 1));

      yield KeyedListEvent(
          eventType: ListEventType.itemRemoved, value: TestFixture('1'));
      expect(
          list,
          equals([
            TestFixture('2', data: 'preserved'),
            TestFixture('3', data: 'updated', updateCount: 1),
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

    await Future<Null>(() {});
    list.deactivate();
    expect(
        list,
        equals([
          TestFixture('2', data: 'preserved'),
          TestFixture('3', data: 'updated', updateCount: 1),
        ]));
    expect(testFixtures[0].active, false);
    expect(testFixtures[1].active, false);
    expect(changedTestFixture.active, false);
    expect(addedTestFixture.active, false);
    list.dispose();
  });
}
