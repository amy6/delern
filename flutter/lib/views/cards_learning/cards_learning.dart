import 'dart:async';
import 'dart:collection';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';
import '../../models/deck.dart';
import '../../view_models/learning_view_model.dart';
import '../../views/card_create_update/card_create_update.dart';
import '../helpers/card_background.dart';
import '../helpers/card_display.dart';
import '../helpers/progress_indicator.dart' as progressBar;
import '../helpers/save_updates_dialog.dart';

class CardsLearning extends StatefulWidget {
  final Deck deck;
  final bool allowEdit;

  CardsLearning({@required this.deck, @required this.allowEdit})
      : assert(deck != null),
        assert(allowEdit != null);

  @override
  State<StatefulWidget> createState() => CardsLearningState();
}

class CardsLearningState extends State<CardsLearning> {
  bool _isBackShown = false;
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
    if (_updates == null) {
      _updates = _viewModel.updates.listen(
          (_) => setState(() {
                _isBackShown = false;
              }),
          onDone: () => Navigator.of(context).pop());
    }
    return Scaffold(
      appBar: AppBar(
        title: Text(_viewModel.deck.name),
        actions: _viewModel.card == null ? null : <Widget>[_buildPopupMenu()],
      ),
      body: _viewModel.card == null
          ? progressBar.ProgressIndicator()
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
                        padding: EdgeInsets.only(top: 25.0, bottom: 20.0),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: _buildButtons(context),
                        ),
                      ),
                      Row(
                        children: <Widget>[
                          Text(
                            AppLocalizations.of(context)
                                .watchedCards(_watchedCount),
                            style: AppStyles.secondaryText,
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
              itemBuilder: (BuildContext context) => _buildMenu(context)
                  .entries
                  .map((entry) => PopupMenuItem<_CardMenuItemType>(
                        value: entry.key,
                        child: Text(entry.value),
                      ))
                  .toList(),
            ),
      );

  //heroTag - https://stackoverflow.com/questions/46509553/
  List<Widget> _buildButtons(BuildContext context) {
    if (_isBackShown) {
      return [
        // TODO(ksheremet): Make buttons disabled when card was answered and is saving to DB
        FloatingActionButton(
            heroTag: "dontknow",
            backgroundColor: Colors.red,
            child: Icon(Icons.clear),
            onPressed: () async {
              await _answerCard(false, context);
              setState(() {});
            }),
        FloatingActionButton(
            heroTag: "know",
            backgroundColor: Colors.green,
            child: Icon(Icons.check),
            onPressed: () async {
              await _answerCard(true, context);
              setState(() {});
            })
      ];
    } else
      return [
        FloatingActionButton(
            backgroundColor: Colors.orange,
            heroTag: "turn",
            child: Icon(Icons.cached),
            onPressed: () {
              setState(() {
                _isBackShown = true;
              });
            })
      ];
  }

  Future<void> _answerCard(bool answer, BuildContext context) async {
    try {
      await _viewModel.answer(answer);
      _isBackShown = false;
      _watchedCount++;
    } catch (e, stacktrace) {
      UserMessages.showError(() => Scaffold.of(context), e, stacktrace);
    }
  }

  void _onCardMenuItemSelected(BuildContext context, _CardMenuItemType item) {
    switch (item) {
      case _CardMenuItemType.edit:
        if (widget.allowEdit) {
          Navigator.push(
              context,
              MaterialPageRoute(
                  builder: (context) => CreateUpdateCard(_viewModel.card)));
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
}

enum _CardMenuItemType { edit, delete }

Map<_CardMenuItemType, String> _buildMenu(BuildContext context) =>
    LinkedHashMap<_CardMenuItemType, String>()
      ..[_CardMenuItemType.edit] = AppLocalizations.of(context).edit
      ..[_CardMenuItemType.delete] = AppLocalizations.of(context).delete;
