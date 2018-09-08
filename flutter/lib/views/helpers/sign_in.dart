import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../../flutter/device_info.dart';
import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../models/base/transaction.dart';
import '../../models/fcm.dart';
import '../../models/user.dart';
import '../../remote/error_reporting.dart';
import '../../remote/sign_in.dart';
import 'progress_indicator.dart';

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

      if (_user != null) {
        ErrorReporting.uid = _user.uid;
        var fcm = FCM(
            uid: firebaseUser.uid,
            language: Localizations.localeOf(context).toString(),
            name: (await DeviceInfo.getDeviceInfo()).userFriendlyName)
          // TODO(ksheremet): await _firebaseMessaging.getToken()
          ..key = 'fake';

        // TODO(dotdoom): move into models?
        print('Registering for FCM as ${fcm.name} in ${fcm.language}');
        (Transaction()..save(User.fromFirebase(firebaseUser))..save(fcm))
            .commit();
      }
    });

    signInSilently();
  }

  @override
  Widget build(BuildContext context) {
    if (_user != null) {
      return CurrentUserWidget(user: _user, child: widget.child);
    }
    if (_isAuthStateKnown == false) {
      return HelperProgressIndicator();
    }
    return Scaffold(
      backgroundColor: AppStyles.signInBackgroundColor,
      body: Stack(
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
            padding: EdgeInsets.only(bottom: 50.0, left: 15.0, right: 15.0),
            child: Row(
              children: <Widget>[
                Expanded(
                  child: Align(
                      alignment: Alignment.bottomCenter,
                      child: RaisedButton(
                          color: Colors.white,
                          onPressed: signInGoogleUser,
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: <Widget>[
                              Container(
                                padding: EdgeInsets.all(10.0),
                                child: Image.asset(
                                  'images/google_sign_in.png',
                                  height: 35.0,
                                  width: 35.0,
                                ),
                              ),
                              Container(
                                  padding: EdgeInsets.only(left: 10.0),
                                  child: Text(
                                    AppLocalizations.of(context)
                                        .signInWithGoogle,
                                    style: AppStyles.primaryText,
                                  )),
                            ],
                          ))),
                ),
              ],
            ),
          )
        ],
      ),
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
  bool updateShouldNotify(CurrentUserWidget oldWidget) =>
      user != oldWidget.user;
}
