import 'package:delern_flutter/flutter/device_info.dart';
import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/views/helpers/progress_indicator_widget.dart';
import 'package:flutter/material.dart';
import 'package:intro_views_flutter/Models/page_view_model.dart';
import 'package:intro_views_flutter/intro_views_flutter.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Onboarding extends StatefulWidget {
  final Widget Function() afterOnboardingBuilder;

  const Onboarding({@required this.afterOnboardingBuilder})
      : assert(afterOnboardingBuilder != null);

  @override
  State<StatefulWidget> createState() => _OnboardingState();
}

class _OnboardingState extends State<Onboarding> {
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
            return widget.afterOnboardingBuilder();
          } else {
            return _OnboardingWidget(callback: () async {
              (await _prefs).setBool(_introPrefKey, true);
              setState(() {
                _isIntroShown = true;
              });
            });
          }
        }
        return ProgressIndicatorWidget();
      });
}

class _OnboardingWidget extends StatelessWidget {
  static const _textStyle = TextStyle(color: Colors.white);
  final Function callback;

  const _OnboardingWidget({@required this.callback}) : assert(callback != null);

  List<PageViewModel> _introPages(BuildContext context) {
    final imageWidth = MediaQuery.of(context).size.width * 0.85;
    // If device is small, set smaller size of text to prevent overlapping
    final textStyle = DeviceInfo.isDeviceSmall(context)
        ? _textStyle.merge(const TextStyle(fontSize: 25))
        : _textStyle;
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
