import 'dart:async';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../models/deck.dart';
import '../pages/card_create_update.dart';
import '../view_models/card_list_view_model.dart';
import '../widgets/card_display.dart';
import '../widgets/save_updates_dialog.dart';

class CardPreview extends StatefulWidget {
  final Deck _deck;
  final CardListItemViewModel _cardViewModel;

  CardPreview(this._deck, this._cardViewModel);

  @override
  State<StatefulWidget> createState() => new _CardPreviewState();
}

class _CardPreviewState extends State<CardPreview> {
  bool _active = false;

  @override
  void deactivate() {
    widget._cardViewModel.deactivate();
    _active = false;
    super.deactivate();
  }

  @override
  Widget build(BuildContext context) {
    if (!_active) {
      widget._cardViewModel.activate();
      _active = true;
    }
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(widget._deck.name),
        actions: <Widget>[
          new IconButton(
              icon: new Icon(Icons.delete),
              onPressed: () async {
                var locale = AppLocalizations.of(context);
                bool saveChanges = await showSaveUpdatesDialog(
                    context: context,
                    changesQuestion: locale.deleteCardQuestion,
                    yesAnswer: locale.delete,
                    noAnswer: locale.cancel);
                if (saveChanges && await _deleteCard()) {
                  Navigator.of(context).pop();
                }
              })
        ],
      ),
      body: Column(
        children: <Widget>[
          new Expanded(
              child: new CardDisplay(
                  widget._cardViewModel.front, widget._cardViewModel.back)),
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
                    new CreateUpdateCard(widget._deck, widget._cardViewModel))),
      ),
    );
  }

  Future<bool> _deleteCard() async {
    try {
      await widget._cardViewModel.card.delete(widget._deck.uid);
      // TODO(ksheremet): Show user message
      print("Card was deleted");
      return true;
    } catch (e) {
      // TODO(ksheremet): Show user message
      print("Error occurred by deleting");
      return false;
    }
  }
}
