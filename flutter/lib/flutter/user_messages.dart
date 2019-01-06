import 'dart:async';
import 'dart:math';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/remote/error_reporting.dart';
import 'package:flutter/material.dart';

class UserMessages {
  // TODO(ksheremet): Get rid of it
  static Future<void> showError(ScaffoldState scaffoldFinder(), e,
      [StackTrace stackTrace]) {
    var errorFuture =
        ErrorReporting.report('showError', e, stackTrace ?? StackTrace.current);

    // Call a finder only *after* reporting the error, in case it crashes
    // (often because Scaffold.of cannot find Scaffold ancestor widget).
    var scaffoldState = scaffoldFinder();
    var message = formUserFriendlyErrorMessage(
        AppLocalizations.of(scaffoldState.context), e);
    showMessage(scaffoldState, message);

    return errorFuture;
  }

  // TODO(ksheremet): Get rid of it
  static void reportError(e, [StackTrace stackTrace]) {
    ErrorReporting.report('showError', e, stackTrace ?? StackTrace.current);
  }

  // TODO(ksheremet): Add user message for Snackbar and error message for
  // reporting.
  // In navigation drawer 'Contact us' show user message to user and send error
  // message to Sentry.
  static void showMessage(ScaffoldState scaffoldState, String message) =>
      scaffoldState.showSnackBar(SnackBar(
        content: Text(message),
        duration: Duration(seconds: 3),
      ));

  static String formUserFriendlyErrorMessage(AppLocalizations locale, e) =>
      locale.errorUserMessage +
      e.toString().substring(0, min(e.toString().length, 50));
}
