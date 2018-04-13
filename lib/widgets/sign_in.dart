import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../remote/sign_in.dart';

typedef void OnSignedIn(FirebaseUser user);

class SignInWidget extends StatelessWidget {
  final OnSignedIn onSignedIn;

  SignInWidget(this.onSignedIn, {Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return new Stack(
      children: <Widget>[
        new Column(
          children: <Widget>[
            new Expanded(
              child: new Container(
                child: new Center(
                  child: new Image.asset(
                    'images/delern.png',
                  ),
                ),
              ),
            ),
          ],
        ),
        new Container(
          padding: const EdgeInsets.only(bottom: 50.0, left: 15.0, right: 15.0),
          child: new Row(
            children: <Widget>[
              new Expanded(
                child: new Align(
                    alignment: Alignment.bottomCenter,
                    child: new RaisedButton(
                        color: Colors.white,
                        onPressed: () async =>
                            onSignedIn(await signInGoogleUser()),
                        child: new Row(
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: <Widget>[
                            new Container(
                              padding: const EdgeInsets.all(10.0),
                              child: new Image.asset(
                                'images/google_sign_in.png',
                                height: 35.0,
                                width: 35.0,
                              ),
                            ),
                            new Container(
                                padding: const EdgeInsets.only(left: 10.0),
                                child: new Text(
                                  'Sign In with Google',
                                  style: new TextStyle(
                                    fontSize: 18.0,
                                  ),
                                )),
                          ],
                        ))),
              ),
            ],
          ),
        )
      ],
    );
  }
}
