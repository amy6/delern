import 'dart:async';

import 'package:test/test.dart';

import '../lib/models/keyed_list.dart';
import '../lib/models/observable_list.dart';
import '../lib/view_models/proxy_keyed_list.dart';

StreamMatcher _eventMatcher(ListEventType eventType, int index,
    [previousValue]) {
  var expected = new ListEvent(
      eventType: eventType, index: index, previousValue: previousValue);
  return new StreamMatcher((q) async {
    if (!await q.hasNext) return '';

    ListEvent actual = await q.next;
    if (actual.eventType == expected.eventType &&
        actual.index == expected.index &&
        actual.previousValue == expected.previousValue) {
      return null;
    }

    return 'emitted $actual';
  }, 'match $expected');
}

class TestFixture implements KeyedListItem {
  final String key;
  TestFixture(this.key);

  @override
  bool operator ==(other) => (other is TestFixture) && key == other.key;

  @override
  int get hashCode => key.hashCode;

  @override
  String toString() => key;
}

void main() {
  test('filters & sorts', () async {
    var baseList = new ObservableList<TestFixture>();
    var list = new ProxyKeyedList<TestFixture>(baseList);

    expect(
        list.events,
        emitsInOrder([
          // Initial set.
          _eventMatcher(ListEventType.set, 0),
          // Filter applied.
          _eventMatcher(ListEventType.itemRemoved, 1, new TestFixture('B')),
          _eventMatcher(ListEventType.itemRemoved, 1, new TestFixture('C')),
          _eventMatcher(ListEventType.itemRemoved, 1, new TestFixture('E')),
          // Sort applied.
          _eventMatcher(ListEventType.set, 0),
          // Item that passes the filter is added.
          _eventMatcher(ListEventType.itemAdded, 1),
          // Item that passed the filter is removed.
          _eventMatcher(ListEventType.itemRemoved, 2, new TestFixture('A')),
          // Filter updated to allow a few more items, and disallow one.
          _eventMatcher(ListEventType.itemAdded, 2),
          _eventMatcher(ListEventType.itemAdded, 2),
          _eventMatcher(ListEventType.itemAdded, 1),
          _eventMatcher(ListEventType.itemRemoved, 2, new TestFixture('D')),
          // Sort removed.
          _eventMatcher(ListEventType.set, 0),
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

    list.comparator = null;
    expect(
        list,
        equals([
          new TestFixture('B'),
          new TestFixture('C'),
          new TestFixture('E'),
          new TestFixture('F'),
        ]));

    list.dispose();
    baseList.dispose();
  });
}
