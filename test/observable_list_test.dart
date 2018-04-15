import 'package:test/test.dart';

import '../lib/models/observable_list.dart';

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

void main() {
  test('events', () {
    var list = new ObservableList<int>();

    expect(
        list.events,
        emitsInOrder([
          _eventMatcher(ListEventType.itemAdded, 0),
          _eventMatcher(ListEventType.itemAdded, 0),
          _eventMatcher(ListEventType.itemAdded, 2),
          _eventMatcher(ListEventType.itemRemoved, 1, 42),
          _eventMatcher(ListEventType.itemAdded, 2),
          _eventMatcher(ListEventType.itemAdded, 3),
          _eventMatcher(ListEventType.itemAdded, 4),
          _eventMatcher(ListEventType.set, 0),
          emitsDone,
        ]));

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

    list.setAll(0, <int>[1, 2, 3]);

    list.dispose();

    expect(() => list.add(0), throwsA(const isInstanceOf<StateError>()));
  });
}
