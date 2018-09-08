import 'package:delern_flutter/models/base/keyed_list.dart';
import 'package:delern_flutter/models/base/observable_list.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:mockito/mockito.dart';
import 'package:test/test.dart';

import 'helpers.dart';

class MockQuery extends Mock implements Query {}

void main() {
  test('map', () {
    var intEvent = KeyedListEvent(
      eventType: ListEventType.itemAdded,
      value: TestFixture('1', data: '1'),
    ).map((f) => TestFixture(f.key, data: int.parse(f.data) + 1));
    expect(intEvent.eventType, ListEventType.itemAdded);
    expect(intEvent.value, TestFixture('1', data: 2));
    expect(intEvent.fullListValueForSet, null);

    var listEvent = KeyedListEvent(
      eventType: ListEventType.setAll,
      fullListValueForSet: [TestFixture('1', data: '1')],
    ).map((f) => TestFixture(f.key, data: int.parse(f.data) + 1));
    expect(listEvent.eventType, ListEventType.setAll);
    expect(listEvent.value, null);
    expect(listEvent.fullListValueForSet, [TestFixture('1', data: 2)]);
  });

  test('event subscriptions', () {
    var query = MockQuery();
    childEventsStream(query, (s) => TestFixture(s.key, data: s.value));

    verify(query.onChildAdded).called(1);
    verify(query.onChildRemoved).called(1);
    verify(query.onChildMoved).called(1);
    verify(query.onChildChanged).called(1);
    verifyNever(query.onValue);
  });
}
