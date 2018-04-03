import 'package:test/test.dart';

import '../lib/models/observable_list.dart';

void main() {
  test('observable list', () {
    var list = new ObservableList(<int>[]);
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
  });
}
