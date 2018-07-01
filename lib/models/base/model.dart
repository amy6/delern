abstract class Model {
  String key;
  String get rootPath;
  Map<String, dynamic> toMap(bool isNew);
}
