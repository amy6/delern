import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:flutter/material.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:url_launcher/url_launcher.dart';

Widget buildNonScrollingMarkdown(String text, BuildContext context) =>
    MarkdownBody(
        data: text,
        styleSheet: MarkdownStyleSheet.fromTheme(Theme.of(context))
            .copyWith(p: AppStyles.primaryText),
        onTapLink: (href) async {
          if (await canLaunch(href)) {
            await launch(href, forceSafariVC: false);
          } else {
            UserMessages.showError(() => Scaffold.of(context),
                AppLocalizations.of(context).couldNotLaunchUrl(href));
          }
        });
