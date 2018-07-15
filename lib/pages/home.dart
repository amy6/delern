import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../remote/sign_in.dart';
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

  @override
  void initState() {
    super.initState();

    signInSilently();

    FirebaseAuth.instance.onAuthStateChanged.listen((firebaseUser) {
      // TODO(dotdoom): this is the place where we should update User model in
      //                DB, upload FCM tokens, install keepSync etc.
      setState(() => _user = firebaseUser);
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
