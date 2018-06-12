import 'dart:async';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../models/card.dart' as model;
import '../models/deck.dart';
import '../view_models/card_view_model.dart';

class CreateUpdateCard extends StatefulWidget {
  final Deck _deck;
  final CardViewModel _cardViewModel;

  CreateUpdateCard(this._deck, this._cardViewModel);

  @override
  State<StatefulWidget> createState() => _CreateUpdateCardState();
}

class _CreateUpdateCardState extends State<CreateUpdateCard> {
  bool _addReversedCard;
  TextEditingController _frontTextController = new TextEditingController();
  TextEditingController _backTextController = new TextEditingController();

  @override
  void initState() {
    super.initState();
    if (widget._cardViewModel != null) {
      _frontTextController.text = widget._cardViewModel.front;
      _backTextController.text = widget._cardViewModel.back;
    }
  }

  @override
  Widget build(BuildContext context) {
    return new WillPopScope(
      onWillPop: () async {
        if (widget._cardViewModel != null) {
          var card = widget._cardViewModel.card;
          card.front = _frontTextController.text;
          card.back = _backTextController.text;
          try {
            await card.save();
            // TODO(ksheremet): Show user message that card was updated
            print("Card was added");
          } catch (e) {
            // TODO(ksheremet): Report error
            print(e);
          }
        }
        return true;
      },
      child: new Scaffold(
        appBar: buildAppBar(),
        body: buildBody(),
      ),
    );
  }

  //TODO(ksheremet): Add SAVE button for updating card
  Widget buildAppBar() {
    if (widget._cardViewModel == null) {
      return new AppBar(
        title: new Text(widget._deck.name),
        actions: <Widget>[
          new IconButton(
              icon: new Icon(Icons.check),
              onPressed: (_frontTextController.text.isEmpty ||
                      _backTextController.text.isEmpty)
                  ? null
                  : () async {
                      await _addCardToDb();
                      setState(() {
                        _clearFields();
                      });
                    })
        ],
      );
    } else {
      return new AppBar(
        title: new Text(widget._deck.name),
      );
    }
  }

  Future<void> _addCardToDb() async {
    var card = model.Card(widget._deck.key,
        front: _frontTextController.text, back: _backTextController.text);
    try {
      await card.save(widget._deck.uid);
      if (_addReversedCard == true) {
        card = model.Card(widget._deck.key,
            front: _backTextController.text, back: _frontTextController.text);
        await card.save(widget._deck.uid);
      }
    } catch (e) {
      // TODO(ksheremet): Show snackbar to user on success and failure
      // TODO(ksheremet): In case of error sent it to sentry
      print(e);
    }
  }

  Widget buildBody() {
    List<Widget> builder = [
      // TODO(ksheremet): limit lines in TextField
      new TextField(
        maxLines: null,
        keyboardType: TextInputType.multiline,
        controller: _frontTextController,
        onChanged: (String text) {
          setState(() {});
        },
        decoration: new InputDecoration(
            hintText: AppLocalizations.of(context).frontSideHint),
      ),
      new TextField(
        maxLines: null,
        keyboardType: TextInputType.multiline,
        controller: _backTextController,
        onChanged: (String text) {
          setState(() {});
        },
        decoration: new InputDecoration(
          hintText: AppLocalizations.of(context).backSideHint,
        ),
      ),
    ];

    // Add reversed card widget it it is adding new cards
    if (widget._cardViewModel == null) {
      builder.add(new Row(
        children: <Widget>[
          new Checkbox(
              value: _addReversedCard == null ? false : _addReversedCard,
              onChanged: (bool newValue) {
                setState(() {
                  _addReversedCard = newValue;
                });
              }),
          new Text(AppLocalizations.of(context).reversedCardLabel),
        ],
      ));
    }

    return new ListView(
      padding: const EdgeInsets.only(left: 8.0, right: 8.0),
      children: builder,
    );
  }

  void _clearFields() {
    _frontTextController.clear();
    _backTextController.clear();
  }
}
