import 'package:delern_flutter/models/base/keyed_list.dart';
import 'package:delern_flutter/models/base/observable_list.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:mockito/mockito.dart';
import 'package:test/test.dart';

import 'helpers.dart';

class MockDataSnapshot extends Mock implements DataSnapshot {
  String get key => 'testKey';
}

class MockEvent extends Mock implements Event {
  @override
  DataSnapshot get snapshot => MockDataSnapshot();
}

Stream<Event> _singleEvent() async* {
  yield MockEvent();
}

class MockQuery extends Mock implements Query {
  @override
  Stream<Event> get onChildAdded => _singleEvent();

  @override
  Stream<Event> get onChildRemoved => _singleEvent();

  @override
  Stream<Event> get onChildMoved => _singleEvent();

  @override
  Stream<Event> get onChildChanged => _singleEvent();
}

void main() {
  test('map', () {
    var intEvent = DatabaseListEvent(
      eventType: ListEventType.itemAdded,
      value: TestFixture('1', data: '1'),
    ).map((f) => TestFixture(f.key, data: int.parse(f.data) + 1));
    expect(intEvent.eventType, ListEventType.itemAdded);
    expect(intEvent.value, TestFixture('1', data: 2));
    expect(intEvent.fullListValueForSet, null);

    var listEvent = DatabaseListEvent(
      eventType: ListEventType.setAll,
      fullListValueForSet: [TestFixture('1', data: '1')],
    ).map((f) => TestFixture(f.key, data: int.parse(f.data) + 1));
    expect(listEvent.eventType, ListEventType.setAll);
    expect(listEvent.value, null);
    expect(listEvent.fullListValueForSet, [TestFixture('1', data: 2)]);
  });

  test('event subscriptions', () async {
    var query = MockQuery();

    final events =
        await childEventsStream(query, (snapshot) => TestFixture(snapshot.key))
            .toList();

    final eventTypesLeft = ListEventType.values.toList()
      ..removeAt(ListEventType.setAll);

    for (final event in events) {
      expect(event.value.key, 'testKey');
      expect(event.fullListValueForSet, null);
      expect(event.previousSiblingKey, null);
      expect(eventTypesLeft, contains(event.eventType));
      expect(event.toString(), contains('#testKey'));
      eventTypesLeft.removeAt(event.eventType);
    }

    // Verify that all events have triggered.
    expect(eventTypesLeft, isEmpty);
    verifyNever(query.onValue);
  });
}
