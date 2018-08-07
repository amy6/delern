import 'package:flutter/material.dart';

import '../views/helpers/card_background.dart';

class AppStyles {
  static final Map<Gender, Color> cardBackgroundColors = {
    Gender.noGender: Colors.greenAccent[100],
    Gender.masculine: Colors.lightBlue[200],
    Gender.feminine: Colors.pink[300],
    Gender.neuter: Colors.amberAccent[100],
  };
}
