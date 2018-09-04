import 'package:flutter/material.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';

class SupportDevelopment extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
            title: Text(AppLocalizations
                .of(context)
                .navigationDrawerSupportDevelopment)),
        body: Builder(
          builder: (context) => Markdown(
              data: AppLocalizations.of(context).supportDevelopment,
              styleSheet: MarkdownStyleSheet
                  .fromTheme(Theme.of(context))
                  .copyWith(p: AppStyles.primaryText),
              onTapLink: (href) async {
                if (await canLaunch(href)) {
                  await launch(href, forceSafariVC: false);
                } else {
                  UserMessages.showError(
                      () => Scaffold.of(context),
                      'Could not launch url',
                      throw 'Could not launch url: $href');
                }
              }),
        ));
  }
}
