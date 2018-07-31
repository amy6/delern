import 'dart:async';
import 'dart:math';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../remote/error_reporting.dart';

class UserMessages {
  // TODO(dotdoom): if Scaffold.of() fails, the error is not reported!
  static Future<Null> showError(
      ScaffoldState scaffoldState, dynamic e, StackTrace stackTrace) {
    var errorFuture = ErrorReporting.report('showError', e, stackTrace);

    String message =
        AppLocalizations.of(scaffoldState.context).errorUserMessage +
            e.toString().substring(0, min(e.toString().length, 50));
    showMessage(scaffoldState, message);

    return errorFuture;
  }

  static void showMessage(ScaffoldState scaffoldState, String message) {
    scaffoldState.showSnackBar(SnackBar(
      content: Text(message),
      duration: Duration(seconds: 3),
    ));
  }
}
