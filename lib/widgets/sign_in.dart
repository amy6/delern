import 'package:flutter/material.dart';

import 'package:firebase_auth/firebase_auth.dart';

import '../remote/sign_in.dart';

typedef void OnSignedIn(FirebaseUser user);

class SignInWidget extends StatelessWidget {
  final OnSignedIn onSignedIn;

  SignInWidget(this.onSignedIn, {Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return new Column(
      children: <Widget>[
        new MaterialButton(
          child: new Row(
            children: <Widget>[
              new Icon(Icons.supervisor_account),
              new Text('Sign in with Google'),
            ],
          ),
          onPressed: () async => onSignedIn(await signInGoogleUser()),
        )
      ],
    );
  }
}
