import 'package:test/test.dart';

import '../lib/models/keyed_list.dart';
import '../lib/models/observable_list.dart';
import '../lib/view_models/proxy_keyed_list.dart';

class TestFixture implements KeyedListItem {
  final String key;
  TestFixture(this.key);
}

void main() {
  test('dispose', () {
    var list = new ProxyKeyedList(new ObservableList<TestFixture>());
    list.dispose();
  });

  test('read only interface', () {
    var list = new ProxyKeyedList(
        new ObservableList<TestFixture>()..add(null)..add(null));

    expect(() => list.setAt(0, null),
        throwsA(const isInstanceOf<UnsupportedError>()));
    expect(
        () => list.move(0, 1), throwsA(const isInstanceOf<UnsupportedError>()));
    expect(() => list.removeAt(0),
        throwsA(const isInstanceOf<UnsupportedError>()));
    expect(() => list.insert(0, null),
        throwsA(const isInstanceOf<UnsupportedError>()));
    expect(
        () => list.shuffle(), throwsA(const isInstanceOf<UnsupportedError>()));
    expect(
        () => list[0] = null, throwsA(const isInstanceOf<UnsupportedError>()));
  });
}
