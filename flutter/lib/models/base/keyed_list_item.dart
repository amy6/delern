import 'dart:collection';

abstract class KeyedListItem {
  String get key;
}

/// Adds an [indexOfKey] method to a list implementing [indexWhere].
mixin KeyedListMixin<E extends KeyedListItem> on ListBase<E> {
  /// Returns an index of the first element where key equals to supplied [key].
  /// If such element is not found, returns -1.
  int indexOfKey(String key) => indexWhere((item) => item.key == key);
}
