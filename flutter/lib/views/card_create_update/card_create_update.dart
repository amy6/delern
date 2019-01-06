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
    // TODO(ksheremet): Wrap Bloc in Stateful widget and use InheritedWidget
    // to access it. It will help to avoid "if" statements when
    // uid or locale changed, therefore helps to prevent bugs
    final uid = CurrentUserWidget.of(context).user.uid;
    final locale = AppLocalizations.of(context);
    if (_bloc?.uid != uid || _bloc?.locale != locale) {
      _bloc?.dispose();
      _bloc = CardCreateUpdateBloc(
          uid: uid,
          cardModel: widget.card,
          locale: AppLocalizations.of(context));
      _bloc.onCardAdded.listen(_onCardAdded);
      _bloc.onPop.listen((_) => Navigator.pop(context));
      _bloc.onErrorOccurred.listen(_onErrorOccurred);
      _frontTextController.text = widget.card.front;
      _backTextController.text = widget.card.back;
    }
    super.didChangeDependencies();
  }

  @override
  void dispose() {
    _frontSideFocus.dispose();
    _bloc?.dispose();
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
          StreamBuilder<bool>(
            initialData: false,
            stream: _bloc.isOperationEnabled,
            builder: (context, snapshot) => _bloc.isAddOperation
                ? IconButton(
                    tooltip: AppLocalizations.of(context).addCardTooltip,
                    icon: const Icon(Icons.check),
                    onPressed: snapshot.data ? _saveCard : null)
                : FlatButton(
                    child: Text(
                      AppLocalizations.of(context).save.toUpperCase(),
                      style: _isChanged && snapshot.data
                          ? const TextStyle(color: Colors.white)
                          : null,
                    ),
                    onPressed: _isChanged && snapshot.data ? _saveCard : null),
          )
        ],
      );

  void _onCardAdded(String userMessage) {
    UserMessages.showMessage(_scaffoldKey.currentState, userMessage);
    setState(() {
      _isChanged = false;
      _clearInputFields();
    });
  }

  // Show error message to user. Do not clean fields
  void _onErrorOccurred(message) {
    UserMessages.showMessage(_scaffoldKey.currentState, message);
  }

  void _saveCard() {
    _bloc.saveCardSink.add(null);
  }

  Widget _buildUserInput() {
    final widgetsList = <Widget>[
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
            _bloc.frontSideTextSink.add(text);
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
            _bloc.backSideTextSink.add(text);
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
          _bloc.addReversedCardSink.add(newValue);
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
    _bloc.frontSideTextSink.add('');
    _bloc.backSideTextSink.add('');
    FocusScope.of(context).requestFocus(_frontSideFocus);
  }
}
