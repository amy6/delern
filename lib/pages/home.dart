import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../flutter/device_info.dart';
import '../flutter/localization.dart';
import '../models/fcm.dart';
import '../remote/sign_in.dart';
import '../view_models/home_view_model.dart';
import '../widgets/create_deck.dart';
import '../widgets/decks.dart';
import '../widgets/navigation_drawer.dart';
import '../widgets/sign_in.dart';

class HomePage extends StatefulWidget {
  final String title;

  HomePage(this.title, {Key key}) : super(key: key);

  @override
  _HomePageState createState() => new _HomePageState();
}

class _HomePageState extends State<HomePage> {
  FirebaseUser _user;
  Widget appBarTitle;
  Icon actionIcon;

  @override
  void initState() {
    super.initState();

    appBarTitle = Text(widget.title);
    actionIcon = Icon(Icons.search);

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

    return Scaffold(
      appBar: _buildAppBarWithSearch(),
      drawer: NavigationDrawer(_user),
      body: DecksWidget(_user.uid),
      floatingActionButton: CreateDeck(_user),
    );
  }

  Widget _buildAppBarWithSearch() {
    return AppBar(
      title: appBarTitle,
      actions: <Widget>[
        IconButton(
          icon: actionIcon,
          onPressed: () {
            setState(() {
              if (actionIcon.icon == Icons.search) {
                actionIcon = Icon(Icons.close);
                appBarTitle = TextField(
                  style: TextStyle(color: Colors.white, fontSize: 16.0),
                  decoration: InputDecoration(
                      prefixIcon: Icon(Icons.search, color: Colors.white),
                      hintText: AppLocalizations.of(context).searchHint,
                      hintStyle: TextStyle(color: Colors.white)),
                );
              } else {
                actionIcon = Icon(Icons.search);
                appBarTitle = Text(widget.title);
              }
            });
          },
        )
      ],
    );
  }
}
