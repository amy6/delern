import 'package:delern_flutter/models/base/keyed_list_item.dart';

abstract class Model implements KeyedListItem {
  String key;
  String get rootPath;
  Map<String, dynamic> toMap(bool isNew);
}
