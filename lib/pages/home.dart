import 'package:device_info/device_info.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';

import '../models/base/transaction.dart';
import '../models/fcm.dart';
import '../models/user.dart';
import '../remote/sign_in.dart';
import '../widgets/create_deck.dart';
import '../widgets/decks.dart';
import '../widgets/navigation_drawer.dart';
import '../widgets/sign_in.dart';

final FirebaseMessaging _firebaseMessaging = new FirebaseMessaging();

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

    signInSilently();

    FirebaseAuth.instance.onAuthStateChanged.listen((firebaseUser) async {
      setState(() => _user = firebaseUser);
      if (firebaseUser != null) {
        // TODO(dotdoom): move this elsewhere, and install keepSync etc.

        DeviceInfoPlugin deviceInfo = new DeviceInfoPlugin();

        String deviceName;
        try {
          var info = await deviceInfo.androidInfo;
          deviceName = '${info.manufacturer} ${info.model}';
        } catch (_) {
          var info = await deviceInfo.iosInfo;
          deviceName = info.model;
        }

        var locale = Localizations.localeOf(context);
        var fcm = FCM(firebaseUser.uid,
            language: '${locale.languageCode}_${locale.countryCode}',
            name: deviceName);

        print('Registering for FCM as ${fcm.name} in ${fcm.language}');
        (Transaction()
              ..save(User(firebaseUser.uid,
                  name: firebaseUser.displayName,
                  photoUrl: firebaseUser.photoUrl))
              ..save(fcm))
            .commit();
        _firebaseMessaging.getToken();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    var appBar = new AppBar(title: new Text(widget.title));
    if (_user == null) {
      return new Scaffold(
        appBar: appBar,
        body: new SignInWidget(),
      );
    }

    return new Scaffold(
      appBar: appBar,
      drawer: new NavigationDrawer(_user),
      body: new DecksWidget(_user.uid),
      floatingActionButton: new CreateDeck(_user),
    );
  }
}
