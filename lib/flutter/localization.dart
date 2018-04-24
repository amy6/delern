import 'dart:async';

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../l10n/messages_all.dart';

/// https://flutter.io/tutorials/internationalization/
/// 1. Generate intl_messages.arb that serves as template for other translations:
/// flutter pub pub run intl_translation:extract_to_arb --output-dir=lib/l10n lib/flutter/localization.dart
/// 2. Create other translations files naming them intl_*.arb
/// 3. Generate messages_*.dart files and messages_all.dart:
/// flutter pub pub run intl_translation:generate_from_arb --output-dir=lib/l10n   --no-use-deferred-loading lib/flutter/localization.dart lib/l10n/intl_*.arb
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

  String get signInWithGoogle {
    return Intl.message(
      'Sign In with Google',
      name: 'signInWithGoogle',
      desc: 'Sign In with Google Button',
    );
  }

  String get editCardsDeckMenu {
    return Intl.message(
      'Edit Cards',
      name: 'editCardsDeckMenu',
      desc: 'Edit Cards in Deck Menu',
    );
  }

  String get settingsDeckMenu {
    return Intl.message(
      'Settings',
      name: 'settingsDeckMenu',
      desc: 'Settings in Deck Menu',
    );
  }

  String get shareDeckMenu {
    return Intl.message(
      'Share',
      name: 'shareDeckMenu',
      desc: 'Share in Deck Menu',
    );
  }

  String get numberOfCards {
    return Intl.message(
      'Number of cards: ',
      name: 'numberOfCards',
      desc: 'Number of cards',
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
