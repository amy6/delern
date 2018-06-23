import 'dart:async';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../flutter/show_error.dart';
import '../models/deck.dart';
import '../models/card.dart' as cardModel;
import '../view_models/card_view_model.dart';
import '../widgets/save_updates_dialog.dart';

class CreateUpdateCard extends StatefulWidget {
  final Deck _deck;
  final cardModel.Card _card;

  CreateUpdateCard(this._deck, [this._card]);

  @override
  State<StatefulWidget> createState() => _CreateUpdateCardState();
}

class _CreateUpdateCardState extends State<CreateUpdateCard> {
  bool _addReversedCard = false;
  bool _isChanged = false;
  TextEditingController _frontTextController = new TextEditingController();
  TextEditingController _backTextController = new TextEditingController();
  final _scaffoldKey = new GlobalKey<ScaffoldState>();
  CardViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = CardViewModel(widget._deck, widget._card);
    if (_viewModel.card.key != null) {
      _frontTextController.text = _viewModel.card.front;
      _backTextController.text = _viewModel.card.back;
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
        key: _scaffoldKey,
        appBar: _buildAppBar(),
        body: _buildBody(),
      ),
    );
  }

  Widget _buildAppBar() {
    return new AppBar(
      title: new Text(_viewModel.deck.name),
      actions: <Widget>[
        _viewModel.card.key == null
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
      // TODO(ksheremet): Consider to check that front or back are empty.
      _viewModel.card.front = _frontTextController.text;
      _viewModel.card.back = _backTextController.text;
      _viewModel.saveCard(_addReversedCard);
      return true;
    } catch (e, stacktrace) {
      showError(_scaffoldKey.currentState, e, stacktrace);
      return false;
    }
  }

  Widget _buildBody() {
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
    if (_viewModel.card.key == null) {
      widgetsList.add(new Row(
        children: <Widget>[
          new Checkbox(
              value: _addReversedCard,
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
