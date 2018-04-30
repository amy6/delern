import 'dart:async';

import 'package:test/test.dart';

import 'helpers.dart';
import '../lib/models/keyed_list.dart';
import '../lib/models/observable_list.dart';
import '../lib/view_models/proxy_keyed_list.dart';

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
          // Doesn't go through updateWith because underlying list is not a
          // ViewModelsList.
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

    list.dispose();
    baseList.dispose();
  });
}
