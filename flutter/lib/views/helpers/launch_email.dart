import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../flutter/localization.dart';
import '../../flutter/user_messages.dart';

const String _googleGmaillUrl =
    'googlegmail:///co?to=delern@dasfoo.org&subject=Delern%20Support';
const String _mailUrl = 'mailto:delern@dasfoo.org?subject=Delern%20Support';

Future<void> launchEmail(BuildContext context) async {
  try {
    if (Theme.of(context).platform == TargetPlatform.iOS &&
        await canLaunch(_googleGmaillUrl)) {
      await launch(_googleGmaillUrl);
      return;
    }
    await launch(_mailUrl, forceSafariVC: false);
  } catch (e, stackTrace) {
    UserMessages.showError(() => Scaffold.of(context),
        AppLocalizations.of(context).installEmailApp, stackTrace);
  }
}
