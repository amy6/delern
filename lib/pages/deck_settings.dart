import 'dart:async';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../flutter/user_messages.dart';
import '../models/deck.dart';
import '../models/deck_access.dart';
import '../view_models/deck_view_model.dart';
import '../widgets/deck_type_dropdown.dart';
import '../widgets/save_updates_dialog.dart';

class DeckSettingsPage extends StatefulWidget {
  final Deck _deck;
  final DeckAccess _access;

  DeckSettingsPage(this._deck, this._access);

  @override
  State<StatefulWidget> createState() => _DeckSettingsPageState();
}

//TODO(ksheremet): Save changes to DB
class _DeckSettingsPageState extends State<DeckSettingsPage> {
  final _scaffoldKey = new GlobalKey<ScaffoldState>();
  TextEditingController _deckNameController = new TextEditingController();
  DeckViewModel _viewModel;
  StreamSubscription<void> _viewModelUpdates;
  DeckType _deckTypeValue;
  bool _isMarkdown = false;
  bool _isDeckChanged = false;

  @override
  void initState() {
    _deckNameController.text = widget._deck.name;
    _viewModel = DeckViewModel(widget._deck, widget._access);
    _deckTypeValue = _viewModel.deck.type;
    _isMarkdown = _viewModel.deck.markdown;
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
    return WillPopScope(
      onWillPop: () async {
        if (_isDeckChanged) {
          try {
            await _viewModel.saveDeck();
            return true;
          } catch (e, stackTrace) {
            UserMessages.showError(_scaffoldKey.currentState, e, stackTrace);
            return false;
          }
        }
        return true;
      },
      child: Scaffold(
          key: _scaffoldKey,
          appBar: AppBar(title: Text(_viewModel.deck.name), actions: <Widget>[
            IconButton(
                icon: Icon(Icons.delete),
                onPressed: () async {
                  var locale = AppLocalizations.of(context);
                  var deleteDeckDialog = await showSaveUpdatesDialog(
                      context: context,
                      changesQuestion: locale.deleteDeckQuestion,
                      yesAnswer: locale.delete,
                      noAnswer: locale.cancel);
                  if (deleteDeckDialog) {
                    try {
                      await _viewModel.delete();
                    } catch (e, stackTrace) {
                      UserMessages.showError(
                          Scaffold.of(context), e, stackTrace);
                      return;
                    }
                    Navigator.of(context).pop();
                  }
                }),
          ]),
          body: _buildBody()),
    );
  }

  Widget _buildBody() {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Column(
        children: <Widget>[
          TextField(
            maxLines: null,
            keyboardType: TextInputType.multiline,
            controller: _deckNameController,
            onChanged: (String text) {
              setState(() {
                _isDeckChanged = true;
              });
            },
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.start,
            children: <Widget>[
              Padding(
                padding: const EdgeInsets.only(top: 24.0),
                child: Text(AppLocalizations.of(context).deckType),
              ),
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.start,
            children: <Widget>[
              DeckTypeDropdown(
                value: _deckTypeValue,
                valueChanged: (DeckType newDeckType) => setState(() {
                      _deckTypeValue = newDeckType;
                    }),
              ),
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: <Widget>[
              Text(AppLocalizations.of(context).markdown),
              Switch(
                value: _isMarkdown,
                onChanged: (newValue) {
                  setState(() {
                    _isMarkdown = newValue;
                  });
                },
              )
            ],
          ),
        ],
      ),
    );
  }
}
