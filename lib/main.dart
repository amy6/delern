import 'dart:async';
import 'dart:isolate';

import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'flutter/localization.dart';
import 'remote/error_reporting.dart';
import 'views/decks_list/decks_list.dart';
import 'views/helpers/sign_in.dart';

class App extends StatelessWidget {
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
      supportedLocales: [
        Locale('en', 'US'),
        Locale('ru', 'RU'),
      ],
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
  FlutterError.onError = (FlutterErrorDetails details) async {
    await reportError('FlutterError', details.exception, details.stack);
  };
  Isolate.current.addErrorListener(RawReceivePort((dynamic pair) async {
    await reportError(
      'Isolate ErrorListener',
      (pair as List<String>).first,
      (pair as List<String>).last,
    );
  }).sendPort);
  runZoned<Future>(() async {
    FirebaseDatabase.instance.setPersistenceEnabled(true);
    runApp(App());
  }, onError: (error, stackTrace) async {
    await reportError('Zone', error, stackTrace);
  });
}
