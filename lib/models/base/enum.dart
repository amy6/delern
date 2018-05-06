class Enum {
  static String asString(value) => value?.toString()?.split('.')?.last;

  static T fromString<T>(String value, List<T> enumValues) =>
      enumValues.firstWhere((enumValue) => asString(enumValue) == value,
          orElse: () => null);
}
