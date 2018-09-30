// DO NOT EDIT. This is code generated via package:intl/generate_localized.dart
// This is a library that provides messages for a ru locale. All the
// messages from the main program should be duplicated here with the same
// function name.

import 'package:intl/intl.dart';
import 'package:intl/message_lookup_by_library.dart';

// ignore: unnecessary_new
final messages = new MessageLookup();

// ignore: unused_element
final _keepAnalysisHappy = Intl.defaultLocale;

// ignore: non_constant_identifier_names
typedef MessageIfAbsent(String message_str, List args);

class MessageLookup extends MessageLookupByLibrary {
  get localeName => 'ru';

  static m0(date) =>
      "Следующая карточка рекомендуется к повторению ${date}. Вы хотите продолжить изучение?";

  static m1(url) => "Не удалось запустить ссылку ${url}";

  static m2(menu) =>
      "Этот список пуст. Пожалуйста добавьте карточки в ${menu} меню списка.";

  static m3(number) => "Количество карточек: ${number}";

  static m4(number) => "Просмотрено: ${number}";

  final messages = _notInlinedMessages(_notInlinedMessages);
  static _notInlinedMessages(_) => <String, Function>{
        "add": MessageLookupByLibrary.simpleMessage("Добавить"),
        "appNotInstalledSharingDeck": MessageLookupByLibrary.simpleMessage(
            "Данный пользователь еще не установил Delern. Отправить приглашение?"),
        "backSideHint":
            MessageLookupByLibrary.simpleMessage("Обратная сторона"),
        "basicDeckType": MessageLookupByLibrary.simpleMessage("Базовый"),
        "canEdit": MessageLookupByLibrary.simpleMessage("Может редактировать"),
        "canView": MessageLookupByLibrary.simpleMessage("Может просматривать"),
        "cardAddedUserMessage":
            MessageLookupByLibrary.simpleMessage("Карточка добавлена"),
        "cardDeletedUserMessage":
            MessageLookupByLibrary.simpleMessage("Карточка удалена"),
        "continueEditingQuestion": MessageLookupByLibrary.simpleMessage(
            "У Вас есть несохраненные изменения. Хотите продолжить редактирование?"),
        "continueLearningQuestion": m0,
        "couldNotLaunchUrl": m1,
        "deck": MessageLookupByLibrary.simpleMessage("Список"),
        "deckType": MessageLookupByLibrary.simpleMessage("Тип списка"),
        "delete": MessageLookupByLibrary.simpleMessage("Удалить"),
        "deleteCardQuestion":
            MessageLookupByLibrary.simpleMessage("Вы хотите удалить карточку?"),
        "deleteDeckQuestion":
            MessageLookupByLibrary.simpleMessage("Вы хотите удалить список?"),
        "discard": MessageLookupByLibrary.simpleMessage("Отменить изменения"),
        "edit": MessageLookupByLibrary.simpleMessage("Редактировать"),
        "editCardsDeckMenu":
            MessageLookupByLibrary.simpleMessage("Редактировать карточки"),
        "emailAddressHint":
            MessageLookupByLibrary.simpleMessage("Адрес электронной почты"),
        "emptyCardsList":
            MessageLookupByLibrary.simpleMessage("Добавьте карточки"),
        "emptyDeckUserMessage": m2,
        "emptyDecksList":
            MessageLookupByLibrary.simpleMessage("Добавьте списки"),
        "emptyUserSharingList":
            MessageLookupByLibrary.simpleMessage("Поделитесь списком"),
        "errorUserMessage":
            MessageLookupByLibrary.simpleMessage("Произошла ошибка: "),
        "frontSideHint":
            MessageLookupByLibrary.simpleMessage("Передняя сторона"),
        "germanDeckType": MessageLookupByLibrary.simpleMessage("Немецкий"),
        "installEmailApp": MessageLookupByLibrary.simpleMessage(
            "Пожалуйста установите приложение Электронной Почты"),
        "inviteToAppMessage": MessageLookupByLibrary.simpleMessage(
            "Я приглашаю Вас установить Delern, систему интервального изучения, которая позволяет изучать быстро и легко!\n  \nПройдите по ссылке https://play.google.com/store/apps/details?id=org.dasfoo.delern чтобы установить из Google Play!\n  \nПосле установки, следите за последними новостями Delern:\nFacebook: https://fb.me/das.delern\nVK: https://vk.com/delern\nGoogle+: https://plus.google.com/communities/104603840044649051798\nTwitter: https://twitter.com/dasdelern"),
        "markdown": MessageLookupByLibrary.simpleMessage("Маркдаун"),
        "navigationDrawerAbout":
            MessageLookupByLibrary.simpleMessage("О приложении"),
        "navigationDrawerCommunicateGroup":
            MessageLookupByLibrary.simpleMessage("Общение"),
        "navigationDrawerContactUs":
            MessageLookupByLibrary.simpleMessage("Связаться с нами"),
        "navigationDrawerInviteFriends":
            MessageLookupByLibrary.simpleMessage("Пригласить друзей"),
        "navigationDrawerSignOut":
            MessageLookupByLibrary.simpleMessage("Выход"),
        "navigationDrawerSupportDevelopment":
            MessageLookupByLibrary.simpleMessage("Поддержать разработку"),
        "no": MessageLookupByLibrary.simpleMessage("нет"),
        "noAccess": MessageLookupByLibrary.simpleMessage("Нет доступа"),
        "noAddingWithReadAccessUserMessage":
            MessageLookupByLibrary.simpleMessage(
                "Вы не можете добавлять карточки с доступом на чтение."),
        "noDeletingWithReadAccessUserMessage":
            MessageLookupByLibrary.simpleMessage(
                "Вы не можете удалить карточки с доступом на чтение."),
        "noEditingWithReadAccessUserMessage":
            MessageLookupByLibrary.simpleMessage(
                "Вы не можете редактировать карточки с доступом на чтение."),
        "noSharingAccessUserMessage": MessageLookupByLibrary.simpleMessage(
            "Только владелец может поделиться списком."),
        "numberOfCards": m3,
        "owner": MessageLookupByLibrary.simpleMessage("Владелец"),
        "peopleLabel": MessageLookupByLibrary.simpleMessage("Люди"),
        "reversedCardLabel":
            MessageLookupByLibrary.simpleMessage("Добавить обратную карточку"),
        "save": MessageLookupByLibrary.simpleMessage("Сохранить"),
        "saveChangesQuestion": MessageLookupByLibrary.simpleMessage(
            "Вы хотите сохранить изменения?"),
        "searchHint": MessageLookupByLibrary.simpleMessage("Поиск..."),
        "send": MessageLookupByLibrary.simpleMessage("Отправить"),
        "settingsDeckMenu": MessageLookupByLibrary.simpleMessage("Настройки"),
        "shareDeckMenu": MessageLookupByLibrary.simpleMessage("Поделиться"),
        "signInWithGoogle":
            MessageLookupByLibrary.simpleMessage("Войти с помощью Google"),
        "supportDevelopment": MessageLookupByLibrary.simpleMessage(
            "Расскажите, пожалуйста, что мы можем сделать, чтобы улучшить Ваш опыт с Delern!\n       \nЕсли у вас есть какие-либо вопросы или предложения, свяжитесь с нами:\n[delern@dasfoo.org](mailto:delern@dasfoo.org) \n       \nCледите за последними новостями Delern:\n       \n- [Facebook](https://fb.me/das.delern) \n- [Twitter](https://twitter.com/dasdelern)\n- [Google+](https://plus.google.com/communities/104603840044649051798)\n- [VK](https://vk.com/delern)\n       \nЧтобы увидеть исходный код этого приложения, посетите [Delern GitHub repo](https://github.com/dasfoo/delern).\n      "),
        "swissDeckType": MessageLookupByLibrary.simpleMessage("Швейцарский"),
        "watchedCards": m4,
        "whoHasAccessLabel":
            MessageLookupByLibrary.simpleMessage("У кого есть доступ"),
        "yes": MessageLookupByLibrary.simpleMessage("Да")
      };
}
