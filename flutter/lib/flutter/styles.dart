import 'package:flutter/material.dart';

import '../views/helpers/card_background.dart';

class AppStyles {
  static Map<Gender, Color> cardBackgroundColors = {
    Gender.no_gender: Colors.greenAccent[100],
    Gender.masculine: Colors.lightBlue[200],
    Gender.feminine: Colors.pink[300],
    Gender.neuter: Colors.amberAccent[100],
  };
}
