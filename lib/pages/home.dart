import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../flutter/device_info.dart';
import '../models/fcm.dart';
import '../pages/decks_list.dart';
import '../remote/sign_in.dart';
import '../view_models/home_view_model.dart';
import '../widgets/sign_in.dart';

class HomePage extends StatefulWidget {
  final String title;

  HomePage(this.title, {Key key}) : super(key: key);

  @override
  _HomePageState createState() => new _HomePageState();
}

class _HomePageState extends State<HomePage> {
  FirebaseUser _user;

  @override
  void initState() {
    super.initState();

    FirebaseAuth.instance.onAuthStateChanged.listen((firebaseUser) async {
      setState(() => _user = firebaseUser);
      if (firebaseUser != null) {
        HomeViewModel.userSignedIn(
            firebaseUser,
            FCM(firebaseUser.uid,
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
      return new Scaffold(
        appBar: AppBar(title: Text(widget.title)),
        body: new SignInWidget(),
      );
    }

    return DecksListPage(user: _user, title: widget.title);
  }
}
