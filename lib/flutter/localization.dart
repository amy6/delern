import 'dart:async';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../l10n/messages_all.dart';

class AppLocalizations {
  static Future<AppLocalizations> load(Locale locale) {
    final String name =
        locale.countryCode.isEmpty ? locale.languageCode : locale.toString();
    final String localeName = Intl.canonicalizedLocale(name);

    return initializeMessages(localeName).then((bool _) {
      Intl.defaultLocale = localeName;
      return new AppLocalizations();
    });
  }

  static AppLocalizations of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations);
  }

  String get navigationDrawerSignOut {
    return Intl.message(
      'Sign Out',
      name: 'navigationDrawerSignOut',
      desc: 'Sign Out in Navigation Drawer',
    );
  }

  String get navigationDrawerCommunicateGroup {
    return Intl.message(
      'Communicate',
      name: 'navigationDrawerCommunicateGroup',
      desc: 'Communicate Group Name in Navigation Drawer',
    );
  }

  String get navigationDrawerInviteFriends {
    return Intl.message(
      'Invite Friends',
      name: 'navigationDrawerInviteFriends',
      desc: 'Invite Friends in Navigation Drawer',
    );
  }

  String get navigationDrawerContactUs {
    return Intl.message(
      'Contact Us',
      name: 'navigationDrawerContactUs',
      desc: 'Contact Us in Navigation Drawer',
    );
  }

  String get navigationDrawerSupportDevelopment {
    return Intl.message(
      'Support Development',
      name: 'navigationDrawerSupportDevelopment',
      desc: 'Support Development in Navigation Drawer',
    );
  }

  String get navigationDrawerAbout {
    return Intl.message(
      'About',
      name: 'navigationDrawerAbout',
      desc: 'About in Navigation Drawer',
    );
  }
}

class AppLocalizationsDelegate extends LocalizationsDelegate<AppLocalizations> {
  const AppLocalizationsDelegate();

  @override
  bool isSupported(Locale locale) => ['en', 'ru'].contains(locale.languageCode);

  @override
  Future<AppLocalizations> load(Locale locale) => AppLocalizations.load(locale);

  @override
  bool shouldReload(AppLocalizationsDelegate old) => false;
}
