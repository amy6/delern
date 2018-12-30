import 'dart:async';
import 'dart:core';

import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

import '../remote/auth.dart' as auth;
import 'base/keyed_list_item.dart';
import 'base/model.dart';

class UserModel implements KeyedListItem, Model {
  String key;
  String name;
  String photoUrl;

  UserModel({@required this.key, this.name, this.photoUrl})
      : assert(key != null);

  UserModel.fromSnapshot(this.key, snapshotValue) {
    _parseSnapshot(snapshotValue);
  }

  UserModel.fromAuth(auth.User user)
      : key = user.uid,
        name = user.displayName,
        photoUrl = user.photoUrl;

  void _parseSnapshot(snapshotValue) {
    if (snapshotValue == null) {
      // Assume the user doesn't exist anymore.
      key = null;
      return;
    }
    name = snapshotValue['name'];
    photoUrl = snapshotValue['photoUrl'];
  }

  Map<String, dynamic> toMap(bool isNew) => {
        'users/$key': {
          'name': name,
          'photoUrl': photoUrl,
        },
      };

  Stream<void> get updates => FirebaseDatabase.instance
      .reference()
      .child('users')
      .child(key)
      .onValue
      .map((event) => _parseSnapshot(event.snapshot.value));

  @override
  String get rootPath => 'users';
}
