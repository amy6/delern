import 'dart:async';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';
import '../../models/card.dart';
import '../../models/card.dart' as card_model;
import '../../view_models/card_create_update_view_model.dart';
import '../../views/helpers/sign_in_widget.dart';
import '../helpers/save_updates_dialog.dart';
import '../helpers/slow_operation_widget.dart';

class CreateUpdateCard extends StatefulWidget {
  final card_model.Card _card;

  const CreateUpdateCard(this._card);

  @override
  State<StatefulWidget> createState() => _CreateUpdateCardState();
}

class _CreateUpdateCardState extends State<CreateUpdateCard> {
  bool _addReversedCard = false;
  bool _isChanged = false;
  CardModel _cardModel;
  final TextEditingController _frontTextController = TextEditingController();
  final TextEditingController _backTextController = TextEditingController();
  final _scaffoldKey = GlobalKey<ScaffoldState>();
  final FocusNode _frontSideFocus = FocusNode();

  @override
  void initState() {
    super.initState();
    _cardModel = CardModel.copyFromLegacy(widget._card);

    if (_cardModel.key != null) {
      _frontTextController.text = _cardModel.front;
      _backTextController.text = _cardModel.back;
    }
  }

  @override
  void dispose() {
    _frontSideFocus.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) => WillPopScope(
        onWillPop: () async {
          if (_isChanged) {
            var locale = AppLocalizations.of(context);
            var continueEditingDialog = await showSaveUpdatesDialog(
                context: context,
                changesQuestion: locale.continueEditingQuestion,
                yesAnswer: locale.yes,
                noAnswer: locale.discard);
            if (continueEditingDialog) {
              return false;
            }
          }
          return true;
        },
        child: Scaffold(
          key: _scaffoldKey,
          appBar: _buildAppBar(),
          body: _buildUserInput(),
        ),
      );

  Widget _buildAppBar() => AppBar(
        // TODO(ksheremet): Consider to add name of deck in CardModel
        title: Text(widget._card.deck.name),
        actions: <Widget>[
          _cardModel.key == null
              ? SlowOperationWidget((cb) => IconButton(
                  icon: const Icon(Icons.check),
                  onPressed: _isCardValid() ? cb(_addCard) : null))
              : SlowOperationWidget((cb) => FlatButton(
                  child: Text(
                    AppLocalizations.of(context).save.toUpperCase(),
                    style: _isChanged && _isCardValid()
                        ? const TextStyle(color: Colors.white)
                        : null,
                  ),
                  onPressed: _isChanged && _isCardValid()
                      ? cb(() async {
                          if (await _saveCard()) {
                            Navigator.of(context).pop();
                          }
                        })
                      : null)),
        ],
      );

  bool _isCardValid() => _addReversedCard
      ? _frontTextController.text.trim().isNotEmpty &&
          _backTextController.text.trim().isNotEmpty
      : _frontTextController.text.trim().isNotEmpty;

  Future<void> _addCard() async {
    if (await _saveCard()) {
      UserMessages.showMessage(_scaffoldKey.currentState,
          AppLocalizations.of(context).cardAddedUserMessage);
      // Unset Card key so that we create a one.
      _cardModel.key = null;
      setState(() {
        _isChanged = false;
        _clearInputFields();
      });
    }
  }

  Future<bool> _saveCard() async {
    _cardModel
      ..front = _frontTextController.text.trim()
      ..back = _backTextController.text.trim();
    try {
      await CardCreateUpdateViewModel.saveCard(
          card: _cardModel,
          uid: CurrentUserWidget.of(context).user.uid,
          addReverse: _addReversedCard);
    } catch (e, stackTrace) {
      UserMessages.showError(() => _scaffoldKey.currentState, e, stackTrace);
      return false;
    }
    return true;
  }

  Widget _buildUserInput() {
    // ignore: omit_local_variable_types
    List<Widget> widgetsList = [
      // TODO(ksheremet): limit lines in TextField
      TextField(
        autofocus: true,
        focusNode: _frontSideFocus,
        maxLines: null,
        keyboardType: TextInputType.multiline,
        controller: _frontTextController,
        onChanged: (text) {
          setState(() {
            _isChanged = true;
          });
        },
        style: AppStyles.primaryText,
        decoration: InputDecoration(
            hintText: AppLocalizations.of(context).frontSideHint),
      ),
      TextField(
        maxLines: null,
        keyboardType: TextInputType.multiline,
        controller: _backTextController,
        onChanged: (text) {
          setState(() {
            _isChanged = true;
          });
        },
        style: AppStyles.primaryText,
        decoration: InputDecoration(
          hintText: AppLocalizations.of(context).backSideHint,
        ),
      ),
    ];

    // Add reversed card widget it it is adding cards
    if (_cardModel.key == null) {
      // https://github.com/flutter/flutter/issues/254 suggests using
      // CheckboxListTile to have a clickable checkbox label.
      widgetsList.add(CheckboxListTile(
        title: Text(
          AppLocalizations.of(context).reversedCardLabel,
          style: AppStyles.secondaryText,
        ),
        value: _addReversedCard,
        onChanged: (newValue) {
          setState(() {
            _addReversedCard = newValue;
          });
        },
        // Position checkbox before the text.
        controlAffinity: ListTileControlAffinity.leading,
      ));
    }

    return ListView(
      padding: const EdgeInsets.only(left: 8.0, right: 8.0),
      children: widgetsList,
    );
  }

  void _clearInputFields() {
    _frontTextController.clear();
    _backTextController.clear();
    FocusScope.of(context).requestFocus(_frontSideFocus);
  }
}
