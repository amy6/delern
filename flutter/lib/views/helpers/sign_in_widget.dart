import 'package:firebase_analytics/firebase_analytics.dart';
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
  final _itemPadding =
      const Padding(padding: EdgeInsets.symmetric(vertical: 10.0));

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

        if (!_user.isAnonymous) {
          // Don't wait for FCM token to save User.
          Transaction()
            ..save(User.fromFirebase(firebaseUser))
            ..commit();
        }

        FirebaseAnalytics()
          ..setUserId(_user.uid)
          ..logLogin();

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

  Widget _buildFeatureText(String text) => ListTile(
      leading: const Icon(Icons.check_circle),
      title: Text(text, style: AppStyles.primaryText),
      contentPadding: const EdgeInsets.symmetric(horizontal: 8.0));

  @override
  Widget build(BuildContext context) {
    if (_user != null) {
      return CurrentUserWidget(user: _user, child: widget.child);
    }
    if (_isAuthStateKnown == false) {
      return HelperProgressIndicator();
    }
    final features = AppLocalizations.of(context)
        .splashScreenFeatures
        .split('\n')
        .map(_buildFeatureText)
        .toList();
    return Scaffold(
      backgroundColor: AppStyles.signInBackgroundColor,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.end,
        children: <Widget>[
              Expanded(
                  child: Center(
                child: Image.asset(
                  'images/delern.png',
                ),
              )),
            ] +
            features +
            [
              _itemPadding,
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 15.0),
                child: RaisedButton(
                    color: Colors.white,
                    onPressed: () => signIn(SignInProvider.google),
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
                              AppLocalizations.of(context).signInWithGoogle,
                              style: AppStyles.primaryText,
                            )),
                      ],
                    )),
              ),
              _itemPadding,
              Text(
                AppLocalizations.of(context).doNotNeedFeaturesText,
                style: AppStyles.primaryText,
              ),
              _itemPadding,
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 15.0),
                child: RaisedButton(
                    color: Colors.white,
                    onPressed: () => signIn(SignInProvider.anonymous),
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        Padding(
                          padding: const EdgeInsets.symmetric(vertical: 15.0),
                          child: Text(
                            AppLocalizations.of(context).continueAnonymously,
                            style: AppStyles.primaryText,
                          ),
                        ),
                      ],
                    )),
              ),
              _itemPadding
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
