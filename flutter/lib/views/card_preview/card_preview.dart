import 'dart:async';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/user_messages.dart';
import '../../models/card.dart' as cardModel;
import '../../view_models/card_view_model.dart';
import '../../views/helpers/card_background.dart';
import '../card_create_update/card_create_update.dart';
import '../helpers/card_display.dart';
import '../helpers/save_updates_dialog.dart';

class CardPreview extends StatefulWidget {
  final cardModel.Card _card;

  CardPreview(this._card);

  @override
  State<StatefulWidget> createState() => _CardPreviewState();
}

class _CardPreviewState extends State<CardPreview> {
  CardViewModel _viewModel;
  StreamSubscription<void> _viewModelUpdates;

  @override
  void initState() {
    _viewModel = CardViewModel(widget._card);
    super.initState();
  }

  @override
  void deactivate() {
    _viewModelUpdates?.cancel();
    _viewModelUpdates = null;
    super.deactivate();
  }

  @override
  Widget build(BuildContext context) {
    if (_viewModelUpdates == null) {
      _viewModelUpdates = _viewModel.updates.listen((_) => setState(() {}));
    }
    return Scaffold(
      appBar: AppBar(
        title: Text(_viewModel.card.deck.name),
        actions: <Widget>[
          Builder(
            builder: (context) => IconButton(
                icon: Icon(Icons.delete),
                onPressed: () async {
                  var locale = AppLocalizations.of(context);
                  bool saveChanges = await showSaveUpdatesDialog(
                      context: context,
                      changesQuestion: locale.deleteCardQuestion,
                      yesAnswer: locale.delete,
                      noAnswer: locale.cancel);
                  if (saveChanges) {
                    try {
                      await _viewModel.deleteCard();
                    } catch (e, stackTrace) {
                      UserMessages.showError(
                          () => Scaffold.of(context), e, stackTrace);
                      return;
                    }
                    Navigator.of(context).pop();
                  }
                }),
          )
        ],
      ),
      body: Column(
        children: <Widget>[
          Expanded(
              child: CardDisplay(
            front: _viewModel.card.front,
            back: _viewModel.card.back,
            showBack: true,
            backgroundColor: specifyCardBackground(
                _viewModel.card.deck.type, _viewModel.card.back),
          )),
          Padding(padding: EdgeInsets.only(bottom: 100.0))
        ],
      ),
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.edit),
        onPressed: () => Navigator.push(
            context,
            MaterialPageRoute(
                builder: (context) => CreateUpdateCard(_viewModel.card))),
      ),
    );
  }
}
