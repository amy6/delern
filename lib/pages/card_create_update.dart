import 'dart:async';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../models/card.dart' as model;
import '../models/deck.dart';
import '../view_models/card_list_view_model.dart';
import '../widgets/save_updates_dialog.dart';

class CreateUpdateCard extends StatefulWidget {
  final Deck _deck;
  final CardListItemViewModel _cardViewModel;

  CreateUpdateCard(this._deck, this._cardViewModel);

  @override
  State<StatefulWidget> createState() => _CreateUpdateCardState();
}

class _CreateUpdateCardState extends State<CreateUpdateCard> {
  bool _addReversedCard;
  bool _isChanged = false;
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
        if (_isChanged) {
          var locale = AppLocalizations.of(context);
          var saveChangesDialog = await showSaveUpdatesDialog(
              context: context,
              changesQuestion: locale.saveChangesQuestion,
              yesAnswer: locale.save,
              noAnswer: locale.cancel);
          if (saveChangesDialog) {
            return await _saveCard();
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

  Widget buildAppBar() {
    return new AppBar(
      title: new Text(widget._deck.name),
      actions: <Widget>[
        widget._cardViewModel == null
            ? new IconButton(
                icon: new Icon(Icons.check),
                onPressed: (_frontTextController.text.isEmpty ||
                        _backTextController.text.isEmpty)
                    ? null
                    : () async {
                        // TODO(ksheremer): disable button when writing to db
                        if (await _saveCard()) {
                          _isChanged = false;
                          setState(() {
                            _clearFields();
                          });
                        }
                      })
            : new FlatButton(
                child: new Text(
                  AppLocalizations.of(context).save.toUpperCase(),
                  style: _isChanged ? new TextStyle(color: Colors.white) : null,
                ),
                onPressed: _isChanged
                    ? () async {
                        if (await _saveCard()) {
                          _isChanged = false;
                          Navigator.of(context).pop();
                        }
                      }
                    : null)
      ],
    );
  }

  Future<bool> _saveCard() async {
    try {
      if (widget._cardViewModel == null) {
        // TODO(ksheremet): Consider to check that front or back are empty.
        await _addCard();
      } else {
        await _updateCard();
      }
      // TODO(ksheremet): Print message for user
      return true;
    } catch (e) {
      // TODO(ksheremet): Print message for user
      return false;
    }
  }

  Future<void> _addCard() async {
    var card = model.Card(widget._deck.key,
        front: _frontTextController.text, back: _backTextController.text);
    card.save(widget._deck.uid);
    if (_addReversedCard == true) {
      card = model.Card(widget._deck.key,
          front: _backTextController.text, back: _frontTextController.text);
      card.save(widget._deck.uid);
    }
  }

  Future<void> _updateCard() async {
    var card = widget._cardViewModel.card;
    card.front = _frontTextController.text;
    card.back = _backTextController.text;
    return card.save();
  }

  Widget buildBody() {
    List<Widget> widgetsList = [
      // TODO(ksheremet): limit lines in TextField
      new TextField(
        maxLines: null,
        keyboardType: TextInputType.multiline,
        controller: _frontTextController,
        onChanged: (String text) {
          setState(() {});
          _isChanged = true;
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
          _isChanged = true;
        },
        decoration: new InputDecoration(
          hintText: AppLocalizations.of(context).backSideHint,
        ),
      ),
    ];

    // Add reversed card widget it it is adding new cards
    if (widget._cardViewModel == null) {
      widgetsList.add(new Row(
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
      children: widgetsList,
    );
  }

  void _clearFields() {
    _frontTextController.clear();
    _backTextController.clear();
  }
}
