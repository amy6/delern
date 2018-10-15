import 'package:intro_views_flutter/Models/page_view_model.dart';
import 'package:flutter/material.dart';

import '../../flutter/localization.dart';

class IntroViewBuilder {
  List<PageViewModel> introPages(BuildContext context) {
    var pages = [
      PageViewModel(
          pageColor: const Color(0xFFFFB74D),
          body: Text(
            AppLocalizations.of(context).decksIntroDescription,
          ),
          title: Text(
            AppLocalizations.of(context).decksIntroTitle,
          ),
          textStyle: const TextStyle(color: Colors.white),
          mainImage: Image.asset(
            'images/deck_creation.png',
            height: 285.0,
            width: 285.0,
            alignment: Alignment.center,
          )),
      PageViewModel(
        pageColor: const Color(0xFF8BC34A),
        body: Text(
          AppLocalizations.of(context).learnIntroDescription,
        ),
        title: Text(AppLocalizations.of(context).learnIntroTitle),
        mainImage: Image.asset(
          'images/child_learning.png',
          height: 285.0,
          width: 285.0,
          alignment: Alignment.center,
        ),
        textStyle: const TextStyle(color: Colors.white),
      ),
      PageViewModel(
        pageColor: const Color(0xFF607D8B),
        body: Text(
          AppLocalizations.of(context).shareIntroDescription,
        ),
        title: Text(AppLocalizations.of(context).shareIntroTitle),
        mainImage: Image.asset(
          'images/card_sharing.png',
          height: 285.0,
          width: 285.0,
          alignment: Alignment.center,
        ),
        textStyle: const TextStyle(color: Colors.white),
      ),
    ];
    return pages;
  }
}
