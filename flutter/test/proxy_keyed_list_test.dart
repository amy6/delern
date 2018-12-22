import 'dart:async';

import 'package:delern_flutter/models/base/observable_list.dart';
import 'package:delern_flutter/view_models/base/proxy_keyed_list.dart';
import 'package:test/test.dart';

import 'helpers.dart';

void main() {
  test('filters & sorts', () async {
    var baseList = ObservableList<TestFixture>();
    var list = ProxyKeyedList<TestFixture>(baseList);

    expect(
        list.events,
        emitsInOrder([
          // Initial set.
          eventMatcher(ListEventType.setAll, 0),
          // Filter applied.
          eventMatcher(ListEventType.itemRemoved, 1, TestFixture('B')),
          eventMatcher(ListEventType.itemRemoved, 1, TestFixture('C')),
          eventMatcher(ListEventType.itemRemoved, 1, TestFixture('E')),
          // Sort applied.
          eventMatcher(ListEventType.setAll, 0),
          // Item that passes the filter is added.
          eventMatcher(ListEventType.itemAdded, 1),
          // Item that passed the filter is removed.
          eventMatcher(ListEventType.itemRemoved, 2, TestFixture('A')),
          // Filter updated to allow a few more items, and disallow one.
          eventMatcher(ListEventType.itemAdded, 2),
          eventMatcher(ListEventType.itemAdded, 2),
          eventMatcher(ListEventType.itemAdded, 1),
          eventMatcher(ListEventType.itemRemoved, 2, TestFixture('D')),
          // Item changed.
          eventMatcher(ListEventType.itemChanged, 3, TestFixture('B')),
          // Sort removed.
          eventMatcher(ListEventType.setAll, 0),
          // Item moved again.
          eventMatcher(ListEventType.itemMoved, 2),
          // Filter removed.
          eventMatcher(ListEventType.itemAdded, 4),
          emitsDone,
        ]));

    // Wait for all microtasks (listen()) to complete.
    await Future(() {});

    baseList.setAll(0, [
      TestFixture('A'),
      TestFixture('B'),
      TestFixture('C'),
      TestFixture('E'),
      TestFixture('F'),
    ]);
    expect(
        list,
        equals([
          TestFixture('A'),
          TestFixture('B'),
          TestFixture('C'),
          TestFixture('E'),
          TestFixture('F'),
        ]));
    expect(list, baseList);

    list.filter = (f) => f.key != 'B' && f.key != 'C' && f.key != 'E';
    expect(list.filter, isNotNull);
    expect(
        list,
        equals([
          TestFixture('A'),
          TestFixture('F'),
        ]));

    list.comparator = (a, b) => b.key.compareTo(a.key);
    expect(
        list,
        equals([
          TestFixture('F'),
          TestFixture('A'),
        ]));

    baseList.add(TestFixture('D'));
    expect(
        list,
        equals([
          TestFixture('F'),
          TestFixture('D'),
          TestFixture('A'),
        ]));

    baseList.removeAt(0);
    expect(
        list,
        equals([
          TestFixture('F'),
          TestFixture('D'),
        ]));

    list.filter = (f) => f.key != 'D';
    expect(
        list,
        equals([
          TestFixture('F'),
          TestFixture('E'),
          TestFixture('C'),
          TestFixture('B'),
        ]));

    baseList.setAt(0, TestFixture('B', data: 'updated'));
    expect(
        list,
        equals([
          TestFixture('F'),
          TestFixture('E'),
          TestFixture('C'),
          // Doesn't go through updateWith, because underlying list is not a
          // ViewModelsList. Therefore, updateCount is not increased.
          TestFixture('B', data: 'updated'),
        ]));

    baseList.move(2, 4);
    expect(
        list,
        equals([
          TestFixture('F'),
          TestFixture('E'),
          TestFixture('C'),
          TestFixture('B', data: 'updated'),
        ]));

    list.comparator = null;
    expect(
        list,
        equals([
          TestFixture('B', data: 'updated'),
          TestFixture('C'),
          TestFixture('F'),
          TestFixture('E'),
        ]));

    baseList.move(3, 2);
    expect(
        list,
        equals([
          TestFixture('B', data: 'updated'),
          TestFixture('C'),
          TestFixture('E'),
          TestFixture('F'),
        ]));

    list.filter = null;
    expect(list, baseList);

    list.dispose();
    baseList.dispose();
  });

  test('setAt with data, move', () async {
    var baseList = ObservableList<TestFixture>()
      ..setAll(0, [
        TestFixture('A', data: 0),
        TestFixture('B', data: 1),
        TestFixture('C', data: 2),
        TestFixture('D', data: 3),
        TestFixture('E', data: 4),
      ]);
    var list = ProxyKeyedList<TestFixture>(baseList);

    expect(
        list.events,
        emitsInOrder([
          // Filter removes 1 item.
          eventMatcher(ListEventType.itemRemoved, 0, TestFixture('A', data: 0)),
          // Sort is applied.
          eventMatcher(ListEventType.setAll, 0),
          // Item that passes the filter is added.
          eventMatcher(ListEventType.itemAdded, 0),
          // Item is changed and this change moves it per the order defined.
          eventMatcher(ListEventType.itemChanged, 3, TestFixture('C', data: 2)),
          eventMatcher(ListEventType.itemMoved, 0),
          // Item is changed and no longer passes the filter.
          eventMatcher(ListEventType.itemChanged, 1, TestFixture('F', data: 5)),
          eventMatcher(ListEventType.itemRemoved, 1, TestFixture('F', data: 0)),
          // Item is changed and starts passing the filter.
          eventMatcher(ListEventType.itemAdded, 1),
          // Sorting is removed.
          eventMatcher(ListEventType.setAll, 0),
          // Item is moved.
          eventMatcher(ListEventType.itemMoved, 4),
          // Item is moved but it doesn't affect the list because it's filtered.
          // Another item is moved.
          eventMatcher(ListEventType.itemMoved, 1),
          // Filter is removed which lets another item in.
          eventMatcher(ListEventType.itemAdded, 0),
          emitsDone,
        ]));

    // Wait for all microtasks (listen()) to complete.
    await Future(() {});

    expect(list, baseList);

    list.filter = (f) => f.data > 0;
    expect(
        list,
        equals([
          TestFixture('B', data: 1),
          TestFixture('C', data: 2),
          TestFixture('D', data: 3),
          TestFixture('E', data: 4),
        ]));

    list.comparator = (a, b) => b.data.compareTo(a.data);
    expect(
        list,
        equals([
          TestFixture('E', data: 4),
          TestFixture('D', data: 3),
          TestFixture('C', data: 2),
          TestFixture('B', data: 1),
        ]));

    baseList.add(TestFixture('F', data: 5));
    expect(
        list,
        equals([
          TestFixture('F', data: 5),
          TestFixture('E', data: 4),
          TestFixture('D', data: 3),
          TestFixture('C', data: 2),
          TestFixture('B', data: 1),
        ]));

    baseList.setAt(2, TestFixture('C', data: 7));
    expect(
        list,
        equals([
          TestFixture('C', data: 7),
          TestFixture('F', data: 5),
          TestFixture('E', data: 4),
          TestFixture('D', data: 3),
          TestFixture('B', data: 1),
        ]));

    baseList.setAt(5, TestFixture('F', data: 0));
    expect(
        list,
        equals([
          TestFixture('C', data: 7),
          TestFixture('E', data: 4),
          TestFixture('D', data: 3),
          TestFixture('B', data: 1),
        ]));

    baseList.setAt(0, TestFixture('A', data: 6));
    expect(
        list,
        equals([
          TestFixture('C', data: 7),
          TestFixture('A', data: 6),
          TestFixture('E', data: 4),
          TestFixture('D', data: 3),
          TestFixture('B', data: 1),
        ]));

    list.comparator = null;
    expect(
        list,
        equals([
          TestFixture('A', data: 6),
          TestFixture('B', data: 1),
          TestFixture('C', data: 7),
          TestFixture('D', data: 3),
          TestFixture('E', data: 4),
        ]));

    baseList.move(0, 6);
    expect(
        list,
        equals([
          TestFixture('B', data: 1),
          TestFixture('C', data: 7),
          TestFixture('D', data: 3),
          TestFixture('E', data: 4),
          TestFixture('A', data: 6),
        ]));

    baseList.move(4, 0);
    expect(
        list,
        equals([
          TestFixture('B', data: 1),
          TestFixture('C', data: 7),
          TestFixture('D', data: 3),
          TestFixture('E', data: 4),
          TestFixture('A', data: 6),
        ]));

    baseList.move(1, 3);
    expect(
        list,
        equals([
          TestFixture('C', data: 7),
          TestFixture('B', data: 1),
          TestFixture('D', data: 3),
          TestFixture('E', data: 4),
          TestFixture('A', data: 6),
        ]));

    list.filter = null;
    expect(list, baseList);

    list.dispose();
    baseList.dispose();
  });

  test('initial setAll with filter and comparator', () async {
    var baseList = ObservableList<TestFixture>();
    var list = ProxyKeyedList<TestFixture>(baseList);

    expect(
        list.events,
        emitsInOrder([
          // Initial set.
          eventMatcher(ListEventType.setAll, 0),
          // Filter removed.
          eventMatcher(ListEventType.itemAdded, 4),
          // Sort removed.
          eventMatcher(ListEventType.setAll, 0),
          emitsDone,
        ]));

    // Wait for all microtasks (listen()) to complete.
    await Future(() {});

    expect(list, baseList);

    list.filter = (f) => f.data > 0;
    // ignore: cascade_invocations
    list.comparator = (a, b) => b.data.compareTo(a.data);

    expect(list, baseList);
    baseList.setAll(0, [
      TestFixture('A', data: 0),
      TestFixture('B', data: 1),
      TestFixture('C', data: 2),
      TestFixture('D', data: 3),
      TestFixture('E', data: 4),
    ]);
    expect(
        list,
        equals([
          TestFixture('E', data: 4),
          TestFixture('D', data: 3),
          TestFixture('C', data: 2),
          TestFixture('B', data: 1),
        ]));

    list
      ..filter = null
      ..comparator = null;
    expect(list, baseList);

    list.dispose();
    baseList.dispose();
  });
}
