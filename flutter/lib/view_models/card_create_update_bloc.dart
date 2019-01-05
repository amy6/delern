import 'dart:async';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:delern_flutter/models/base/transaction.dart';
import 'package:delern_flutter/models/card_model.dart';
import 'package:delern_flutter/models/scheduled_card_model.dart';
import 'package:delern_flutter/remote/analytics.dart';
import 'package:delern_flutter/remote/error_reporting.dart';
import 'package:meta/meta.dart';

class CreateUpdateUIState {
  String front;
  String back;
}

class CardCreateUpdateBloc {
  final String uid;
  final CardModel cardModel;
  final AppLocalizations locale;
  CreateUpdateUIState uiState;
  bool isAddOperation = false;
  bool isOperationEnabled = true;

  CardCreateUpdateBloc(
      {@required this.uid, @required this.cardModel, @required this.locale})
      : assert(uid != null),
        assert(cardModel != null) {
    if (cardModel.key == null) {
      isAddOperation = true;
    }
    uiState = CreateUpdateUIState()
      ..front = cardModel.front ?? ''
      ..back = cardModel.back ?? '';
    _saveCardController.stream.listen((addReversed) async {
      cardModel
        ..front = uiState.front.trim()
        ..back = uiState.back.trim();
      try {
        await _saveCard(addReversed);
        isOperationEnabled = true;
        if (!isAddOperation) {
          _onPopController.add(null);
          return;
        }
        _clearFields();
        if (addReversed) {
          _onUserMessageController.add(locale.cardAndReversedAddedUserMessage);
        } else {
          _onUserMessageController.add(locale.cardAddedUserMessage);
        }
      } catch (e, stackTrace) {
        ErrorReporting.report('saveCard', e, stackTrace ?? StackTrace.current);
        _onUserMessageController
            .add(UserMessages.formUserFriendlyErrorMessage(locale, e));
      }
    });
  }

  final _saveCardController = StreamController<bool>();
  Sink<bool> get saveCardSink => _saveCardController.sink;

  final _onUserMessageController = StreamController<String>();
  Stream<String> get onUserMessage => _onUserMessageController.stream;

  final _onPopController = StreamController<void>();
  Stream<void> get onPop => _onPopController.stream;

  Future<void> _saveCard(bool addReversed) {
    logCardCreate(cardModel.deckKey);

    var t = Transaction()..save(cardModel);
    final sCard = ScheduledCardModel(deckKey: cardModel.deckKey, uid: uid)
      ..key = cardModel.key;
    t.save(sCard);

    if (addReversed) {
      var reverse = CardModel.copyFrom(cardModel)
        ..key = null
        ..front = cardModel.back
        ..back = cardModel.front;
      t.save(reverse);
      var reverseScCard = ScheduledCardModel(deckKey: reverse.deckKey, uid: uid)
        ..key = reverse.key;
      t.save(reverseScCard);
    }
    return t.commit();
  }

  void _clearFields() {
    // Unset Card key so that we create a new one.
    cardModel.key = null;
    uiState
      ..front = ''
      ..back = '';
  }

  void dispose() {
    _saveCardController.close();
    _onUserMessageController.close();
    _onPopController.close();
  }
}
