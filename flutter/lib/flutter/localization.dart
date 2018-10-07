import 'dart:async';

import 'package:flutter/material.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'package:intl/intl.dart';

import '../l10n/messages_all.dart';

/// https://flutter.io/tutorials/internationalization/
class AppLocalizations {
  static Future<AppLocalizations> load(Locale locale) async {
    final name =
        locale.countryCode.isEmpty ? locale.languageCode : locale.toString();
    final localeName = Intl.canonicalizedLocale(name);

    await initializeMessages(localeName);
    await initializeDateFormatting(localeName);
    Intl.defaultLocale = localeName;

    return AppLocalizations();
  }

  static AppLocalizations of(BuildContext context) =>
      Localizations.of<AppLocalizations>(context, AppLocalizations);

  String get navigationDrawerSignOut => Intl.message(
        'Sign Out',
        name: 'navigationDrawerSignOut',
        desc: 'Sign Out in Navigation Drawer',
      );

  String get navigationDrawerCommunicateGroup => Intl.message(
        'Communicate',
        name: 'navigationDrawerCommunicateGroup',
        desc: 'Communicate Group Name in Navigation Drawer',
      );

  String get navigationDrawerInviteFriends => Intl.message(
        'Invite Friends',
        name: 'navigationDrawerInviteFriends',
        desc: 'Invite Friends in Navigation Drawer',
      );

  String get navigationDrawerContactUs => Intl.message(
        'Contact Us',
        name: 'navigationDrawerContactUs',
        desc: 'Contact Us in Navigation Drawer',
      );

  String get navigationDrawerSupportDevelopment => Intl.message(
        'Support Development',
        name: 'navigationDrawerSupportDevelopment',
        desc: 'Support Development in Navigation Drawer',
      );

  String get navigationDrawerAbout => Intl.message(
        'About',
        name: 'navigationDrawerAbout',
        desc: 'About in Navigation Drawer',
      );

  String get signInWithGoogle => Intl.message(
        'Sign In with Google',
        name: 'signInWithGoogle',
        desc: 'Sign In with Google Button',
      );

  String get editCardsDeckMenu => Intl.message(
        'Edit Cards',
        name: 'editCardsDeckMenu',
        desc: 'Edit Cards in Deck Menu',
      );

  String get settingsDeckMenu => Intl.message(
        'Settings',
        name: 'settingsDeckMenu',
        desc: 'Settings in Deck Menu',
      );

  String get shareDeckMenu => Intl.message(
        'Share',
        name: 'shareDeckMenu',
        desc: 'Share in Deck Menu',
      );

  String numberOfCards(int number) => Intl.message(
        'Number of cards: $number',
        name: 'numberOfCards',
        args: [number],
        desc: 'Number of cards',
      );

  String get canEdit => Intl.message(
        'Can Edit',
        name: 'canEdit',
        desc: 'User can edit a deck',
      );

  String get canView => Intl.message(
        'Can View',
        name: 'canView',
        desc: 'User can view a deck',
      );

  String get owner => Intl.message(
        'Owner',
        name: 'owner',
        desc: 'User has owner access',
      );

  String get noAccess => Intl.message(
        'No access',
        name: 'noAccess',
        desc: 'User has no access',
      );

  String get emailAddressHint => Intl.message(
        'Email address',
        name: 'emailAddressHint',
        desc: 'Email address hint',
      );

  String get peopleLabel => Intl.message(
        'People',
        name: 'peopleLabel',
        desc: 'People label',
      );

  String get whoHasAccessLabel => Intl.message(
        'Who has access',
        name: 'whoHasAccessLabel',
        desc: 'Who has access label',
      );

  String get frontSideHint => Intl.message(
        'Front side',
        name: 'frontSideHint',
        desc: 'front side',
      );

  String get backSideHint => Intl.message(
        'Back side',
        name: 'backSideHint',
        desc: 'back side',
      );

