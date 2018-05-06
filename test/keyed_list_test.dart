import 'package:test/test.dart';

import '../lib/models/base/keyed_list.dart';
import '../lib/models/base/observable_list.dart';
import 'helpers.dart';

void main() {
  test('map', () {
    var intEvent = new KeyedListEvent(
      eventType: ListEventType.itemAdded,
      value: new TestFixture('1', data: '1'),
    ).map((f) => new TestFixture(f.key, data: int.parse(f.data) + 1));
    expect(intEvent.eventType, ListEventType.itemAdded);
    expect(intEvent.value, new TestFixture('1', data: 2));
    expect(intEvent.fullListValueForSet, null);

    var listEvent = new KeyedListEvent(
      eventType: ListEventType.set,
      fullListValueForSet: [new TestFixture('1', data: '1')],
    ).map((f) => new TestFixture(f.key, data: int.parse(f.data) + 1));
    expect(listEvent.eventType, ListEventType.set);
    expect(listEvent.value, null);
    expect(listEvent.fullListValueForSet, [new TestFixture('1', data: 2)]);
  });
}
