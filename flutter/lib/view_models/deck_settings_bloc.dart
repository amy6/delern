import 'dart:async';

import 'package:delern_flutter/models/base/transaction.dart';
import 'package:delern_flutter/models/card_model.dart';
import 'package:delern_flutter/models/card_reply_model.dart';
import 'package:delern_flutter/models/deck_access_model.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/models/scheduled_card_model.dart';
import 'package:delern_flutter/remote/analytics.dart';
import 'package:delern_flutter/remote/error_reporting.dart';
import 'package:delern_flutter/view_models/base/screen_bloc.dart';
import 'package:meta/meta.dart';

class DeckSettingsUIState {
  String deckName;
  DeckType deckType;
  bool isMarkdown;
}

class DeckSettingsBloc extends ScreenBloc {
  final DeckModel _deck;
  DeckSettingsUIState _settingsUiState;

  DeckSettingsBloc({@required DeckModel deck})
      : assert(deck != null),
        _deck = deck {
    _initListeners();
  }

  final _deleteDeckController = StreamController<void>();
  Sink<void> get deleteDeckSink => _deleteDeckController.sink;

  final _deleteDeckIntentionController = StreamController<void>();
  Sink<void> get deleteDeckIntentionSink => _deleteDeckIntentionController.sink;

  final _showDialogController = StreamController<String>();
  Stream<String> get showConfirmationDialog => _showDialogController.stream;

  final _deckSettingsUiStateController =
      StreamController<DeckSettingsUIState>();
  Sink<DeckSettingsUIState> get deckSettingsUiState =>
      _deckSettingsUiStateController.sink;

  Future<void> _delete() async {
    logDeckDelete(_deck.key);
    var t = Transaction()..delete(_deck);
    var card = CardModel(deckKey: _deck.key);
    if (_deck.access == AccessType.owner) {
      final accessList = DeckAccessModel.getList(deckKey: _deck.key);
      await accessList.fetchFullValue();
      accessList
          .forEach((a) => t.delete(DeckModel(uid: a.key)..key = _deck.key));
      t..deleteAll(DeckAccessModel(deckKey: _deck.key))..deleteAll(card);
      // TODO(dotdoom): delete other users' ScheduledCard and Views?
    }
    t
      ..deleteAll(ScheduledCardModel(deckKey: _deck.key, uid: _deck.uid))
      ..deleteAll(
          CardReplyModel(uid: _deck.uid, deckKey: _deck.key, cardKey: null));
    await t.commit();
  }

  Future<void> _save() => (Transaction()..save(_deck)).commit();

  @override
  void dispose() {
    _deleteDeckController.close();
    _deleteDeckIntentionController.close();
    _showDialogController.close();
    _deckSettingsUiStateController.close();
    super.dispose();
  }

  Future<bool> _saveDeckSettings() async {
    _deck
      ..name = _settingsUiState.deckName
      ..markdown = _settingsUiState.isMarkdown
      ..type = _settingsUiState.deckType;
    try {
      await _save();
      return true;
    } catch (e, stackTrace) {
      ErrorReporting.report('updateDeck', e, stackTrace ?? StackTrace.current);
      super.notifyErrorOccurred(e);
    }
    return false;
  }

  void _initListeners() {
    _deleteDeckController.stream.listen((_) async {
      try {
        await _delete();
        super.notifyPop();
      } catch (e, stackTrace) {
        ErrorReporting.report(
            'deleteCard', e, stackTrace ?? StackTrace.current);
        super.notifyErrorOccurred(e);
      }
    });

    _deleteDeckIntentionController.stream.listen((_) {
      String deleteDeckQuestion;
      switch (_deck.access) {
        case AccessType.owner:
          deleteDeckQuestion = locale.deleteDeckOwnerAccessQuestion;
          break;
        case AccessType.write:
        case AccessType.read:
          deleteDeckQuestion = locale.deleteDeckWriteReadAccessQuestion;
          break;
      }
      _showDialogController.add(deleteDeckQuestion);
    });

    _deckSettingsUiStateController.stream.listen((settingsUiState) {
      _settingsUiState = settingsUiState;
    });
  }

  void closeScreen() async {
    // TODO(ksheremet): Consider to check whether deck settings changed
    if (await _saveDeckSettings()) {
      super.notifyPop();
    }
  }
}
