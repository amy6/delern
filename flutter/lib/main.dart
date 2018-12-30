import 'dart:async';
import 'dart:isolate';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/models/base/transaction.dart';
import 'package:delern_flutter/remote/error_reporting.dart';
import 'package:delern_flutter/views/decks_list/decks_list.dart';
import 'package:delern_flutter/views/helpers/scheduled_cards_bloc_widget.dart';
import 'package:delern_flutter/views/helpers/sign_in_widget.dart';
import 'package:delern_flutter/views/onboarding/intro_view.dart';
import 'package:firebase_analytics/firebase_analytics.dart';
import 'package:firebase_analytics/observer.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

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
        // This list limits what locales Global Localizations delegates above
        // will support. The first element of this list is a fallback locale.
        Locale('en', 'US'),
        Locale('ru', 'RU'),
      ],
      navigatorObservers: [_analyticsNavigatorObserver],
      title: title,
      // SignInWidget must be above Navigator to provide CurrentUserWidget.of().
      builder: (context, child) => OnboardingViewWidget(
          afterOnboardingBuilder: () => SignInWidget(
              afterSignInBuilder: () =>
                  ScheduledCardsBlocHolderWidget(child: child))),
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
