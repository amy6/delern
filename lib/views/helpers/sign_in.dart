import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../remote/sign_in.dart';
import 'progress_indicator.dart' as progressIndicator;

class SignInWidget extends StatefulWidget {
  final Widget child;

  SignInWidget({this.child});

  @override
  State<StatefulWidget> createState() => _SignInWidgetState();
}

class _SignInWidgetState extends State<SignInWidget> {
  FirebaseUser _user;
  bool _isAuthStateKnown = false;

  @override
  void initState() {
    super.initState();

    FirebaseAuth.instance.onAuthStateChanged.listen((firebaseUser) async {
      setState(() {
        _user = firebaseUser;
        _isAuthStateKnown = true;
      });
    });

    signInSilently();
  }

  @override
  Widget build(BuildContext context) {
    if (_user != null) {
      return CurrentUserWidget(user: _user, child: widget.child);
    }
    if (_isAuthStateKnown == false) {
      return progressIndicator.ProgressIndicator();
    }
    return Stack(
      children: <Widget>[
        Column(
          children: <Widget>[
            Expanded(
              child: Container(
                child: Center(
                  child: Padding(
                    padding: EdgeInsets.all(50.0),
                    child: Image.asset(
                      'images/delern.png',
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
        Container(
          padding: const EdgeInsets.only(bottom: 50.0, left: 15.0, right: 15.0),
          child: new Row(
            children: <Widget>[
              new Expanded(
                child: new Align(
                    alignment: Alignment.bottomCenter,
                    child: new RaisedButton(
                        color: Colors.white,
                        onPressed: signInGoogleUser,
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
                                  AppLocalizations.of(context).signInWithGoogle,
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

class CurrentUserWidget extends InheritedWidget {
  final FirebaseUser user;

  static CurrentUserWidget of(BuildContext context) =>
      context.inheritFromWidgetOfExactType(CurrentUserWidget);

  CurrentUserWidget({Key key, Widget child, @required this.user})
      : assert(user != null),
        super(key: key, child: child);

  @override
  bool updateShouldNotify(InheritedWidget oldWidget) =>
      user != (oldWidget as CurrentUserWidget).user;
}
