import 'dart:async';
import 'dart:isolate';

import 'package:firebase_analytics/firebase_analytics.dart';
import 'package:firebase_analytics/observer.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'flutter/localization.dart';
import 'models/base/transaction.dart';
import 'remote/error_reporting.dart';
import 'views/decks_list/decks_list.dart';
import 'views/helpers/sign_in_widget.dart';
import 'views/onboarding/intro_view.dart';

class App extends StatefulWidget {
  static final _analyticsNavigatorObserver =
      FirebaseAnalyticsObserver(analytics: FirebaseAnalytics());

  @override
  AppState createState() => AppState();
}

class AppState extends State<App> {
  static const String _introPrefKey = 'is-intro-shown';
  final Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  bool _isIntroShown;

  @override
  void initState() {
    super.initState();
    _prefs.then((pref) {
      _isIntroShown = pref.getBool(_introPrefKey);
    });
  }

  @override
  Widget build(BuildContext context) {
    var title = 'Delern';
    assert((title = 'Delern DEBUG') != null);
    return MaterialApp(
      // Produce collections of localized values
      localizationsDelegates: [
        AppLocalizationsDelegate(),
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ],
      supportedLocales: const [
        // This list limits what locales Global Localizations delegates above
        // will support. The first element of this list is a fallback locale.
        Locale('en', 'US'),
        Locale('ru', 'RU'),
      ],
      navigatorObservers: [App._analyticsNavigatorObserver],
      title: title,
      // SignInWidget must be above Navigator to provide CurrentUserWidget.of().
      builder: (context, child) {
        if (_isIntroShown != null && _isIntroShown != false) {
          return SignInWidget(child: child);
        } else {
          return IntroViewWidget(callback: () {
            setState(() {
              _prefs.then((pref) {
                pref.setBool(_introPrefKey, true);
                _isIntroShown = true;
              });
            });
          });
        }
      },
      theme:
          ThemeData(primarySwatch: Colors.green, accentColor: Colors.redAccent),
      home: DecksListPage(title: title),
    );
  }
}

void main() {
  FlutterError.onError = (details) async {
    FlutterError.dumpErrorToConsole(details);
    await ErrorReporting.report(
        'FlutterError', details.exception, details.stack,
        extra: {
          'FlutterErrorDetails': {
            'library': details.library,
            'context': details.context,
            'silent': details.silent,
          },
        },
        printErrorInfo: false);
  };
  Isolate.current.addErrorListener(RawReceivePort((pair) async {
    List<dynamic> errorAndStacktrace = pair;
    await ErrorReporting.report(
      'Isolate ErrorListener',
      errorAndStacktrace.first,
      errorAndStacktrace.last,
    );
  }).sendPort);
  runZoned<Future>(() async {
    FirebaseDatabase.instance.setPersistenceEnabled(true);
    Transaction.subscribeToOnlineStatus();
    FirebaseAnalytics().logAppOpen();
    runApp(App());
  }, onError: (error, stackTrace) async {
    await ErrorReporting.report('Zone', error, stackTrace);
  });
}
