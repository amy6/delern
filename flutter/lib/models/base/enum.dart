/// Helper generic methods for enums.
class Enum {
  /// Returns the name of the enum [value], non-qualified (i.e. the last part).
  static String asString(value) => value?.toString()?.split('.')?.last;

  /// Finds non-qualified enum value that matches the [value] string, or returns
  /// null. Comparison is case sensitive. [enumValues] has to list all possible
  /// enum values, which is usually available as <EnumType>.values.
  static T fromString<T>(String value, List<T> enumValues) =>
      enumValues.firstWhere((enumValue) => asString(enumValue) == value,
          orElse: () => null);
}
