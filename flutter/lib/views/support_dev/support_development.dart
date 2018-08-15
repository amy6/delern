import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';

class _LinkTextSpan extends TextSpan {
  _LinkTextSpan(
      {TextStyle style,
      String url,
      String text,
      @required BuildContext context})
      : super(
            style: style,
            text: text ?? url,
            recognizer: new TapGestureRecognizer()
              ..onTap = () async {
                if (await canLaunch(url)) {
                  await launch(url, forceSafariVC: false);
                } else {
                  UserMessages.showError(
                      () => Scaffold.of(context),
                      'Could not launch url',
                      throw 'Could not launch url: $url');
                }
              });
}

class SupportDevelopment extends StatelessWidget {
  final TextStyle linkStyle = AppStyles.linkText;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
            title: Text(AppLocalizations
                .of(context)
                .navigationDrawerSupportDevelopment)),
        body: Builder(
          builder: (context) => Padding(
                padding: const EdgeInsets.all(8.0),
                child: new RichText(
                    softWrap: true,
                    text: TextSpan(
                      children: <TextSpan>[
                        TextSpan(
                            text: AppLocalizations.of(context).supportDev,
                            style: AppStyles.primaryText),
                        _LinkTextSpan(
                            style: linkStyle,
                            url: 'mailto:delern@dasfoo.org',
                            text: 'delern@dasfoo.org',
                            context: context),
                        TextSpan(
                          text: '\n\n' +
                              AppLocalizations
                                  .of(context)
                                  .followSocialMediaLabel,
                          style: AppStyles.primaryText,
                        ),
                        _LinkTextSpan(
                            style: linkStyle,
                            url: 'https://fb.me/das.delern',
                            text: '\nFacebook',
                            context: context),
                        _LinkTextSpan(
                            style: linkStyle,
                            url: 'https://twitter.com/dasdelern',
                            text: '\n\nTwitter',
                            context: context),
                        _LinkTextSpan(
                            style: linkStyle,
                            url:
                                'https://plus.google.com/communities/104603840044649051798',
                            text: '\n\nGoogle+',
                            context: context),
                        _LinkTextSpan(
                            style: linkStyle,
                            url: 'https://vk.com/delern',
                            text: '\n\nVK',
                            context: context),
                        TextSpan(
                            style: AppStyles.primaryText,
                            text: '\n\n' +
                                AppLocalizations.of(context).sourceCodeLabel +
                                ' '),
                        _LinkTextSpan(
                            style: linkStyle,
                            url: 'https://github.com/dasfoo/delern',
                            text: 'Delern guthub repo.',
                            context: context)
                      ],
                    )),
              ),
        ));
  }
}
