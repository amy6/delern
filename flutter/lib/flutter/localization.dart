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
      return AppLocalizations();
    });
  }

  static AppLocalizations of(BuildContext context) =>
      Localizations.of<AppLocalizations>(context, AppLocalizations);

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
      'Number of cards:',
      name: 'numberOfCards',
      desc: 'Number of cards',
    );
  }

  String get canEdit {
    return Intl.message(
      'Can Edit',
      name: 'canEdit',
      desc: 'User can edit a deck',
    );
  }

  String get canView {
    return Intl.message(
      'Can View',
      name: 'canView',
      desc: 'User can view a deck',
    );
  }

  String get owner {
    return Intl.message(
      'Owner',
      name: 'owner',
      desc: 'User has owner access',
    );
  }

  String get noAccess {
    return Intl.message(
      'No access',
      name: 'noAccess',
      desc: 'User has no access',
    );
  }

  String get emailAddressHint {
    return Intl.message(
      'Email address',
      name: 'emailAddressHint',
      desc: 'Email address hint',
    );
  }

  String get peopleLabel {
    return Intl.message(
      'People',
      name: 'peopleLabel',
      desc: 'People label',
    );
  }

  String get whoHasAccessLabel {
    return Intl.message(
      'Who has access',
      name: 'whoHasAccessLabel',
      desc: 'Who has access label',
    );
  }

  String get frontSideHint {
    return Intl.message(
      'front side',
      name: 'frontSideHint',
      desc: 'front side',
    );
  }

  String get backSideHint {
    return Intl.message(
      'back side',
      name: 'backSideHint',
      desc: 'back side',
    );
  }

  String get reversedCardLabel {
    return Intl.message(
      'Add reversed card',
      name: 'reversedCardLabel',
      desc: 'Add reversed card',
    );
  }

  String get deck {
    return Intl.message(
      'Deck',
      name: 'deck',
      desc: 'Deck label',
    );
  }

  String get add {
    return Intl.message(
      'Add',
      name: 'add',
      desc: 'Add',
    );
  }

  String get cancel {
    return Intl.message(
      'Cancel',
      name: 'cancel',
      desc: 'Cancel',
    );
  }

  String get save {
    return Intl.message(
      'Save',
      name: 'save',
      desc: 'Save',
    );
  }

  String get delete {
    return Intl.message(
      'Delete',
      name: 'delete',
      desc: 'Delete',
    );
  }

  String get saveChangesQuestion {
    return Intl.message(
      'Do you want to save changes?',
      name: 'saveChangesQuestion',
      desc: 'Do you want to save changes?',
    );
  }

  String get deleteCardQuestion {
    return Intl.message(
      'Do you want to delete card?',
      name: 'deleteCardQuestion',
      desc: 'Do you want to delete card?',
    );
  }

  String get errorUserMessage {
    return Intl.message(
      'Error occurred: ',
      name: 'errorUserMessage',
      desc: 'Error occurred.',
    );
  }

  String get send {
    return Intl.message(
      'Send',
      name: 'send',
      desc: 'Send',
    );
  }

  String get appNotInstalledSharingDeck {
    return Intl.message(
      'This user hasn\'t installed Delern yet. Do you want to sent an invite?',
      name: 'appNotInstalledSharingDeck',
      desc: 'The app hasn\'t installed by user with who deck was shared',
    );
  }

  String get inviteToAppMessage {
    return Intl.message(
      '''I invite you to install Delern, a spaced repetition learning app, which will allow you to learn quickly and easily!
     
Proceed to https://play.google.com/store/apps/details?id=org.dasfoo.delern to install it from Google Play!
  
After install, follow Delern latest news on:
Facebook: https://fb.me/das.delern
VK: https://vk.com/delern
Google+: https://plus.google.com/communities/104603840044649051798
Twitter: https://twitter.com/dasdelern''',
      name: 'inviteToAppMessage',
      desc: 'Invite to the App message',
    );
  }

  String get edit {
    return Intl.message(
      'Edit',
      name: 'edit',
      desc: 'Edit',
    );
  }

  String get watchedCards {
    return Intl.message(
      'Watched: ',
      name: 'watchedCards',
      desc: 'watchedCards',
    );
  }

  String get deleteDeckQuestion {
    return Intl.message(
      'Do you want to delete deck?',
      name: 'deleteDeckQuestion',
      desc: 'Do you want to delete deck?',
    );
  }

  String get basicDeckType {
    return Intl.message(
      'Basic',
      name: 'basicDeckType',
      desc: 'basic decktype name',
    );
  }

  String get germanDeckType {
    return Intl.message(
      'German',
      name: 'germanDeckType',
      desc: 'german decktype name',
    );
  }

  String get swissDeckType {
    return Intl.message(
      'Swiss',
      name: 'swissDeckType',
      desc: 'swiss decktype name',
    );
  }

  String get deckType {
    return Intl.message(
      'Deck Type',
      name: 'deckType',
      desc: 'Deck Type',
    );
  }

  String get markdown {
    return Intl.message(
      'Markdown',
      name: 'markdown',
      desc: 'Markdown',
    );
  }

  String get cardDeletedUserMessage {
    return Intl.message(
      'Card was deleted',
      name: 'cardDeletedUserMessage',
      desc: 'Card was deleted',
    );
  }

  String get cardAddedUserMessage {
    return Intl.message(
      'Card was added',
      name: 'cardAddedUserMessage',
      desc: 'Card was added',
    );
  }

  String get searchHint {
    return Intl.message(
      'Search...',
      name: 'searchHint',
      desc: 'Search...',
    );
  }

  String get emptyUserSharingList {
    return Intl.message(
      'Share your deck',
      name: 'emptyUserSharingList',
      desc: 'Share your deck',
    );
  }

  String get emptyDecksList {
    return Intl.message(
      'Add your decks',
      name: 'emptyDecksList',
      desc: 'Add your decks',
    );
  }

  String get emptyCardsList {
    return Intl.message(
      'Add your cards',
      name: 'emptyCardsList',
      desc: 'Add your cards',
    );
  }

  String get noSharingAccessUserMessage {
    return Intl.message(
      'Only owner of deck can share it.',
      name: 'noSharingAccessUserMessage',
      desc: 'Only owner of deck can share it.',
    );
  }

  String get noEditingWithReadAccessUserMessage {
    return Intl.message(
      'You cannot edit card with a read access.',
      name: 'noEditingWithReadAccessUserMessage',
      desc: 'You cannot edit card with a read access.',
    );
  }

  String get noDeletingWithReadAccessUserMessage {
    return Intl.message(
      'You cannot delete card with a read access.',
      name: 'noDeletingWithReadAccessUserMessage',
      desc: 'You cannot delete card with a read access.',
    );
  }

  String get noAddingWithReadAccessUserMessage {
    return Intl.message(
      'You cannot add cards with a read access.',
      name: 'noAddingWithReadAccessUserMessage',
      desc: 'You cannot add cards with a read access..',
    );
  }

  String get continueEditingQuestion {
    return Intl.message(
      'You have unsaved changes. Would you like to continue editing?',
      name: 'continueEditingQuestion',
      desc: 'You have unsaved changes. Would you like to continue editing?',
    );
  }

  String get yes {
    return Intl.message(
      'Yes',
      name: 'yes',
      desc: 'Yes',
    );
  }

  String get discard {
    return Intl.message(
      'Discard',
      name: 'discard',
      desc: 'Discard',
    );
  }
}

class AppLocalizationsDelegate extends LocalizationsDelegate<AppLocalizations> {
  AppLocalizationsDelegate();

  @override
  bool isSupported(Locale locale) => ['en', 'ru'].contains(locale.languageCode);

  @override
  Future<AppLocalizations> load(Locale locale) => AppLocalizations.load(locale);

  @override
  bool shouldReload(AppLocalizationsDelegate old) => false;
}
