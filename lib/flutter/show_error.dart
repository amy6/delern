import 'dart:async';
import 'dart:math';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../remote/error_reporting.dart';

Future<Null> showError(
    ScaffoldState scaffoldState, dynamic e, StackTrace stackTrace) {
  String message = AppLocalizations.of(scaffoldState.context).errorUserMessage +
      e.toString().substring(0, min(e.toString().length, 50));

  scaffoldState.showSnackBar(SnackBar(
    content: Text(message),
    duration: Duration(seconds: 3),
  ));
  return reportError('showError', e, stackTrace);
}
