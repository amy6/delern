import 'dart:async';
import 'dart:core';

import 'package:delern_flutter/models/base/database_list_event.dart';
import 'package:delern_flutter/models/base/enum.dart';
import 'package:delern_flutter/models/base/keyed_list_item.dart';
import 'package:delern_flutter/models/base/model.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:meta/meta.dart';

enum AccessType {
  read,

  /// "write" implies "read".
  write,
  owner,
}

class DeckAccessModel implements KeyedListItem, Model {
  /// DeckAccessModel key is uid of the user whose access it holds.
  String key;

  String deckKey;
  AccessType access;
  String email;

  /// Display Name is populated by database, can be null.
  String get displayName => _displayName;
  String _displayName;

  /// Photo URL is populated by database, can be null.
  String get photoUrl => _photoUrl;
  String _photoUrl;

  DeckAccessModel({@required this.deckKey}) : assert(deckKey != null);

  DeckAccessModel._fromSnapshot({
    @required this.key,
    @required this.deckKey,
    @required Map<String, dynamic> value,
  })  : assert(key != null),
        assert(deckKey != null) {
    if (value == null) {
      key = null;
      return;
    }
    _displayName = value['displayName'];
    _photoUrl = value['photoUrl'];
    email = value['email'];
    access = Enum.fromString(value['access'], AccessType.values);
  }

  static Stream<DatabaseListEvent<DeckAccessModel>> getList(
          {@required String deckKey}) =>
      fullThenChildEventsStream(
          FirebaseDatabase.instance
              .reference()
              .child('deck_access')
              .child(deckKey)
              .orderByKey(),
          (key, value) => DeckAccessModel._fromSnapshot(
              key: key, deckKey: deckKey, value: value));

  static Stream<DeckAccessModel> get(
          {@required String deckKey, @required String key}) =>
      FirebaseDatabase.instance
          .reference()
          .child('deck_access')
          .child(deckKey)
          .child(key)
          .onValue
          .map((evt) => DeckAccessModel._fromSnapshot(
              key: key, deckKey: deckKey, value: evt.snapshot.value));

  @override
  String get rootPath => 'deck_access/$deckKey';

  @override
  Map<String, dynamic> toMap(bool isNew) => {
        // Do not save displayName and photoUrl because these are populated by
        // Cloud functions.
        '$rootPath/$key/access': Enum.asString(access),
        '$rootPath/$key/email': email,
        // Update "access" field of the Deck, too.
        'decks/$key/$deckKey/access': Enum.asString(access),
      };
}
