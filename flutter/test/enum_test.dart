import 'package:test/test.dart';

import '../lib/models/base/enum.dart';

enum _Test {
  first,
  second,
}

void main() {
  test('asString', () {
    var test = _Test.second;
    expect(Enum.asString(test), 'second');

    test = _Test.first;
    expect(Enum.asString(test), 'first');

    expect(Enum.asString(null), null);
  });

  test('fromString', () {
    expect(Enum.fromString('first', _Test.values), _Test.first);
    expect(Enum.fromString('second', _Test.values), _Test.second);
    expect(Enum.fromString(null, _Test.values), null);
  });
}
