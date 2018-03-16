import 'dart:async';
import 'dart:isolate';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:sentry/sentry.dart';

// Error reporting inspired (or rather, copied 1:1)
// by https://github.com/yjbanov/crashy/
final SentryClient _sentry = new SentryClient(
    dsn: "https://36d72a65344d439d86ee65d623d050ce:" +
        "038b2b2aa94f474db45ce1c4676b845e@sentry.io/305345");

Future _reportError(dynamic error, dynamic stackTrace) async {
  print('Caught error: $error');
  print('Reporting to Sentry.io...');
  final SentryResponse response = await _sentry.captureException(
    exception: error,
    stackTrace: stackTrace,
  );
  if (response.isSuccessful) {
    print('Success! Event ID: ${response.eventId}');
  } else {
    print('Failed to report to Sentry.io: ${response.error}');
  }
}

void main() {
  FlutterError.onError = (FlutterErrorDetails details) async {
    print('FlutterError.onError caught an error');
    await _reportError(details.exception, details.stack);
  };
  Isolate.current.addErrorListener(new RawReceivePort((dynamic pair) async {
    print('Isolate.current.addErrorListener caught an error');
    await _reportError(
      (pair as List<String>).first,
      (pair as List<String>).last,
    );
  }).sendPort);

  runZoned<Future>(() async {
    runApp(new MyApp());
  }, onError: (error, stackTrace) async {
    print('Zone caught an error');
    await _reportError(error, stackTrace);
  });
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'Delern',
      theme: new ThemeData(
        primarySwatch: Colors.green,
      ),
      home: new MyHomePage(title: 'Delern'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  _MyHomePageState createState() => new _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const android = const MethodChannel('org.dasfoo.delern/android');

  bool launched = false;

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.

    if (!launched) {
      launched = true;
      android.invokeMethod('Main');
    }

    return new Scaffold(
      appBar: new AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: new Text(widget.title),
      ),
    );
  }
}
