import 'package:flutter/material.dart';

import '../../flutter/styles.dart';
import '../../models/deck.dart';

enum Gender {
  masculine,
  feminine,
  neuter,
  no_gender,
}

Color specifyCardBackground(DeckType deckType, String text) {
  if (deckType == DeckType.basic) {
    return AppStyles.cardBackgroundColors[Gender.no_gender];
  }
  Gender textGender = Gender.no_gender;
  text = text.toLowerCase();
  if (deckType == DeckType.swiss) {
    textGender = _swissCardGender(text);
  } else {
    textGender = _germanCardGender(text);
  }
  return AppStyles.cardBackgroundColors[textGender];
}

Gender _swissCardGender(String text) {
  if (text.startsWith("de ") || text.startsWith("en ")) {
    return Gender.masculine;
  }
  if (text.startsWith("d ") || text.startsWith("e ")) {
    return Gender.feminine;
  }
  if (text.startsWith("s ") || text.startsWith("es ")) {
    return Gender.neuter;
  }
  return Gender.no_gender;
}

Gender _germanCardGender(String text) {
  if (text.startsWith("der ")) {
    return Gender.masculine;
  }
  if (text.startsWith("die ") || text.startsWith("eine ")) {
    return Gender.feminine;
  }
  if (text.startsWith("das ")) {
    return Gender.neuter;
  }
  return Gender.no_gender;
}
