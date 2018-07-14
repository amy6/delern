import 'package:flutter/material.dart';

void showMessage(ScaffoldState scaffoldState, String message) {
  scaffoldState.showSnackBar(SnackBar(
    content: Text(message),
    duration: Duration(seconds: 3),
  ));
}
