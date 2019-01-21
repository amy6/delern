import 'package:delern_flutter/flutter/device_info.dart';
import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:flutter/material.dart';
import 'package:package_info/package_info.dart';
import 'package:url_launcher/url_launcher.dart';

const String _supportEmail = 'delern@dasfoo.org';

//https://www.w3schools.com/tags/ref_urlencode.asp
String _queryEncodingToPercent(String text) => text.replaceAll('+', '%20');

Future<void> launchEmail(BuildContext context) async {
  final appInfo = await PackageInfo.fromPlatform();
  final appVersion = appInfo.version;
  final buildNumber = appInfo.buildNumber;
  final appName = appInfo.appName;
  final orientation = MediaQuery.of(context).orientation.toString();
  //Count physical pixels of device
  final screenSize =
      MediaQuery.of(context).size * MediaQuery.of(context).devicePixelRatio;

  // On iPhone Gmail app \n does not work.
  final deviceInfo = (await DeviceInfo.getDeviceInfo())
      .info
      .entries
      .map((entry) => '${entry.key}: ${entry.value}')
      .join('; \n');
  final appInfoOptions = {
    'subject': '$appName Feedback',
    'body': '\n\n\nApp Version: $appVersion; \n'
        'Build Number: $buildNumber; \n'
        'App Orientation: $orientation; \n'
        'Screensize: $screenSize; \n'
        '$deviceInfo \n'
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
