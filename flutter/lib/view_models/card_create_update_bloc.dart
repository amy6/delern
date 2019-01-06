import 'dart:async';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:delern_flutter/models/base/transaction.dart';
import 'package:delern_flutter/models/card_model.dart';
import 'package:delern_flutter/models/scheduled_card_model.dart';
import 'package:delern_flutter/remote/analytics.dart';
import 'package:delern_flutter/remote/error_reporting.dart';
import 'package:meta/meta.dart';

class CardCreateUpdateBloc {
  String _frontText;
  String _backText;
  bool _addReversedCard = false;
  final String uid;
  CardModel _cardModel;
  final AppLocalizations locale;
  final bool isAddOperation;
  bool _isOperationEnabled = true;

  CardCreateUpdateBloc(
      {@required this.uid, @required cardModel, @required this.locale})
      : assert(uid != null),
        assert(cardModel != null),
        isAddOperation = cardModel.key == null {
    this._cardModel = cardModel;
    _initFields();
    _initListeners();
  }

  final _saveCardController = StreamController<void>();
  Sink<void> get saveCardSink => _saveCardController.sink;

  final _frontSideTextController = StreamController<String>();
  Sink<String> get frontSideTextSink => _frontSideTextController.sink;

  final _backSideTextController = StreamController<String>();
  Sink<String> get backSideTextSink => _backSideTextController.sink;

  final _addReversedCardController = StreamController<bool>();
  Sink<bool> get addReversedCardSink => _addReversedCardController.sink;

  final _onCardAddedController = StreamController<String>();
  Stream<String> get onCardAdded => _onCardAddedController.stream;

  final _onErrorController = StreamController<String>();
  Stream<String> get onErrorOccurred => _onErrorController.stream;

  final _onPopController = StreamController<void>();
  Stream<void> get onPop => _onPopController.stream;

  final _isOperationEnabledController = StreamController<bool>();
  Stream<bool> get isOperationEnabled => _isOperationEnabledController.stream;

  void _initFields() {
    _frontText = _cardModel.front ?? '';
    _backText = _cardModel.back ?? '';
  }

  void _initListeners() {
    _saveCardController.stream.listen((_) => _processSavingCard());
    _frontSideTextController.stream.listen((frontText) {
      _frontText = frontText;
      _checkOperationAvailability();
    });
    _backSideTextController.stream.listen((backText) {
      _backText = backText;
      _checkOperationAvailability();
    });
    _addReversedCardController.stream.listen((addReversed) {
      _addReversedCard = addReversed;
      _checkOperationAvailability();
    });
  }

  Future<void> _saveCard() {
    logCardCreate(_cardModel.deckKey);
    var t = Transaction()..save(_cardModel);
    final sCard = ScheduledCardModel(deckKey: _cardModel.deckKey, uid: uid)
      ..key = _cardModel.key;
    t.save(sCard);

    if (_addReversedCard) {
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

  Future<void> _disableUI(Future<void> f()) async {
    _isOperationEnabled = false;
    _isOperationEnabledController.add(_isOperationEnabled);
    try {
      await f();
    } finally {
      _isOperationEnabled = true;
      _isOperationEnabledController.add(_isOperationEnabled);
    }
  }

  void _processSavingCard() async {
    _cardModel
      ..front = _frontText.trim()
      ..back = _backText.trim();
    try {
      await _disableUI(_saveCard);
      if (!isAddOperation) {
        _onPopController.add(null);
        return;
      }
      _clearCard();
      if (_addReversedCard) {
        _onCardAddedController.add(locale.cardAndReversedAddedUserMessage);
      } else {
        _onCardAddedController.add(locale.cardAddedUserMessage);
      }
    } catch (e, stackTrace) {
      ErrorReporting.report('saveCard', e, stackTrace ?? StackTrace.current);
      _onErrorController
          .add(UserMessages.formUserFriendlyErrorMessage(locale, e));
    }
  }

  void _clearCard() {
    // Unset Card key so that we create a new one.
    _cardModel.key = null;
  }

  bool _isCardValid() => _addReversedCard
      ? _frontText.trim().isNotEmpty && _backText.trim().isNotEmpty
      : _frontText.trim().isNotEmpty;

  void _checkOperationAvailability() {
    _isOperationEnabledController.add(_isOperationEnabled && _isCardValid());
  }

  void dispose() {
    _saveCardController.close();
    _onCardAddedController.close();
    _onPopController.close();
    _onErrorController.close();
    _frontSideTextController.close();
    _backSideTextController.close();
    _isOperationEnabledController.close();
    _addReversedCardController.close();
  }
}
