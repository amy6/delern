import 'package:flutter/material.dart';
import 'package:intro_views_flutter/Models/page_view_model.dart';
import 'package:intro_views_flutter/intro_views_flutter.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../flutter/localization.dart';
import '../../views/helpers/helper_progress_indicator.dart';
import '../../views/helpers/sign_in_widget.dart';

class OnboardingViewWidget extends StatefulWidget {
  final Widget child;

  const OnboardingViewWidget({@required this.child}) : assert(child != null);

  @override
  State<StatefulWidget> createState() => _OnboardingViewWidgetState();
}

class _OnboardingViewWidgetState extends State<OnboardingViewWidget> {
  static const String _introPrefKey = 'is-intro-shown';
  final Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  bool _isIntroShown;

  @override
  Widget build(BuildContext context) => FutureBuilder(
      future: _prefs,
      builder: (context, pref) {
        if (pref.connectionState == ConnectionState.done) {
          _isIntroShown = pref.data.getBool(_introPrefKey);
          if (_isIntroShown == true) {
            return SignInWidget(child: widget.child);
          } else {
            return _IntroViewWidget(callback: () async {
              (await _prefs).setBool(_introPrefKey, true);
              setState(() {
                _isIntroShown = true;
              });
            });
          }
        }
        return HelperProgressIndicator();
      });
}

class _IntroViewWidget extends StatelessWidget {
  static const _textStyle = TextStyle(color: Colors.white);
  final Function callback;

  const _IntroViewWidget({@required this.callback}) : assert(callback != null);

  List<PageViewModel> _introPages(BuildContext context) {
    final imageWidth = MediaQuery.of(context).size.width * 0.85;
    // If device is small, set smaller size of text to prevent overlapping
    // TODO(ksheremet): In landscape mode on small device font size isn't set up
    // Check intro_views_flutter package to fix it
    final textStyle = (imageWidth > 280)
        ? _textStyle
        : _textStyle.merge(const TextStyle(fontSize: 25));
    var pages = [
      PageViewModel(
          pageColor: const Color(0xFF3F51A5),
          body: Text(
            AppLocalizations.of(context).decksIntroDescription,
          ),
          title: Text(
            AppLocalizations.of(context).decksIntroTitle,
          ),
          textStyle: textStyle,
          mainImage: Image.asset(
            'images/deck_creation.png',
            width: imageWidth,
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
          width: imageWidth,
          alignment: Alignment.center,
        ),
        textStyle: textStyle,
      ),
      PageViewModel(
        pageColor: const Color(0xFF607D8B),
        body: Text(
          AppLocalizations.of(context).shareIntroDescription,
        ),
        title: Text(AppLocalizations.of(context).shareIntroTitle),
        mainImage: Image.asset(
          'images/card_sharing.png',
          width: imageWidth,
          alignment: Alignment.center,
        ),
        textStyle: textStyle,
      ),
    ];
    return pages;
  }

  @override
  Widget build(BuildContext context) => IntroViewsFlutter(
        _introPages(context),
        doneText: Text(AppLocalizations.of(context).done.toUpperCase()),
        skipText: Text(AppLocalizations.of(context).skip.toUpperCase()),
        onTapDoneButton: callback,
        showSkipButton: true,
        pageButtonTextStyles: const TextStyle(
          color: Colors.white,
          fontSize: 18.0,
        ),
      );
}
