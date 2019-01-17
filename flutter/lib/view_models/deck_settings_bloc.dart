import 'dart:async';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:delern_flutter/models/base/transaction.dart';
import 'package:delern_flutter/models/card_model.dart';
import 'package:delern_flutter/models/card_reply_model.dart';
import 'package:delern_flutter/models/deck_access_model.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/models/scheduled_card_model.dart';
import 'package:delern_flutter/remote/analytics.dart';
import 'package:delern_flutter/remote/error_reporting.dart';
import 'package:meta/meta.dart';

class DeckSettingsModel {
  String deckName;
  DeckType deckType;
  bool isMarkdown;
}

class DeckSettingsBloc {
  final DeckModel _deck;
  final AppLocalizations locale;

  DeckSettingsBloc({@required DeckModel deck, @required this.locale})
      : assert(deck != null),
        assert(locale != null),
        this._deck = deck {
    _initListeners();
  }

  final _saveDeckController = StreamController<DeckSettingsModel>();
  Sink<DeckSettingsModel> get saveDeckSink => _saveDeckController.sink;

  final _deleteDeckController = StreamController<void>();
  Sink<void> get deleteDeckSink => _deleteDeckController.sink;

  final _onErrorController = StreamController<String>();
  Stream<String> get onErrorOccurred => _onErrorController.stream;

  final _onPopController = StreamController<void>();
  Stream<void> get onPop => _onPopController.stream;

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

  Future<void> save() => (Transaction()..save(_deck)).commit();

  void dispose() {
    _saveDeckController.close();
    _onErrorController.close();
    _onPopController.close();
    _deleteDeckController.close();
  }

  void _initListeners() {
    _saveDeckController.stream.listen((deckSettingsModel) async {
      _deck
        ..name = deckSettingsModel.deckName
        ..markdown = deckSettingsModel.isMarkdown
        ..type = deckSettingsModel.deckType;
      try {
        await save();
      } catch (e, stackTrace) {
        ErrorReporting.report(
            'updateDeck', e, stackTrace ?? StackTrace.current);
        _onErrorController
            .add(UserMessages.formUserFriendlyErrorMessage(locale, e));
      }
    });

    _deleteDeckController.stream.listen((_) async {
      try {
        await _delete();
        _onPopController.add(null);
      } catch (e, stackTrace) {
        ErrorReporting.report(
            'deleteCard', e, stackTrace ?? StackTrace.current);
        _onErrorController
            .add(UserMessages.formUserFriendlyErrorMessage(locale, e));
      }
    });
  }
}
