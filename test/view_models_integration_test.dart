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
    list = new ProxyKeyedList<TestFixture>(
        new ViewModelsList<TestFixture>(() async* {
      yield new KeyedListEvent(
          eventType: ListEventType.set,
          fullListValueForSet: [
            new TestFixture('A', data: 0),
            new TestFixture('B', data: 1),
            new TestFixture('C', data: 2),
            new TestFixture('D', data: 3),
            new TestFixture('E', data: 4),
          ]);
      expect(
          list,
          equals([
            new TestFixture('A', data: 0),
            new TestFixture('B', data: 1),
            new TestFixture('C', data: 2),
            new TestFixture('D', data: 3),
            new TestFixture('E', data: 4),
          ]));

      list.filter = (f) => f.data > 0;
      expect(
          list,
          equals([
            new TestFixture('B', data: 1),
            new TestFixture('C', data: 2),
            new TestFixture('D', data: 3),
            new TestFixture('E', data: 4),
          ]));

      list.comparator = (a, b) => b.data.compareTo(a.data);
      expect(
          list,
          equals([
            new TestFixture('E', data: 4),
            new TestFixture('D', data: 3),
            new TestFixture('C', data: 2),
            new TestFixture('B', data: 1),
          ]));

      yield new KeyedListEvent(
          eventType: ListEventType.set,
          fullListValueForSet: [
            new TestFixture('A', data: 5),
            new TestFixture('B', data: 4),
            new TestFixture('C', data: 3),
            new TestFixture('D', data: 2),
            new TestFixture('E', data: 1),
            new TestFixture('F', data: 0),
          ]);
      expect(
          list,
          equals([
            new TestFixture('A', data: 5, updateCount: 1),
            new TestFixture('B', data: 4, updateCount: 1),
            new TestFixture('C', data: 3, updateCount: 1),
            new TestFixture('D', data: 2, updateCount: 1),
            new TestFixture('E', data: 1, updateCount: 1),
          ]));

      yield new KeyedListEvent(
          eventType: ListEventType.set,
          fullListValueForSet: [
            new TestFixture('F', data: 0),
            new TestFixture('E', data: 1),
            new TestFixture('A', data: 5),
            new TestFixture('C', data: 3),
          ]);
      expect(
          list,
          equals([
            new TestFixture('A', data: 5, updateCount: 2),
            new TestFixture('C', data: 3, updateCount: 2),
            new TestFixture('E', data: 1, updateCount: 2),
          ]));

      list.comparator = null;
      expect(
          list,
          equals([
            new TestFixture('E', data: 1, updateCount: 2),
            new TestFixture('A', data: 5, updateCount: 2),
            new TestFixture('C', data: 3, updateCount: 2),
          ]));

      list.filter = null;
      expect(
          list,
          equals([
            new TestFixture('F', data: 0, updateCount: 1),
            new TestFixture('E', data: 1, updateCount: 2),
            new TestFixture('A', data: 5, updateCount: 2),
            new TestFixture('C', data: 3, updateCount: 2),
          ]));
    })
          ..activate());

    // Wait for all microtasks (listen()) to complete.
    await new Future(() {});

    expect(
        list,
        equals([
          new TestFixture('F', data: 0, updateCount: 1),
          new TestFixture('E', data: 1, updateCount: 2),
          new TestFixture('A', data: 5, updateCount: 2),
          new TestFixture('C', data: 3, updateCount: 2),
        ]));

    list.dispose();
  });
}
