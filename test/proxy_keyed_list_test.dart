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
}
