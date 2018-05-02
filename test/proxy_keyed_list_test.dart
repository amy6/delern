import 'dart:async';

import 'package:test/test.dart';

import '../lib/models/observable_list.dart';
import '../lib/view_models/proxy_keyed_list.dart';
import 'helpers.dart';

void main() {
  test('filters & sorts', () async {
    var baseList = new ObservableList<TestFixture>();
    var list = new ProxyKeyedList<TestFixture>(baseList);

    expect(
        list.events,
        emitsInOrder([
          // Initial set.
          eventMatcher(ListEventType.set, 0),
          // Filter applied.
          eventMatcher(ListEventType.itemRemoved, 1, new TestFixture('B')),
          eventMatcher(ListEventType.itemRemoved, 1, new TestFixture('C')),
          eventMatcher(ListEventType.itemRemoved, 1, new TestFixture('E')),
          // Sort applied.
          eventMatcher(ListEventType.set, 0),
          // Item that passes the filter is added.
          eventMatcher(ListEventType.itemAdded, 1),
          // Item that passed the filter is removed.
          eventMatcher(ListEventType.itemRemoved, 2, new TestFixture('A')),
          // Filter updated to allow a few more items, and disallow one.
          eventMatcher(ListEventType.itemAdded, 2),
          eventMatcher(ListEventType.itemAdded, 2),
          eventMatcher(ListEventType.itemAdded, 1),
          eventMatcher(ListEventType.itemRemoved, 2, new TestFixture('D')),
          // Item changed.
          eventMatcher(ListEventType.itemChanged, 3, new TestFixture('B')),
          // Sort removed.
          eventMatcher(ListEventType.set, 0),
          // Item moved again.
          eventMatcher(ListEventType.itemMoved, 2),
          // Filter removed.
          eventMatcher(ListEventType.itemAdded, 4),
          emitsDone,
        ]));

    // Wait for all microtasks (listen()) to complete.
    await new Future(() {});

    baseList.setAll(0, [
      new TestFixture('A'),
      new TestFixture('B'),
      new TestFixture('C'),
      new TestFixture('E'),
      new TestFixture('F'),
    ]);
    expect(
        list,
        equals([
          new TestFixture('A'),
          new TestFixture('B'),
          new TestFixture('C'),
          new TestFixture('E'),
          new TestFixture('F'),
        ]));
    expect(list, baseList);

    list.filter = (f) => f.key != 'B' && f.key != 'C' && f.key != 'E';
    expect(
        list,
        equals([
          new TestFixture('A'),
          new TestFixture('F'),
        ]));

    list.comparator = (a, b) => b.key.compareTo(a.key);
    expect(
        list,
        equals([
          new TestFixture('F'),
          new TestFixture('A'),
        ]));

    baseList.add(new TestFixture('D'));
    expect(
        list,
        equals([
          new TestFixture('F'),
          new TestFixture('D'),
          new TestFixture('A'),
        ]));

    baseList.removeAt(0);
    expect(
        list,
        equals([
          new TestFixture('F'),
          new TestFixture('D'),
        ]));

    list.filter = (f) => f.key != 'D';
    expect(
        list,
        equals([
          new TestFixture('F'),
          new TestFixture('E'),
          new TestFixture('C'),
          new TestFixture('B'),
        ]));

    baseList.setAt(0, new TestFixture('B', data: 'updated'));
    expect(
        list,
        equals([
          new TestFixture('F'),
          new TestFixture('E'),
          new TestFixture('C'),
          // Doesn't go through updateWith, because underlying list is not a
          // ViewModelsList. Therefore, updateCount is not increased.
          new TestFixture('B', data: 'updated'),
        ]));

    baseList.move(2, 3);
    expect(
        list,
        equals([
          new TestFixture('F'),
          new TestFixture('E'),
          new TestFixture('C'),
          new TestFixture('B', data: 'updated'),
        ]));

    list.comparator = null;
    expect(
        list,
        equals([
          new TestFixture('B', data: 'updated'),
          new TestFixture('C'),
          new TestFixture('F'),
          new TestFixture('E'),
        ]));

    baseList.move(3, 2);
    expect(
        list,
        equals([
          new TestFixture('B', data: 'updated'),
          new TestFixture('C'),
          new TestFixture('E'),
          new TestFixture('F'),
        ]));

    list.filter = null;
    expect(list, baseList);

    list.dispose();
    baseList.dispose();
  });

  test('setAt with data, move', () async {
    var baseList = new ObservableList<TestFixture>();
    baseList.setAll(0, [
      new TestFixture('A', data: 0),
      new TestFixture('B', data: 1),
      new TestFixture('C', data: 2),
      new TestFixture('D', data: 3),
      new TestFixture('E', data: 4),
    ]);
    var list = new ProxyKeyedList<TestFixture>(baseList);

    expect(
        list.events,
        emitsInOrder([
          // Filter removes 1 item.
          eventMatcher(
              ListEventType.itemRemoved, 0, new TestFixture('A', data: 0)),
          // Sort is applied.
          eventMatcher(ListEventType.set, 0),
          // Item that passes the filter is added.
          eventMatcher(ListEventType.itemAdded, 0),
          // Item is changed and this change moves it per the order defined.
          eventMatcher(
              ListEventType.itemChanged, 3, new TestFixture('C', data: 2)),
          eventMatcher(ListEventType.itemMoved, 0),
          // Item is changed and no longer passes the filter.
          eventMatcher(
              ListEventType.itemChanged, 1, new TestFixture('F', data: 5)),
          eventMatcher(
              ListEventType.itemRemoved, 1, new TestFixture('F', data: 0)),
          // Item is changed and starts passing the filter.
          eventMatcher(ListEventType.itemAdded, 1),
          // Sorting is removed.
          eventMatcher(ListEventType.set, 0),
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
    await new Future(() {});

    expect(list, baseList);

    list.filter = (f) => f.data > 0;
    expect(
        list,
        equals([
          new TestFixture('B', data: 1),
          new TestFixture('C', data: 2),
          new TestFixture('D', data: 3),
          new TestFixture('E', data: 4),
        ]));

    list.comparator = (a, b) => b.data.compareTo(a.data);
    expect(
        list,
        equals([
          new TestFixture('E', data: 4),
          new TestFixture('D', data: 3),
          new TestFixture('C', data: 2),
          new TestFixture('B', data: 1),
        ]));

    baseList.add(new TestFixture('F', data: 5));
    expect(
        list,
        equals([
          new TestFixture('F', data: 5),
          new TestFixture('E', data: 4),
          new TestFixture('D', data: 3),
          new TestFixture('C', data: 2),
          new TestFixture('B', data: 1),
        ]));

    baseList.setAt(2, new TestFixture('C', data: 7));
    expect(
        list,
        equals([
          new TestFixture('C', data: 7),
          new TestFixture('F', data: 5),
          new TestFixture('E', data: 4),
          new TestFixture('D', data: 3),
          new TestFixture('B', data: 1),
        ]));

    baseList.setAt(5, new TestFixture('F', data: 0));
    expect(
        list,
        equals([
          new TestFixture('C', data: 7),
          new TestFixture('E', data: 4),
          new TestFixture('D', data: 3),
          new TestFixture('B', data: 1),
        ]));

    baseList.setAt(0, new TestFixture('A', data: 6));
    expect(
        list,
        equals([
          new TestFixture('C', data: 7),
          new TestFixture('A', data: 6),
          new TestFixture('E', data: 4),
          new TestFixture('D', data: 3),
          new TestFixture('B', data: 1),
        ]));

    list.comparator = null;
    expect(
        list,
        equals([
          new TestFixture('A', data: 6),
          new TestFixture('B', data: 1),
          new TestFixture('C', data: 7),
          new TestFixture('D', data: 3),
          new TestFixture('E', data: 4),
        ]));

    baseList.move(0, 5);
    expect(
        list,
        equals([
          new TestFixture('B', data: 1),
          new TestFixture('C', data: 7),
          new TestFixture('D', data: 3),
          new TestFixture('E', data: 4),
          new TestFixture('A', data: 6),
        ]));

    baseList.move(4, 0);
    expect(
        list,
        equals([
          new TestFixture('B', data: 1),
          new TestFixture('C', data: 7),
          new TestFixture('D', data: 3),
          new TestFixture('E', data: 4),
          new TestFixture('A', data: 6),
        ]));

    baseList.move(1, 2);
    expect(
        list,
        equals([
          new TestFixture('C', data: 7),
          new TestFixture('B', data: 1),
          new TestFixture('D', data: 3),
          new TestFixture('E', data: 4),
          new TestFixture('A', data: 6),
        ]));

    list.filter = null;
    expect(list, baseList);

    list.dispose();
    baseList.dispose();
  });
}
