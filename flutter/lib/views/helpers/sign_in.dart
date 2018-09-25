import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';

import '../../flutter/device_info.dart';
import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../models/base/transaction.dart';
import '../../models/fcm.dart';
import '../../models/user.dart';
import '../../remote/error_reporting.dart';
import '../../remote/sign_in.dart';
import 'helper_progress_indicator.dart';

final _firebaseMessaging = FirebaseMessaging();

class SignInWidget extends StatefulWidget {
  final Widget child;

  const SignInWidget({this.child});

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

        // Don't wait for FCM token to save User.
        Transaction()
          ..save(User.fromFirebase(firebaseUser))
          ..commit();

        _firebaseMessaging.onTokenRefresh.listen((token) async {
          var fcm = FCM(
              uid: firebaseUser.uid,
              language: Localizations.localeOf(context).toString(),
              name: (await DeviceInfo.getDeviceInfo()).userFriendlyName)
            ..key = token;

          print('Registering for FCM as ${fcm.name} in ${fcm.language}');
          (Transaction()..save(fcm)).commit();
        });

        _firebaseMessaging
          ..requestNotificationPermissions()
          // TODO(dotdoom): register onMessage to show a snack bar with
          //                notification when the app is in foreground.
          ..configure();
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
                      padding: const EdgeInsets.all(50.0),
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
            padding:
                const EdgeInsets.only(bottom: 50.0, left: 15.0, right: 15.0),
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
                                padding: const EdgeInsets.all(10.0),
                                child: Image.asset(
                                  'images/google_sign_in.png',
                                  height: 35.0,
                                  width: 35.0,
                                ),
                              ),
                              Container(
                                  padding: const EdgeInsets.only(left: 10.0),
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

  const CurrentUserWidget({@required this.user, Key key, Widget child})
      : assert(user != null),
        super(key: key, child: child);

  @override
  bool updateShouldNotify(CurrentUserWidget oldWidget) =>
      user != oldWidget.user;
}
