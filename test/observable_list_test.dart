import 'package:test/test.dart';

import '../lib/models/base/observable_list.dart';
import 'helpers.dart';

void main() {
  test('events', () {
    var list = new ObservableList<int>();

    expect(
        list.events,
        emitsInOrder([
          eventMatcher(ListEventType.set, 0),
          eventMatcher(ListEventType.itemAdded, 0),
          eventMatcher(ListEventType.itemAdded, 0),
          eventMatcher(ListEventType.itemAdded, 2),
          eventMatcher(ListEventType.itemRemoved, 1, 42),
          eventMatcher(ListEventType.itemAdded, 2),
          eventMatcher(ListEventType.itemAdded, 3),
          eventMatcher(ListEventType.itemAdded, 4),
          eventMatcher(ListEventType.itemMoved, 0),
          eventMatcher(ListEventType.itemMoved, 4),
          eventMatcher(ListEventType.itemChanged, 2, 2),
          eventMatcher(ListEventType.set, 0),
          emitsDone,
        ]));

    expect(list.changed, false);
    list.setAll(0, []);
    // Even an empty setAll must flip 'changed' because we use 'changed' as an
    // indicator that data has arrived (and therefore stop showing 'loading').
    // TODO(dotdoom): consider renaming 'changed' to 'dataArrived' or such
    expect(list.changed, true);
    list.add(42);
    expect(list.changed, true);
    expect(list, equals([42]));
    list.insert(0, 17);
    expect(list, equals([17, 42]));
    list.insert(2, -1);
    expect(list, equals([17, 42, -1]));
    list.removeAt(1);
    expect(list, equals([17, -1]));
    list.addAll([1, 2, 3]);
    expect(list, equals([17, -1, 1, 2, 3]));
    list.move(2, 0);
    expect(list, equals([1, 17, -1, 2, 3]));
    list.move(1, 5);
    expect(list, equals([1, -1, 2, 3, 17]));
    list.setAt(2, 0);
    expect(list, equals([1, -1, 0, 3, 17]));
    list.setAll(0, [1, 2, 3]);
    expect(list, equals([1, 2, 3]));

    list.dispose();
  });

  test('disallowed methods', () {
    var list = new ObservableList<int>();
    list.setAll(0, [1, -1, 0]);

    expect(() => list.length += 1,
        throwsA(const isInstanceOf<UnsupportedError>()));
    expect(list.sort, throwsA(const isInstanceOf<UnsupportedError>()));

    list.dispose();
    expect(() => list.add(0), throwsA(const isInstanceOf<StateError>()));
  });

  test('empty changes', () {
    var list = new ObservableList();

    list.move(0, 0);
    expect(list.changed, false);

    list.move(0, 1);
    expect(list.changed, false);
  });
}
