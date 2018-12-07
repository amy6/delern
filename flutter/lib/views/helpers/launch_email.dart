import 'package:flutter/material.dart';
import 'package:package_info/package_info.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../flutter/localization.dart';
import '../../flutter/user_messages.dart';

const String _supportEmail = 'delern@dasfoo.org';

//https://www.w3schools.com/tags/ref_urlencode.asp
String _queryEncodingToPercent(String text) => text.replaceAll('+', '%20');

Future<void> launchEmail(BuildContext context) async {
  final platform = Theme.of(context).platform;
  final appInfo = await PackageInfo.fromPlatform();
  final appVersion = appInfo.version;
  final buildNumber = appInfo.buildNumber;
  final appName = appInfo.appName;
  final orientation = MediaQuery.of(context).orientation.toString();
  final screenSize = MediaQuery.of(context).size;

  // On iPhone Gmail app \n does not work.
  final appInfoOptions = {
    'subject': 'Delern Support',
    'body': '\n\n\nApp Name: $appName; \n'
        'App Version: $appVersion; \n'
        'Build Number: $buildNumber; \n'
        'Plaform: $platform; \n'
        'App Orientation: $orientation; \n'
        'Screensize: $screenSize; \n'
  };

  final mailUrl = _queryEncodingToPercent(Uri(
          scheme: 'mailto',
          path: _supportEmail,
          queryParameters: appInfoOptions)
      .toString());

  final googleGmailUrl = _queryEncodingToPercent(Uri(
          scheme: 'googlegmail',
          path: '/co',
          queryParameters: {'to': _supportEmail}..addAll(appInfoOptions))
      .toString());

  try {
    if (Theme.of(context).platform == TargetPlatform.iOS &&
        await canLaunch(googleGmailUrl)) {
      await launch(googleGmailUrl);
      return;
    }
    await launch(mailUrl, forceSafariVC: false);
  } catch (e, stackTrace) {
    UserMessages.showError(() => Scaffold.of(context),
        AppLocalizations.of(context).installEmailApp, stackTrace);
  }
}
