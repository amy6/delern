import 'dart:async';

import 'package:delern_flutter/flutter/device_info.dart';
import 'package:flutter/foundation.dart';
import 'package:package_info/package_info.dart';
import 'package:sentry/sentry.dart';

// Error reporting inspired by https://github.com/yjbanov/crashy/.

class ErrorReporting {
  static SentryClient _sentry;
  static String uid;

  static Future<void> report(String src, error, stackTrace,
      {Map<String, dynamic> extra, bool printErrorInfo = true}) async {
    var message = error.toString();
    try {
      // For DatabaseError, toString() returns "Instance of 'DatabaseError'":
      // https://github.com/flutter/plugins/pull/1096.
      message += ': ${error.message}';
    } catch (e) {
      // We tried.
    }

    if (printErrorInfo) {
      debugPrint('/!\\ /!\\ /!\\ Caught error in $src: $message');
    }

    if (stackTrace == null && error is Error) {
      stackTrace = error.stackTrace;
    }

    if (_sentry == null) {
      var environment = 'production';
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
          'device': deviceInfo.info,
        },
      );
      _sentry = SentryClient(
          dsn: 'https://36d72a65344d439d86ee65d623d050ce:'
              '038b2b2aa94f474db45ce1c4676b845e@sentry.io/305345',
          environmentAttributes: environmentAttributes);
    }

    if (printErrorInfo && _sentry.environmentAttributes.environment == 'dev') {
      debugPrint(
          'Stack trace follows on the next line:\n$stackTrace\n${'-' * 80}');
    }

    print('Reporting to Sentry.io...');
    final response = await _sentry.capture(
        event: Event(
      message: message,
      stackTrace: stackTrace,
      loggerName: src,
      userContext: User(id: uid),
      extra: extra,
    ));
    if (response.isSuccessful) {
      print('Success! Event ID: ${response.eventId}');
    } else {
      print('Failed to report to Sentry.io: ${response.error}');
    }
  }
}
