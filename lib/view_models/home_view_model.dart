import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';

import '../models/base/transaction.dart';
import '../models/fcm.dart';
import '../models/user.dart';

class HomeViewModel {
  static Future<void> userSignedIn(FirebaseUser firebaseUser, FCM fcm) {
    // TODO(dotdoom): install keepSync etc.
    print('Registering for FCM as ${fcm.name} in ${fcm.language}');
    return (Transaction()
          ..save(User(firebaseUser.uid,
              name: firebaseUser.displayName, photoUrl: firebaseUser.photoUrl))
          ..save(fcm))
        .commit();
  }
}
