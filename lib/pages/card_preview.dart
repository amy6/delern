import 'dart:async';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../flutter/user_messages.dart';
import '../models/card.dart' as cardModel;
import '../models/deck.dart';
import '../pages/card_create_update.dart';
import '../view_models/card_view_model.dart';
import '../widgets/card_display.dart';
import '../widgets/save_updates_dialog.dart';

class CardPreview extends StatefulWidget {
  final Deck _deck;
  final cardModel.Card _card;

  CardPreview(this._deck, this._card);

  @override
  State<StatefulWidget> createState() => new _CardPreviewState();
}

class _CardPreviewState extends State<CardPreview> {
  CardViewModel _viewModel;
  StreamSubscription<void> _viewModelUpdates;

  @override
  void initState() {
    _viewModel = CardViewModel(widget._deck, widget._card);
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
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(_viewModel.deck.name),
        actions: <Widget>[
          Builder(
            builder: (context) => IconButton(
                icon: new Icon(Icons.delete),
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
                      Navigator.of(context).pop();
                    } catch (e, stackTrace) {
                      UserMessages.showError(
                          Scaffold.of(context), e, stackTrace);
                    }
                  }
                }),
          )
        ],
      ),
      body: Column(
        children: <Widget>[
          new Expanded(
              child: new CardDisplay(_viewModel.card.front,
                  _viewModel.card.back, /*show back*/ true)),
          new Padding(padding: const EdgeInsets.only(bottom: 100.0))
        ],
      ),
      floatingActionButton: new FloatingActionButton(
        child: new Icon(Icons.edit),
        onPressed: () => Navigator.push(
            context,
            new MaterialPageRoute(
                builder: (context) =>
                    //TODO(ksheremet): pass appropriate viewModel instead of _deck
                    new CreateUpdateCard(_viewModel.deck, _viewModel.card))),
      ),
    );
  }
}