  String get reversedCardLabel => Intl.message(
        'Add reversed card',
        name: 'reversedCardLabel',
        desc: 'Add reversed card',
      );

  String get deck => Intl.message(
        'Deck',
        name: 'deck',
        desc: 'Deck label',
      );

  String get add => Intl.message(
        'Add',
        name: 'add',
        desc: 'Add',
      );

  String get save => Intl.message(
        'Save',
        name: 'save',
        desc: 'Save',
      );

  String get delete => Intl.message(
        'Delete',
        name: 'delete',
        desc: 'Delete',
      );

  String get saveChangesQuestion => Intl.message(
        'Do you want to save changes?',
        name: 'saveChangesQuestion',
        desc: 'Do you want to save changes?',
      );

  String get deleteCardQuestion => Intl.message(
        'Do you want to delete card?',
        name: 'deleteCardQuestion',
        desc: 'Do you want to delete card?',
      );

  String get errorUserMessage => Intl.message(
        'Error: ',
        name: 'errorUserMessage',
        desc: 'Error occurred.',
      );

  String get send => Intl.message(
        'Send',
        name: 'send',
        desc: 'Send',
      );

  String get appNotInstalledSharingDeck => Intl.message(
        'This user hasn\'t installed Delern yet. Do you want to sent an '
            'invite?',
        name: 'appNotInstalledSharingDeck',
        desc: 'The app hasn\'t installed by user with who deck was shared',
      );

