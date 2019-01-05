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
  bool addReversed;
}

class CardCreateUpdateBloc {
  final String uid;
  CardModel _cardModel;
  final AppLocalizations locale;
  bool isAddOperation = false;
  bool isOperationEnabled = true;

  CardCreateUpdateBloc(
      {@required this.uid, @required cardModel, @required this.locale})
      : assert(uid != null),
        assert(cardModel != null) {
    this._cardModel = cardModel;
    if (cardModel.key == null) {
      isAddOperation = true;
    }
    _saveCardController.stream.listen((cardUIState) async {
      cardModel
        ..front = cardUIState.front.trim()
        ..back = cardUIState.back.trim();
      try {
        await _saveCard(cardUIState.addReversed);
        isOperationEnabled = true;
        if (!isAddOperation) {
          _onPopController.add(null);
          return;
        }
        _clearFields();
        if (cardUIState.addReversed) {
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

  final _saveCardController = StreamController<CreateUpdateUIState>();
  Sink<CreateUpdateUIState> get saveCardSink => _saveCardController.sink;

  final _onUserMessageController = StreamController<String>();
  Stream<String> get onUserMessage => _onUserMessageController.stream;

  final _onPopController = StreamController<void>();
  Stream<void> get onPop => _onPopController.stream;

  Future<void> _saveCard(bool addReversed) {
    logCardCreate(_cardModel.deckKey);

    var t = Transaction()..save(_cardModel);
    final sCard = ScheduledCardModel(deckKey: _cardModel.deckKey, uid: uid)
      ..key = _cardModel.key;
    t.save(sCard);

    if (addReversed) {
      var reverse = CardModel.copyFrom(_cardModel)
        ..key = null
        ..front = _cardModel.back
        ..back = _cardModel.front;
      t.save(reverse);
      var reverseScCard = ScheduledCardModel(deckKey: reverse.deckKey, uid: uid)
        ..key = reverse.key;
      t.save(reverseScCard);
    }
    return t.commit();
  }

  void _clearFields() {
    // Unset Card key so that we create a new one.
    _cardModel.key = null;
  }

  void dispose() {
    _saveCardController.close();
    _onUserMessageController.close();
    _onPopController.close();
  }
}
