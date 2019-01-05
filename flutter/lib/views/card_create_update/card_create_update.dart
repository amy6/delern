import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:delern_flutter/models/card_model.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/view_models/card_create_update_bloc.dart';
import 'package:delern_flutter/views/helpers/save_updates_dialog.dart';
import 'package:delern_flutter/views/helpers/sign_in_widget.dart';
import 'package:flutter/material.dart';

class CardCreateUpdate extends StatefulWidget {
  final CardModel card;
  final DeckModel deck;

  const CardCreateUpdate({@required this.card, @required this.deck})
      : assert(card != null),
        assert(deck != null);

  @override
  State<StatefulWidget> createState() => _CardCreateUpdateState();
}

class _CardCreateUpdateState extends State<CardCreateUpdate> {
  bool _addReversedCard = false;
  bool _isChanged = false;
  final TextEditingController _frontTextController = TextEditingController();
  final TextEditingController _backTextController = TextEditingController();
  final _scaffoldKey = GlobalKey<ScaffoldState>();
  final FocusNode _frontSideFocus = FocusNode();
  CardCreateUpdateBloc _bloc;

  @override
  void didChangeDependencies() {
    final uid = CurrentUserWidget.of(context).user.uid;
    final locale = AppLocalizations.of(context);
    if (_bloc?.uid != uid || _bloc?.locale != locale) {
      _bloc?.dispose();
      _bloc = CardCreateUpdateBloc(
          uid: uid,
          cardModel: widget.card,
          locale: AppLocalizations.of(context));
      _bloc.onUserMessage.listen(_onCardAdded);
      _bloc.onPop.listen((data) {
        Navigator.pop(context);
      });
      _frontTextController.text = widget.card.front;
      _backTextController.text = widget.card.back;
    }
    super.didChangeDependencies();
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
        title: Text(widget.deck.name),
        actions: <Widget>[
          _bloc.isAddOperation
              ? IconButton(
                  tooltip: AppLocalizations.of(context).addCardTooltip,
                  icon: const Icon(Icons.check),
                  onPressed: _isCardValid() && _bloc.isOperationEnabled
                      ? _saveCard
                      : null)
              : FlatButton(
                  child: Text(
                    AppLocalizations.of(context).save.toUpperCase(),
                    style: _isChanged && _isCardValid()
                        ? const TextStyle(color: Colors.white)
                        : null,
                  ),
                  onPressed:
                      _isChanged && _isCardValid() && _bloc.isOperationEnabled
                          ? _saveCard
                          : null),
        ],
      );

  bool _isCardValid() => _addReversedCard
      ? _frontTextController.text.trim().isNotEmpty &&
          _backTextController.text.trim().isNotEmpty
      : _frontTextController.text.isNotEmpty;

  void _onCardAdded(String userMessage) {
    UserMessages.showMessage(_scaffoldKey.currentState, userMessage);
    setState(() {
      _isChanged = false;
      _clearInputFields();
    });
  }

  void _saveCard() {
    setState(() {
      _bloc.isOperationEnabled = false;
    });
    _bloc.saveCardSink.add(CreateUpdateUIState()
      ..front = _frontTextController.text.trim()
      ..back = _backTextController.text.trim()
      ..addReversed = _addReversedCard);
  }

  Widget _buildUserInput() {
    // ignore: omit_local_variable_types
    List<Widget> widgetsList = [
      // TODO(ksheremet): limit lines in TextField
      TextField(
        key: const Key('frontCardInput'),
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
        key: const Key('backCardInput'),
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
    if (_bloc.isAddOperation) {
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
