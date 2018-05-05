import 'dart:core';

import 'keyed_list.dart';

class User implements KeyedListItem {
  final String key;
  String name;
  String photoUrl;

  User.fromSnapshot(this.key, dynamic snapshotValue)
      : name = snapshotValue['name'],
        photoUrl = snapshotValue['photoUrl'];
}
