import 'dart:async';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';
import '../../models/deck.dart';
import '../../models/deck_access.dart';
import '../../view_models/deck_view_model.dart';
import '../helpers/save_updates_dialog.dart';
import 'deck_type_dropdown.dart';

class DeckSettingsPage extends StatefulWidget {
  final Deck _deck;
  final DeckAccess _access;

  DeckSettingsPage(this._deck, this._access);

  @override
  State<StatefulWidget> createState() => _DeckSettingsPageState();
}

class _DeckSettingsPageState extends State<DeckSettingsPage> {
  final _scaffoldKey = GlobalKey<ScaffoldState>();
  TextEditingController _deckNameController = TextEditingController();
  DeckViewModel _viewModel;
  StreamSubscription<void> _viewModelUpdates;
  bool _isDeckChanged = false;

  @override
  void initState() {
    _deckNameController.text = widget._deck.name;
    _viewModel = DeckViewModel(widget._deck, widget._access);
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
            await _viewModel.save();
          } catch (e, stackTrace) {
            UserMessages.showError(
                () => _scaffoldKey.currentState, e, stackTrace);
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
                          () => _scaffoldKey.currentState, e, stackTrace);
                      return;
                    }
                    Navigator.of(context).pop();
                  }
                }),
          ]),
          body: _buildBody()),
    );
  }

  Widget _buildBody() => Padding(
        padding: EdgeInsets.all(8.0),
        child: Column(
          children: <Widget>[
            TextField(
              maxLines: null,
              keyboardType: TextInputType.multiline,
              controller: _deckNameController,
              style: AppStyles.primaryText,
              onChanged: (String text) {
                setState(() {
                  _isDeckChanged = true;
                  _viewModel.deck.name = text;
                });
              },
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.start,
              children: <Widget>[
                Padding(
                  padding: EdgeInsets.only(top: 24.0),
                  child: Text(
                    AppLocalizations.of(context).deckType,
                    style: AppStyles.secondaryText,
                  ),
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.start,
              children: <Widget>[
                DeckTypeDropdown(
                  value: _viewModel.deck.type,
                  valueChanged: (DeckType newDeckType) => setState(() {
                        _isDeckChanged = true;
                        _viewModel.deck.type = newDeckType;
                      }),
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: <Widget>[
                Text(
                  AppLocalizations.of(context).markdown,
                  style: AppStyles.secondaryText,
                ),
                Switch(
                  value: _viewModel.deck.markdown,
                  onChanged: (newValue) {
                    setState(() {
                      _isDeckChanged = true;
                      _viewModel.deck.markdown = newValue;
                    });
                  },
                )
              ],
            ),
          ],
        ),
      );
}
