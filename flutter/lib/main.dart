import 'dart:async';
import 'dart:isolate';

import 'package:firebase_analytics/firebase_analytics.dart';
import 'package:firebase_analytics/observer.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'flutter/localization.dart';
import 'models/base/transaction.dart';
import 'remote/error_reporting.dart';
import 'views/decks_list/decks_list.dart';
import 'views/helpers/sign_in.dart';

class App extends StatelessWidget {
  static final _analyticsNavigatorObserver =
      FirebaseAnalyticsObserver(analytics: FirebaseAnalytics());

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
        Locale('en', 'US'),
        Locale('ru', 'RU'),
      ],
      navigatorObservers: [_analyticsNavigatorObserver],
      title: title,
      // SignInWidget must be above Navigator to provide CurrentUserWidget.of().
      builder: (context, child) => SignInWidget(child: child),
      theme:
          ThemeData(primarySwatch: Colors.green, accentColor: Colors.redAccent),
      home: DecksListPage(title: title),
    );
  }
}

void main() {
  FlutterError.onError = (details) async {
    await ErrorReporting.report(
        'FlutterError', details.exception, details.stack);
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
