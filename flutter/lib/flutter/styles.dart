import 'package:delern_flutter/views/helpers/card_background_specifier.dart';
import 'package:flutter/material.dart';

class AppStyles {
  static final Map<Gender, Color> cardBackgroundColors = {
    Gender.noGender: Colors.greenAccent[100],
    Gender.masculine: Colors.lightBlue[200],
    Gender.feminine: Colors.pink[300],
    Gender.neuter: Colors.amberAccent[100],
  };

  static final Color signInBackgroundColor = Colors.greenAccent[100];

  static const TextStyle primaryText = TextStyle(
    fontSize: 19.0,
    fontWeight: FontWeight.w400,
    color: Colors.black,
  );

  static const TextStyle secondaryText = TextStyle(
    fontSize: 16.0,
    fontWeight: FontWeight.w400,
    color: Colors.black87,
  );

  static final TextStyle navigationDrawerGroupText = TextStyle(
    fontWeight: FontWeight.w600,
    color: Colors.grey[600],
  );

  static const TextStyle searchBarText = TextStyle(
    color: Colors.white,
    fontSize: 19.0,
  );
}
