import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:package_info/package_info.dart';
import 'package:sentry/sentry.dart';

import '../flutter/device_info.dart';

// Error reporting inspired by https://github.com/yjbanov/crashy/.

class ErrorReporting {
  static SentryClient _sentry;
  static String uid;

  static Future<Null> report(
      String src, dynamic error, dynamic stackTrace) async {
    var message = error.toString();
    try {
      // For DatabaseError, toString() returns "Instance of 'DatabaseError'".
      message += ': ${error.message}';
    } catch (e) {
      // We tried.
    }

    debugPrint('/!\\ /!\\ /!\\ Caught error in $src: $message');

    if (stackTrace == null && error is Error) {
      stackTrace = error.stackTrace;
    }

    if (_sentry == null) {
      String environment = 'production';
      assert(() {
            environment = 'dev';
            return true;
          }() !=
          null);

      var packageInfo = await PackageInfo.fromPlatform();
      var deviceInfo = await DeviceInfo.getDeviceInfo();
      var environmentAttributes = Event(
        release: '${packageInfo.version} (${packageInfo.buildNumber})',
        environment: environment,
        extra: {
          'model': deviceInfo.userFriendlyName,
          'sdk': deviceInfo.sdk,
        },
      );
      _sentry = SentryClient(
          dsn: "https://36d72a65344d439d86ee65d623d050ce:" +
              "038b2b2aa94f474db45ce1c4676b845e@sentry.io/305345",
          environmentAttributes: environmentAttributes);
    }

    if (_sentry.environmentAttributes.environment == 'dev') {
      debugPrint(
          'Stack trace follows on the next line:\n$stackTrace\n${'-' * 80}');
    }

    print('Reporting to Sentry.io...');
    final SentryResponse response = await _sentry.capture(
        event: Event(
      message: message,
      stackTrace: stackTrace,
      loggerName: src,
      userContext: User(id: uid),
    ));
    if (response.isSuccessful) {
      print('Success! Event ID: ${response.eventId}');
    } else {
      print('Failed to report to Sentry.io: ${response.error}');
    }
  }
}
