import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';

import '../models/base/transaction.dart';
import '../models/fcm.dart';
import '../models/user.dart';

class HomeViewModel {
  static Future<void> userSignedIn(FirebaseUser firebaseUser, FCM fcm) {
    // TODO(dotdoom): move keepSync into models, but need to:
    // use User instead of FirebaseUser, for that need to
    // move NavigationDrawer into Home, for that need to
    // move Scaffold into Home => need AppBar in Home, for that need
    // a generic SearchBar, or set Navigator in one place and AppBar in another.

    FirebaseDatabase.instance
        .reference()
        .child('decks')
        .child(firebaseUser.uid)
        .keepSynced(true);
    // TODO(dotdoom): learning/$uid; but that's too much!

    print('Registering for FCM as ${fcm.name} in ${fcm.language}');
    return (Transaction()
          ..save(User(
              key: firebaseUser.uid,
              name: firebaseUser.displayName,
              photoUrl: firebaseUser.photoUrl))
          ..save(fcm))
        .commit();
  }
}
