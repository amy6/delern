import 'dart:async';
import 'dart:math';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../remote/error_reporting.dart';

class UserMessages {
  static Future<Null> showError(
      ScaffoldState scaffoldState, dynamic e, StackTrace stackTrace) {
    String message =
        AppLocalizations.of(scaffoldState.context).errorUserMessage +
            e.toString().substring(0, min(e.toString().length, 50));

    showMessage(scaffoldState, message);
    return reportError('showError', e, stackTrace);
  }

  static void showMessage(ScaffoldState scaffoldState, String message) {
    scaffoldState.showSnackBar(SnackBar(
      content: Text(message),
      duration: Duration(seconds: 3),
    ));
  }
}
