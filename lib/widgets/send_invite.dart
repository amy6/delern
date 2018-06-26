import 'package:flutter/material.dart';
import 'package:share/share.dart';

import '../flutter/localization.dart';

sendInvite(BuildContext context) {
  Share.share(AppLocalizations.of(context).inviteToAppMessage);
}
