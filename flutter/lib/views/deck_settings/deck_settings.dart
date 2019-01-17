import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:delern_flutter/models/deck_access_model.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/view_models/deck_settings_bloc.dart';
import 'package:delern_flutter/views/deck_settings/deck_type_dropdown_widget.dart';
import 'package:delern_flutter/views/helpers/save_updates_dialog.dart';
import 'package:flutter/material.dart';

class DeckSettings extends StatefulWidget {
  final DeckModel _deck;

  const DeckSettings(this._deck);

  @override
  State<StatefulWidget> createState() => _DeckSettingsState();
}

class _DeckSettingsState extends State<DeckSettings> {
  final _scaffoldKey = GlobalKey<ScaffoldState>();
  final TextEditingController _deckNameController = TextEditingController();
  DeckSettingsBloc _bloc;
  bool _isDeckChanged = false;

  DeckSettingsModel _settingsModel;

  @override
  void didChangeDependencies() {
    // TODO(ksheremet): Wrap Bloc in Stateful widget and use InheritedWidget
    // to access it. It will help to avoid "if" statements when
    // locale changed, therefore helps to prevent bugs
    final locale = AppLocalizations.of(context);
    if (_bloc?.locale != locale) {
      _bloc?.dispose();
      _bloc = DeckSettingsBloc(deck: widget._deck, locale: locale);
      _bloc.onPop.listen((_) => Navigator.pop(context));
      _bloc.onErrorOccurred.listen(_onErrorOccurred);
      _deckNameController.text = widget._deck.name;
      _settingsModel = DeckSettingsModel()
        ..deckName = widget._deck.name
        ..deckType = widget._deck.type
        ..isMarkdown = widget._deck.markdown;
    }
    super.didChangeDependencies();
  }

  @override
  Widget build(BuildContext context) => WillPopScope(
        onWillPop: () async {
          if (_isDeckChanged) {
            try {
              // TODO(ksheremet): error handling
              _bloc.saveDeckSink.add(_settingsModel);
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
            appBar:
                AppBar(title: Text(_settingsModel.deckName), actions: <Widget>[
              IconButton(
                icon: const Icon(Icons.delete),
                onPressed: () async {
                  var locale = AppLocalizations.of(context);
                  String deleteDeckQuestion;
                  //TODO(ksheremet): Move to bloc
                  switch (widget._deck.access) {
                    case AccessType.owner:
                      deleteDeckQuestion = locale.deleteDeckOwnerAccessQuestion;
                      break;
                    case AccessType.write:
                    case AccessType.read:
                      deleteDeckQuestion =
                          locale.deleteDeckWriteReadAccessQuestion;
                      break;
                  }
                  var deleteDeckDialog = await showSaveUpdatesDialog(
                      context: context,
                      changesQuestion: deleteDeckQuestion,
                      yesAnswer: locale.delete,
                      noAnswer:
                          MaterialLocalizations.of(context).cancelButtonLabel);
                  if (deleteDeckDialog) {
                    _bloc.deleteDeckSink.add(null);
                  }
                },
              ),
            ]),
            body: _buildBody()),
      );

  Widget _buildBody() => Padding(
        padding: const EdgeInsets.all(8.0),
        child: SingleChildScrollView(
          child: Column(
            children: <Widget>[
              TextField(
                maxLines: null,
                keyboardType: TextInputType.multiline,
                controller: _deckNameController,
                style: AppStyles.primaryText,
                onChanged: (text) {
                  setState(() {
                    _isDeckChanged = true;
                    _settingsModel.deckName = text;
                  });
                },
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.start,
                children: <Widget>[
                  Padding(
                    padding: const EdgeInsets.only(top: 24.0),
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
                  DeckTypeDropdownWidget(
                    value: _settingsModel.deckType,
                    valueChanged: (newDeckType) => setState(() {
                          _isDeckChanged = true;
                          _settingsModel.deckType = newDeckType;
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
                    value: _settingsModel.isMarkdown,
                    onChanged: (newValue) {
                      setState(() {
                        _isDeckChanged = true;
                        _settingsModel.isMarkdown = newValue;
                      });
                    },
                  )
                ],
              ),
            ],
          ),
        ),
      );

  void _onErrorOccurred(String message) {
    UserMessages.showMessage(_scaffoldKey.currentState, message);
  }

  @override
  void dispose() {
    _bloc.dispose();
    super.dispose();
  }
}
