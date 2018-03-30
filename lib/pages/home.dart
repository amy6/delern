import 'package:flutter/material.dart';

import 'package:firebase_auth/firebase_auth.dart';

import '../view_models/deck_view_model.dart';
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

  @override
  void initState() {
    super.initState();

    getCurrentUser()
        .then((currentUser) => setState(() {
              user = currentUser;
            }))
        .catchError((e) {/* TODO(dotdoom): not currently signed in */});
  }

  @override
  Widget build(BuildContext context) {
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
      body: new DecksWidget(DecksViewModel.getDecks(user.uid)),
      floatingActionButton:
          new FloatingActionButton(child: new Icon(Icons.add), onPressed: null),
    );
  }
}
