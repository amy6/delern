import 'package:flutter/material.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';

Widget buildNonScrollingMarkdown(String text, BuildContext context) {
  return MarkdownBody(
      data: text,
      styleSheet: MarkdownStyleSheet.fromTheme(Theme.of(context))
          .copyWith(p: AppStyles.primaryText),
      onTapLink: (href) async {
        if (await canLaunch(href)) {
          await launch(href, forceSafariVC: false);
        } else {
          UserMessages.showError(() => Scaffold.of(context),
              AppLocalizations.of(context).couldNotLaunchUrl + href);
        }
      });
}
