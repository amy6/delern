abstract class KeyedListItem {
  String get key;
}

/// Adds an [indexOfKey] method to a list implementing [indexWhere].
abstract class KeyedListMixin<E extends KeyedListItem> {
  /// Returns an index of the first element where key equals to supplied [key].
  /// If such element is not found, returns -1.
  int indexOfKey(String key) => indexWhere((item) => item.key == key);

  int indexWhere(bool test(E element), [int start = 0]);
}