  String get inviteToAppMessage => Intl.message(
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

  String get edit => Intl.message(
        'Edit',
        name: 'edit',
        desc: 'Edit',
      );

  String watchedCards(int number) => Intl.message(
        'Watched: $number',
        args: [number],
        name: 'watchedCards',
        desc: 'watchedCards',
      );

  String get deleteDeckQuestion => Intl.message(
        'Do you want to delete deck?',
        name: 'deleteDeckQuestion',
        desc: 'Do you want to delete deck?',
      );

  String get basicDeckType => Intl.message(
        'Basic',
        name: 'basicDeckType',
        desc: 'basic decktype name',
      );

  String get germanDeckType => Intl.message(
        'German',
        name: 'germanDeckType',
        desc: 'german decktype name',
      );

  String get swissDeckType => Intl.message(
        'Swiss',
        name: 'swissDeckType',
        desc: 'swiss decktype name',
      );

  String get deckType => Intl.message(
        'Deck Type',
        name: 'deckType',
        desc: 'Deck Type',
      );

  String get markdown => Intl.message(
        'Markdown',
        name: 'markdown',
        desc: 'Markdown',
      );

  String get cardDeletedUserMessage => Intl.message(
        'Card was deleted',
        name: 'cardDeletedUserMessage',
        desc: 'Card was deleted',
      );

  String get cardAddedUserMessage => Intl.message(
        'Card was added',
        name: 'cardAddedUserMessage',
        desc: 'Card was added',
      );

  String get searchHint => Intl.message(
        'Search...',
        name: 'searchHint',
        desc: 'Search...',
      );

  String get emptyUserSharingList => Intl.message(
        'Share your deck',
        name: 'emptyUserSharingList',
        desc: 'Share your deck',
      );

  String get emptyDecksList => Intl.message(
        'Add your decks',
        name: 'emptyDecksList',
        desc: 'Add your decks',
      );

  String get emptyCardsList => Intl.message(
        'Add your cards',
        name: 'emptyCardsList',
        desc: 'Add your cards',
      );

  String get noSharingAccessUserMessage => Intl.message(
        'Only owner of deck can share it.',
        name: 'noSharingAccessUserMessage',
        desc: 'Only owner of deck can share it.',
      );

  String get noEditingWithReadAccessUserMessage => Intl.message(
        'You cannot edit card with a read access.',
        name: 'noEditingWithReadAccessUserMessage',
        desc: 'You cannot edit card with a read access.',
      );

  String get noDeletingWithReadAccessUserMessage => Intl.message(
        'You cannot delete card with a read access.',
        name: 'noDeletingWithReadAccessUserMessage',
        desc: 'You cannot delete card with a read access.',
      );

  String get noAddingWithReadAccessUserMessage => Intl.message(
        'You cannot add cards with a read access.',
        name: 'noAddingWithReadAccessUserMessage',
        desc: 'You cannot add cards with a read access..',
      );

  String get continueEditingQuestion => Intl.message(
        'You have unsaved changes. Would you like to continue editing?',
        name: 'continueEditingQuestion',
        desc: 'You have unsaved changes. Would you like to continue editing?',
      );

  String get yes => Intl.message(
        'Yes',
        name: 'yes',
        desc: 'Yes',
      );

  String get discard => Intl.message(
        'Discard',
        name: 'discard',
        desc: 'Discard',
      );

  String get supportDevelopment => Intl.message(
        '''
Please tell us what we can do to make your experience with Delern better!
       
If you have any questions or suggestions please contact us:
[delern@dasfoo.org](mailto:delern@dasfoo.org) 
       
Follow latest news on:
       
- [Facebook](https://fb.me/das.delern) 
- [Twitter](https://twitter.com/dasdelern)
- [Google+](https://plus.google.com/communities/104603840044649051798)
- [VK](https://vk.com/delern)
       
To see the source code for this app, please visit the [Delern GitHub repo](https://github.com/dasfoo/delern).
      ''',
        name: 'supportDevelopment',
        desc: 'Support Development',
      );

  String get installEmailApp => Intl.message(
        'Please install Email App',
        name: 'installEmailApp',
        desc: 'Please install Email App',
      );

  String couldNotLaunchUrl(String url) => Intl.message(
        'Could not launch url $url',
        args: [url],
        name: 'couldNotLaunchUrl',
        desc: 'Could not launch url',
      );

  String get no => Intl.message(
        'no',
        name: 'no',
        desc: 'no',
      );

  String emptyDeckUserMessage(String menu) => Intl.message(
        'This deck is currently empty. Please add cards in $menu deck menu.',
        args: [menu],
        name: 'emptyDeckUserMessage',
        desc: 'Empty deck user message',
      );

  String continueLearningQuestion(String date) => Intl.message(
        'Next card to learn is suggested at $date. Would you like to continue '
            'learning anyway?',
        args: [date],
        name: 'continueLearningQuestion',
        desc: 'Question for the user to continue learning',
      );

  String get offlineUserMessage => Intl.message(
        'You are offline, please try it later',
        name: 'offlineUserMessage',
        desc: 'Offline user message',
      );

  String get serverUnavailableUserMessage => Intl.message(
        'Server temporarily unavailable, please try again later',
        name: 'serverUnavailableUserMessage',
        desc: 'Server temporarily unavailable',
      );

  String get doNotNeedFeaturesText => Intl.message(
        'I do not want any of these features',
        name: 'doNotNeedFeaturesText',
        desc: 'Do not need features text',
      );

  String get continueAnonymously => Intl.message(
        'Continue Anonymously',
        name: 'continueAnonymously',
        desc: 'Continue Anonymously',
      );

  String get splashScreenFeatures => Intl.message(
        'Data and progress are saved in the Cloud\n'
            'Data and progress are synchronized across all your devices\n'
            'Share cards with your friends and colleagues',
        name: 'splashScreenFeatures',
        desc: 'Data and progress are saved',
      );

  String get anonymous => Intl.message(
        'Anonymous',
        name: 'anonymous',
        desc: 'Anonymous',
      );

  String get navigationDrawerSignIn => Intl.message(
        'Sign In',
        name: 'navigationDrawerSignIn',
        desc: 'Sign In',
      );

  String get accountExistUserWarning => Intl.message(
        'You have already an account, please sign in with your credentials. '
            'All your data, that was created anonymously, will be lost. '
            'Would you like to continue?',
        name: 'accountExistUserWarning',
        desc: 'User warning, account already exists',
      );
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
