import 'package:delern/widgets/logo.dart';
import 'package:flutter/material.dart';

import 'package:firebase_auth/firebase_auth.dart';

import '../remote/remote_config.dart';
import '../remote/sign_in.dart';
import '../widgets/sign_in.dart';
import '../widgets/decks.dart';

class HomePage extends StatefulWidget {
  final String title;

  HomePage(this.title, {Key key}) : super(key: key);

  @override
  _HomePageState createState() => new _HomePageState();
}

class _HomePageState extends State<HomePage> {
  FirebaseUser user;
  bool initialized;

  @override
  void initState() {
    super.initState();

    getRemoteConfig().then((nothing) {
      setState(() {
        initialized = true;
      });

      getCurrentUser()
          .then((currentUser) => setState(() {
                user = currentUser;
              }))
          .catchError((e) {/* TODO(dotdoom): not currently signed in */});
    });
  }

  @override
  Widget build(BuildContext context) {
    if (!initialized) {
      return new LogoWidget();
    }

    var appBar = new AppBar(title: new Text(widget.title));
    if (user == null) {
      return new Scaffold(
        appBar: appBar,
        body: new SignInWidget((u) => setState(() => user = u)),
      );
    }

    return new Scaffold(
      appBar: appBar,
      drawer: new Drawer(
          child: new Column(
        children: <Widget>[
          new UserAccountsDrawerHeader(
            accountName: new Text(user.displayName),
            accountEmail: new Text(user.email),
            currentAccountPicture: new CircleAvatar(
              backgroundImage: new NetworkImage(user.photoUrl),
            ),
          ),
          new MaterialButton(
            child: new Text('Sign Out'),
            onPressed: () {
              signOut().then((nothing) => setState(() => user = null));
            },
          ),
        ],
      )),
      body: new DecksWidget(user),
    );
  }
}
