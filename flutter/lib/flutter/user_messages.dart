import 'dart:async';
import 'dart:math';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../remote/error_reporting.dart';

class UserMessages {
  static Future<Null> showError(ScaffoldState scaffoldFinder(), dynamic e,
      [StackTrace stackTrace]) {
    var errorFuture =
        ErrorReporting.report('showError', e, stackTrace ?? StackTrace.current);

    // Call a finder only *after* reporting the error, in case it crashes
    // (often because Scaffold.of cannot find Scaffol ancestor widget).
    var scaffoldState = scaffoldFinder();
    var message = AppLocalizations.of(scaffoldState.context).errorUserMessage +
        e.toString().substring(0, min(e.toString().length, 50));
    showMessage(scaffoldState, message);

    return errorFuture;
  }

  // TODO(ksheremet): Add user message for Snackbar and error message for reporting
  // In navigation drawer 'Contact us' show user message to user and send error message
  // to Sentry
  static void showMessage(ScaffoldState scaffoldState, String message) =>
      scaffoldState.showSnackBar(SnackBar(
        content: Text(message),
        duration: Duration(seconds: 3),
      ));
}
