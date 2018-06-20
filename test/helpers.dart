import 'package:test/test.dart';

import '../lib/models/base/observable_list.dart';
import '../lib/view_models/base/view_models_list.dart';

StreamMatcher eventMatcher(ListEventType eventType, int index,
    [previousValue]) {
  var expected = new ListEvent(
      eventType: eventType, index: index, previousValue: previousValue);
  return new StreamMatcher((q) async {
    if (!await q.hasNext) return '';

    ListEvent actual = await q.next;
    if (actual.eventType == expected.eventType &&
        actual.index == expected.index &&
        actual.previousValue == expected.previousValue) {
      return null;
    }

    return 'emitted $actual';
  }, 'match $expected');
}

class TestFixture extends ListItemViewModel {
  final String key;
  dynamic data;
  int updateCount;
  bool get active => _active;

  bool _active = false;

  TestFixture(this.key, {this.data, this.updateCount});

  @override
  void activate() => _active = true;

  @override
  void deactivate() => _active = false;

  @override
  ListItemViewModel updateWith(TestFixture value) {
    expect(value.key, key);
    data = value.data;
    updateCount = (updateCount ?? 0) + 1;
    return this;
  }

  @override
  bool operator ==(other) =>
      (other is TestFixture) &&
      key == other.key &&
      data == other.data &&
      updateCount == other.updateCount;

  @override
  int get hashCode => key.hashCode ^ data.hashCode ^ (updateCount ?? 0);

  @override
  String toString() =>
      '#$key [$data]${updateCount == null ? '' : ' gen $updateCount'}';
}
