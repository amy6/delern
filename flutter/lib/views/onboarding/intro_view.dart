import 'package:intro_views_flutter/Models/page_view_model.dart';
import 'package:intro_views_flutter/intro_views_flutter.dart';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';

class IntroViewWidget extends StatelessWidget {
  static const double _imageHeight = 285.0;
  static const double _imageWidth = 285.0;
  static const _textStyle = TextStyle(color: Colors.white);
  final Function callback;

  const IntroViewWidget({@required this.callback}) : assert(callback != null);

  List<PageViewModel> _introPages(BuildContext context) {
    var pages = [
      PageViewModel(
          pageColor: const Color(0xFF3F51A5),
          body: Text(
            AppLocalizations.of(context).decksIntroDescription,
          ),
          title: Text(
            AppLocalizations.of(context).decksIntroTitle,
          ),
          textStyle: _textStyle,
          mainImage: Image.asset(
            'images/deck_creation.png',
            height: _imageHeight,
            width: _imageWidth,
            alignment: Alignment.center,
          )),
      PageViewModel(
        pageColor: const Color(0xFFFFB74D),
        body: Text(
          AppLocalizations.of(context).learnIntroDescription,
        ),
        title: Text(AppLocalizations.of(context).learnIntroTitle),
        mainImage: Image.asset(
          'images/child_learning.png',
          height: _imageHeight,
          width: _imageWidth,
          alignment: Alignment.center,
        ),
        textStyle: _textStyle,
      ),
      PageViewModel(
        pageColor: const Color(0xFF607D8B),
        body: Text(
          AppLocalizations.of(context).shareIntroDescription,
        ),
        title: Text(AppLocalizations.of(context).shareIntroTitle),
        mainImage: Image.asset(
          'images/card_sharing.png',
          height: _imageHeight,
          width: _imageWidth,
          alignment: Alignment.center,
        ),
        textStyle: _textStyle,
      ),
    ];
    return pages;
  }

  @override
  Widget build(BuildContext context) => IntroViewsFlutter(
        _introPages(context),
        onTapDoneButton: callback,
        showSkipButton: true,
        pageButtonTextStyles: const TextStyle(
          color: Colors.white,
          fontSize: 18.0,
        ),
      );
}
