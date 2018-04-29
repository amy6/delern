import 'dart:async';

import 'package:test/test.dart';

import '../lib/models/keyed_list.dart';
import '../lib/models/observable_list.dart';
import '../lib/view_models/proxy_keyed_list.dart';

class TestFixture implements KeyedListItem {
  final String key;
  TestFixture(this.key);

  @override
  bool operator ==(other) => (other is TestFixture) && key == other.key;

  @override
  int get hashCode => key.hashCode;

  @override
  String toString() => '#$key';
}

void main() {
  test('dispose', () {
    var list = new ProxyKeyedList(new ObservableList<TestFixture>());
    list.dispose();
  });

  test('filtered', () async {
    var baseList = new ObservableList<TestFixture>();
    var list = new ProxyKeyedList<TestFixture>(baseList);

    // Wait for all microtasks (listen()) to complete.
    await new Future(() {});

    baseList.setAll(0, [
      new TestFixture('1'),
      new TestFixture('2'),
      new TestFixture('3'),
    ]);
    expect(
        list,
        equals([
          new TestFixture('1'),
          new TestFixture('2'),
          new TestFixture('3'),
        ]));

    /*
    list.filter = ((f) => f.key != '1');
    expect(
        list,
        equals([
          new TestFixture('2'),
          new TestFixture('3'),
        ]));

    baseList.add(new TestFixture('4'));
    expect(
        list,
        equals([
          new TestFixture('2'),
          new TestFixture('3'),
          new TestFixture('4'),
        ]));

    baseList.removeAt(1);
    expect(
        list,
        equals([
          new TestFixture('3'),
          new TestFixture('4'),
        ]));

    list.filter = null;
    expect(
        list,
        equals([
          new TestFixture('1'),
          new TestFixture('3'),
          new TestFixture('4'),
        ]));
        */

    list.dispose();
    baseList.dispose();
  });
}
