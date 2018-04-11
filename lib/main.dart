import 'dart:async';
import 'dart:isolate';

import 'package:flutter/material.dart';

import 'remote/error_reporting.dart';
import 'pages/home.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'flutter/localization.dart';

class App extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    var title = 'Delern';
    assert((title = 'Delern DEBUG') != null);
    return new MaterialApp(
      // Produce collections of localized values
      localizationsDelegates: [
        const AppLocalizationsDelegate(),
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ],
      supportedLocales: [
        const Locale('en', 'US'),
        const Locale('ru', 'RU'),
      ],
      title: title,
      theme: new ThemeData(
          primarySwatch: Colors.green, accentColor: Colors.redAccent),
      home: new HomePage(title),
    );
  }
}

void main() {
  FlutterError.onError = (FlutterErrorDetails details) async {
    await reportError('FlutterError', details.exception, details.stack);
  };
  Isolate.current.addErrorListener(new RawReceivePort((dynamic pair) async {
    await reportError(
      'Isolate ErrorListener',
      (pair as List<String>).first,
      (pair as List<String>).last,
    );
  }).sendPort);
  runZoned<Future>(() async {
    runApp(new App());
  }, onError: (error, stackTrace) async {
    await reportError('Zone', error, stackTrace);
  });
}
