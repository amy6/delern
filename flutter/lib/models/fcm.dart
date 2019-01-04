import 'dart:core';

import 'package:delern_flutter/models/base/keyed_list_item.dart';
import 'package:delern_flutter/models/base/model.dart';
import 'package:meta/meta.dart';

class FCM implements KeyedListItem, Model {
  String key;
  String uid;
  String name;
  String language;

  FCM({@required this.uid}) : assert(uid != null);

  Map<String, dynamic> toMap(bool isNew) => {
        'fcm/$uid/$key': {
          'name': name,
          'language': language,
        },
      };

  @override
  String get rootPath => 'fcm/$uid';
}
