import 'package:test/test.dart';

import 'helpers.dart';
import '../lib/models/observable_list.dart';

void main() {
  test('events', () {
    var list = new ObservableList<int>();

    expect(
        list.events,
        emitsInOrder([
          eventMatcher(ListEventType.itemAdded, 0),
          eventMatcher(ListEventType.itemAdded, 0),
          eventMatcher(ListEventType.itemAdded, 2),
          eventMatcher(ListEventType.itemRemoved, 1, 42),
          eventMatcher(ListEventType.itemAdded, 2),
          eventMatcher(ListEventType.itemAdded, 3),
          eventMatcher(ListEventType.itemAdded, 4),
          eventMatcher(ListEventType.itemMoved, 0),
          eventMatcher(ListEventType.itemMoved, 2),
          eventMatcher(ListEventType.itemChanged, 2, 17),
          eventMatcher(ListEventType.set, 0),
          emitsDone,
        ]));

    expect(list.changed, equals(false));
    list.add(42);
    expect(list.changed, true);

    // 42
    list.insert(0, 17);
    // 17 42
    list.insert(2, -1);
    // 17 42 -1
    list.removeAt(1);
    // 17 -1
    list.addAll(<int>[1, 2, 3]);
    // 17 -1 1 2 3
    list.move(2, 0);
    // 1 17 -1 2 3
    list.move(1, 2);
    // 1 -1 17 2 3
    list.setAt(2, 0);
    expect(list, equals(<int>[1, -1, 0, 2, 3]));

    list.setAll(0, <int>[1, 2, 3]);
    expect(list, equals(<int>[1, 2, 3]));

    list.dispose();
  });

  test('disallowed methods', () {
    var list = new ObservableList<int>();
    list.setAll(0, <int>[1, -1, 0]);

    expect(() => list.length += 1,
        throwsA(const isInstanceOf<UnsupportedError>()));
    expect(list.sort, throwsA(const isInstanceOf<UnsupportedError>()));

    list.dispose();
    expect(() => list.add(0), throwsA(const isInstanceOf<StateError>()));
  });
}
