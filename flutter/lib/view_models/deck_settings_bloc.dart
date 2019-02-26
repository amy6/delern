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

class DeckSettingsBloc extends ScreenBloc {
  final DeckModel _deck;
  String _deckName;
  DeckType _deckType;
  bool _markdown;

  DeckSettingsBloc({@required DeckModel deck})
      : assert(deck != null),
        _deck = deck {
    _deckName = deck.name;
    _deckType = deck.type;
    _markdown = deck.markdown;
    _initListeners();
  }

  final _onDeleteDeckController = StreamController<void>();
  Sink<void> get onDeleteDeck => _onDeleteDeckController.sink;

  final _onDeleteDeckIntention = StreamController<void>();
  Sink<void> get onDeleteDeckIntention => _onDeleteDeckIntention.sink;

  final _doShowConfirmationDialogController = StreamController<String>();
  Stream<String> get doShowConfirmationDialog =>
      _doShowConfirmationDialogController.stream;

  final _onDeckNameController = StreamController<String>();
  Sink<String> get onDeckName => _onDeckNameController.sink;

  final _onDeckTypeController = StreamController<DeckType>();
  Sink<DeckType> get onDeckType => _onDeckTypeController.sink;

  final _onMarkdownController = StreamController<bool>();
  Sink<bool> get onMarkdown => _onMarkdownController.sink;

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
    _onDeleteDeckController.close();
    _onDeleteDeckIntention.close();
    _doShowConfirmationDialogController.close();
    _onDeckTypeController.close();
    _onDeckNameController.close();
    _onMarkdownController.close();
    super.dispose();
  }

  Future<bool> _saveDeckSettings() async {
    _deck
      ..name = _deckName
      ..markdown = _markdown
      ..type = _deckType;
    try {
      await _save();
      return true;
    } catch (e, stackTrace) {
      ErrorReporting.report('updateDeck', e, stackTrace ?? StackTrace.current);
      notifyErrorOccurred(e);
    }
    return false;
  }

  void _initListeners() {
    _onDeleteDeckController.stream.listen((_) async {
      try {
        await _delete();
        notifyPop();
      } catch (e, stackTrace) {
        ErrorReporting.report(
            'deleteCard', e, stackTrace ?? StackTrace.current);
        super.notifyErrorOccurred(e);
      }
    });

    _onDeleteDeckIntention.stream.listen((_) {
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
      _doShowConfirmationDialogController.add(deleteDeckQuestion);
    });

    _onDeckNameController.stream.listen((name) => _deckName = name);

    _onDeckTypeController.stream.listen((deckType) => _deckType = deckType);

    _onMarkdownController.stream.listen((markdown) => _markdown = markdown);
  }

  @override
  Future<bool> userClosesScreen() async => await _saveDeckSettings();
}
