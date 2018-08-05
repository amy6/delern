import 'dart:async';

import 'package:test/test.dart';

import '../lib/models/base/keyed_list.dart';
import '../lib/models/base/observable_list.dart';
import '../lib/view_models/base/proxy_keyed_list.dart';
import '../lib/view_models/base/view_models_list.dart';
import 'helpers.dart';

void main() {
  test('setAll', () async {
    ProxyKeyedList<TestFixture> list;
    list = ProxyKeyedList<TestFixture>(ViewModelsList<TestFixture>(() async* {
      yield KeyedListEvent(
          eventType: ListEventType.setAll,
          fullListValueForSet: [
            TestFixture('A', data: 0),
            TestFixture('B', data: 1),
            TestFixture('C', data: 2),
            TestFixture('D', data: 3),
            TestFixture('E', data: 4),
          ]);
      expect(
          list,
          equals([
            TestFixture('A', data: 0),
            TestFixture('B', data: 1),
            TestFixture('C', data: 2),
            TestFixture('D', data: 3),
            TestFixture('E', data: 4),
          ]));

      list.filter = (f) => f.data > 0;
      expect(
          list,
          equals([
            TestFixture('B', data: 1),
            TestFixture('C', data: 2),
            TestFixture('D', data: 3),
            TestFixture('E', data: 4),
          ]));

      list.comparator = (a, b) => b.data.compareTo(a.data);
      expect(
          list,
          equals([
            TestFixture('E', data: 4),
            TestFixture('D', data: 3),
            TestFixture('C', data: 2),
            TestFixture('B', data: 1),
          ]));

      yield KeyedListEvent(
          eventType: ListEventType.setAll,
          fullListValueForSet: [
            TestFixture('A', data: 5),
            TestFixture('B', data: 4),
            TestFixture('C', data: 3),
            TestFixture('D', data: 2),
            TestFixture('E', data: 1),
            TestFixture('F', data: 0),
          ]);
      expect(
          list,
          equals([
            TestFixture('A', data: 5, updateCount: 1),
            TestFixture('B', data: 4, updateCount: 1),
            TestFixture('C', data: 3, updateCount: 1),
            TestFixture('D', data: 2, updateCount: 1),
            TestFixture('E', data: 1, updateCount: 1),
          ]));

      yield KeyedListEvent(
          eventType: ListEventType.setAll,
          fullListValueForSet: [
            TestFixture('F', data: 0),
            TestFixture('E', data: 1),
            TestFixture('A', data: 5),
            TestFixture('C', data: 3),
          ]);
      expect(
          list,
          equals([
            TestFixture('A', data: 5, updateCount: 2),
            TestFixture('C', data: 3, updateCount: 2),
            TestFixture('E', data: 1, updateCount: 2),
          ]));

      list.comparator = null;
      expect(
          list,
          equals([
            TestFixture('E', data: 1, updateCount: 2),
            TestFixture('A', data: 5, updateCount: 2),
            TestFixture('C', data: 3, updateCount: 2),
          ]));

      list.filter = null;
      expect(
          list,
          equals([
            TestFixture('F', data: 0, updateCount: 1),
            TestFixture('E', data: 1, updateCount: 2),
            TestFixture('A', data: 5, updateCount: 2),
            TestFixture('C', data: 3, updateCount: 2),
          ]));
    })
      ..activate());

    // Wait for all microtasks (listen()) to complete.
    await Future(() {});

    expect(
        list,
        equals([
          TestFixture('F', data: 0, updateCount: 1),
          TestFixture('E', data: 1, updateCount: 2),
          TestFixture('A', data: 5, updateCount: 2),
          TestFixture('C', data: 3, updateCount: 2),
        ]));

    list.dispose();
  });
}
