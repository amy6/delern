import 'dart:async';
import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';
import '../../models/deck.dart';
import '../../view_models/learning_view_model.dart';
import '../../views/card_create_update/card_create_update.dart';
import '../helpers/card_background.dart';
import '../helpers/card_display.dart';
import '../helpers/helper_progress_indicator.dart';
import '../helpers/save_updates_dialog.dart';
import '../helpers/slow_operation_widget.dart';

class CardsLearning extends StatefulWidget {
  final DeckModel deck;
  final bool allowEdit;

  const CardsLearning({@required this.deck, @required this.allowEdit})
      : assert(deck != null),
        assert(allowEdit != null);

  @override
  State<StatefulWidget> createState() => CardsLearningState();
}

class CardsLearningState extends State<CardsLearning> {
  /// Whether or not back side of the card is visible.
  bool _isBackShown = false;

  /// Whether the card on the display is scheduled for the time in future.
  /// Implies that the user has been asked to learn cards beyond current date,
  /// and replied positively.
  bool _learnBeyondHorizon = false;

  /// Whether we have shown at least one side of one card to the user (does not
  /// necessarily mean that they anwered it).
  bool _atLeastOneCardShown = false;

  /// Number of cards the user has answered (either positively or negatively) to
  /// in this session.
  // TODO(ksheremet): rename to "Answers", also in the UI.
  int _watchedCount = 0;

  LearningViewModel _viewModel;
  StreamSubscription<void> _updates;

  @override
  void initState() {
    _viewModel =
        LearningViewModel(deck: widget.deck, allowEdit: widget.allowEdit);
    super.initState();
  }

  @override
  void deactivate() {
    _updates?.cancel();
    _updates = null;
    super.deactivate();
  }

  @override
  Widget build(BuildContext context) {
    _updates ??= _viewModel.updates.listen((updateType) {
      if (!mounted) {
        return;
      }
      if (updateType == LearningUpdateType.scheduledCardUpdate) {
        _nextCardArrived();
      } else {
        // Usually a deck update.
        setState(() {});
      }
    },
        // Tell caller that no cards were available,
        onDone: () => Navigator.of(context).pop(false));

    return Scaffold(
      appBar: AppBar(
        title: Text(_viewModel.deck.name),
        actions: _viewModel.card == null ? null : <Widget>[_buildPopupMenu()],
      ),
      body: _viewModel.card == null
          ? HelperProgressIndicator()
          : Builder(
              builder: (context) => Column(
                    children: <Widget>[
                      Expanded(
                          child: CardDisplay(
                        front: _viewModel.card.front,
                        back: _viewModel.card.back ?? '',
                        showBack: _isBackShown,
                        backgroundColor: specifyCardBackground(
                            _viewModel.deck.type, _viewModel.card.back),
                        isMarkdown: _viewModel.deck.markdown,
                      )),
                      Padding(
                        padding: const EdgeInsets.only(top: 25.0, bottom: 20.0),
                        child: _buildButtons(context),
                      ),
                      Row(
                        children: <Widget>[
                          // Use SafeArea to indent the child by the amount
                          // necessary to avoid The Notch on the iPhone X,
                          // or other similar creative physical features of
                          // the display.
                          SafeArea(
                            child: Text(
                              AppLocalizations.of(context)
                                  .watchedCards(_watchedCount),
                              style: AppStyles.secondaryText,
                            ),
                          ),
                        ],
                      )
                    ],
                  ),
            ),
    );
  }

  Widget _buildPopupMenu() => Builder(
        builder: (context) => PopupMenuButton<_CardMenuItemType>(
              onSelected: (itemType) =>
                  _onCardMenuItemSelected(context, itemType),
              itemBuilder: (context) => _buildMenu(context)
                  .entries
                  .map((entry) => PopupMenuItem<_CardMenuItemType>(
                        value: entry.key,
                        child: Text(entry.value),
                      ))
                  .toList(),
            ),
      );

