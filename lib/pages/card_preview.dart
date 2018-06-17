import 'dart:async';

import 'package:flutter/material.dart';

import '../models/deck.dart';
import '../pages/card_create_update.dart';
import '../view_models/card_view_model.dart';
import '../widgets/card_display.dart';

class CardPreview extends StatefulWidget {
  final Deck _deck;
  final CardViewModel _cardViewModel;

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
              onPressed: () {
                // TODO(ksheremet): Confirm that user wants to delete the card.
                _deleteCard();
                Navigator.of(context).pop();
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
      print("Card was deleted");
      return true;
    } catch (e) {
      print("Error occurred by deleting");
      return false;
    }
  }
}
