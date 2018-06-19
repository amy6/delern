import 'package:flutter/material.dart';

simpleTextSnackBar(_text) => SnackBar(
      content: Text(_text),
      duration: Duration(seconds: 3),
    );
