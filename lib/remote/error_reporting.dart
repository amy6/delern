import 'dart:async';

import 'package:sentry/sentry.dart';

// Error reporting inspired (or rather, copied 1:1)
// by https://github.com/yjbanov/crashy/
final SentryClient _sentry = new SentryClient(
    dsn: "https://36d72a65344d439d86ee65d623d050ce:" +
        "038b2b2aa94f474db45ce1c4676b845e@sentry.io/305345");

Future<Null> reportError(String src, dynamic error, dynamic stackTrace) async {
  print('/!\\ /!\\ /!\\ Caught error in $src: $error /!\\ /!\\ /!\\ ');

  bool sendToServer = true;
  assert(() {
        sendToServer = false;
        return true;
      }() !=
      null);
  if (!sendToServer) {
    print(stackTrace);
    print('-' * 80);
    return;
  }

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
