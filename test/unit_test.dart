import 'package:test/test.dart';

import '../lib/models/observable_list.dart';
import '../lib/models/sorted_observable_list.dart';
import '../lib/models/filtered_observable_list.dart';

void main() {
  test('observable list', () {
    var list = new ObservableList(<int>[]);
    list.add(42);
    list.insert(0, 17);
    list.insert(2, -1);
    list.removeAt(1);
    list.addAll(<int>[1, 2, 3]);

    expect(list[0], 17);
    expect(list[1], -1);
    expect(list[2], 1);
    expect(list[3], 2);
    expect(list[4], 3);
  });

  test('filtered list', () async {
    var base = new ObservableList(<int>[1, 2, 3, 4]);
    var list = new FilteredObservableList(base);

    expect(
        list.events,
        emitsInOrder([
          new ListEvent(eventType: ListEventType.added, index: 4),
          new ListEvent(
              eventType: ListEventType.removed, index: 0, previousValue: 1),
          new ListEvent(
              eventType: ListEventType.removed, index: 0, previousValue: 2),
        ]));

    base.add(5);
    list.filter = (x) => x > 2;
    base.move(0, 2);
    base.removeAt(2);

    expect(list.length, 3);
    expect(list[0], 3);
    expect(list[1], 4);
    expect(list[2], 5);
  });

  test('sorted list', () {
    var list = new SortedObservableList(new ObservableList(<int>[1, 3, 2, 4]));

    expect(
        list.events,
        emitsInOrder([
          new ListEvent(
              eventType: ListEventType.removed, index: 0, previousValue: 1),
          new ListEvent(
              eventType: ListEventType.removed, index: 0, previousValue: 3),
          new ListEvent(
              eventType: ListEventType.removed, index: 0, previousValue: 2),
          new ListEvent(
              eventType: ListEventType.removed, index: 0, previousValue: 4),
          new ListEvent(eventType: ListEventType.added, index: 0),
          new ListEvent(eventType: ListEventType.added, index: 1),
          new ListEvent(eventType: ListEventType.added, index: 2),
          new ListEvent(eventType: ListEventType.added, index: 3),
          new ListEvent(
              eventType: ListEventType.removed, index: 3, previousValue: 4),
        ]));

    list.comparator = (x, y) => x.compareTo(y);
    list.removeAt(3);
    list.move(0, 2);

    expect(list.length, 3);
    expect(list[0], 2);
    expect(list[1], 3);
    expect(list[2], 1);
  });

  test('filtered sorted list', () async {
    var original = new ObservableList(<int>[1, 3, -3, 2, 4, -2, 6, 5, 0, -1]);
    var filtered = new FilteredObservableList(original);
    var sorted = new SortedObservableList(filtered);

    filtered.filter = (x) => x >= 0;
    sorted.comparator = (x, y) => x.compareTo(y);

    expect(sorted.length, 7);
    expect(sorted[0], 0);
    expect(sorted[1], 1);
    expect(sorted[2], 2);
    expect(sorted[3], 3);
    expect(sorted[4], 4);
    expect(sorted[5], 5);
    expect(sorted[6], 6);
  });
}