  Widget _buildButtons(BuildContext context) {
    if (_isBackShown) {
      return SlowOperationWidget((cb) => Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              FloatingActionButton(
                // heroTag - https://stackoverflow.com/questions/46509553/
                heroTag: 'dontknow',
                backgroundColor: Colors.red,
                child: const Icon(Icons.clear),
                onPressed: cb(() => _answerCard(false, context)),
              ),
              FloatingActionButton(
                heroTag: 'know',
                backgroundColor: Colors.green,
                child: const Icon(Icons.check),
                onPressed: cb(() => _answerCard(true, context)),
              ),
            ],
          ));
    }

    return Row(mainAxisAlignment: MainAxisAlignment.center, children: [
      FloatingActionButton(
          backgroundColor: Colors.orange,
          heroTag: 'turn',
          child: const Icon(Icons.cached),
          onPressed: () {
            setState(() {
              _isBackShown = true;
            });
          })
    ]);
  }

  Future<void> _answerCard(bool answer, BuildContext context) async {
    try {
      await _viewModel.answer(answer, _learnBeyondHorizon);
    } catch (e, stacktrace) {
      UserMessages.showError(() => Scaffold.of(context), e, stacktrace);
      return;
    }

    if (mounted) {
      setState(() {
        _watchedCount++;
      });
    }
  }

  void _onCardMenuItemSelected(BuildContext context, _CardMenuItemType item) {
    switch (item) {
      case _CardMenuItemType.edit:
        if (widget.allowEdit) {
          Navigator.push(
              context,
              MaterialPageRoute(
                  settings: const RouteSettings(name: '/cards/edit'),
                  builder: (context) => CreateUpdateCard(
                        card: _viewModel.card,
                        deck: widget.deck,
                      )));
        } else {
          UserMessages.showMessage(Scaffold.of(context),
              AppLocalizations.of(context).noEditingWithReadAccessUserMessage);
        }
        break;
      case _CardMenuItemType.delete:
        if (widget.allowEdit) {
          _deleteCard(context);
        } else {
          UserMessages.showMessage(Scaffold.of(context),
              AppLocalizations.of(context).noDeletingWithReadAccessUserMessage);
        }
        break;
    }
  }

  void _deleteCard(BuildContext context) async {
    var locale = AppLocalizations.of(context);
    var saveChanges = await showSaveUpdatesDialog(
        context: context,
        changesQuestion: locale.deleteCardQuestion,
        yesAnswer: locale.delete,
        noAnswer: MaterialLocalizations.of(context).cancelButtonLabel);
    if (saveChanges) {
      try {
        await _viewModel.deleteCard();
        UserMessages.showMessage(Scaffold.of(context),
            AppLocalizations.of(context).cardDeletedUserMessage);
      } catch (e, stackTrace) {
        UserMessages.showError(() => Scaffold.of(context), e, stackTrace);
      }
    }
  }

  void _nextCardArrived() async {
    setState(() {
      // For a new card we show, hide the back side.
      _isBackShown = false;
    });

    if (!_learnBeyondHorizon &&
        _viewModel.scheduledCard.repeatAt.isAfter(DateTime.now().toUtc())) {
      if (!_atLeastOneCardShown) {
        _learnBeyondHorizon = await showSaveUpdatesDialog(
                context: context,
                changesQuestion: AppLocalizations.of(context)
                    .continueLearningQuestion(DateFormat.yMMMd()
                        .add_jm()
                        .format(_viewModel.scheduledCard.repeatAt)),
                noAnswer: AppLocalizations.of(context).no,
                yesAnswer: AppLocalizations.of(context).yes) ==
            true;
      }
      if (!_learnBeyondHorizon) {
        Navigator.of(context).pop();
      }
    }

    _atLeastOneCardShown = true;
  }
}

enum _CardMenuItemType { edit, delete }

Map<_CardMenuItemType, String> _buildMenu(BuildContext context) =>
    // We want this Map to be ordered.
    // ignore: prefer_collection_literals
    LinkedHashMap<_CardMenuItemType, String>()
      ..[_CardMenuItemType.edit] = AppLocalizations.of(context).edit
      ..[_CardMenuItemType.delete] = AppLocalizations.of(context).delete;
