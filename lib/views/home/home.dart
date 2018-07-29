import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../../flutter/device_info.dart';
import '../../models/fcm.dart';
import '../../remote/sign_in.dart';
import '../../view_models/home_view_model.dart';
import '../decks_list/decks_list.dart';
import '../helpers/progress_indicator.dart' as progressIndicator;
import 'sign_in.dart';

class HomePage extends StatefulWidget {
  final String title;

  HomePage(this.title, {Key key}) : super(key: key);

  @override
  _HomePageState createState() => new _HomePageState();
}

class _HomePageState extends State<HomePage> {
  FirebaseUser _user;
  bool _authStateKnown = false;

  @override
  void initState() {
    super.initState();

    FirebaseAuth.instance.onAuthStateChanged.listen((firebaseUser) async {
      setState(() {
        _user = firebaseUser;
        _authStateKnown = true;
      });
      if (firebaseUser != null) {
        HomeViewModel.userSignedIn(
            firebaseUser,
            FCM(
                uid: firebaseUser.uid,
                language: Localizations.localeOf(context).toString(),
                name: await DeviceInfo.getDeviceManufactureName())
              // TODO(ksheremet): await _firebaseMessaging.getToken()
              ..key = 'fake');
      }
    });

    signInSilently();
  }

  @override
  Widget build(BuildContext context) {
    if (_user == null) {
      return Scaffold(
        appBar: AppBar(title: Text(widget.title)),
        body: _authStateKnown
            ? SignInWidget()
            : progressIndicator.ProgressIndicator(),
      );
    }

    return DecksListPage(user: _user, title: widget.title);
  }
}
