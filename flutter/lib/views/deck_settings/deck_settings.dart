import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/view_models/deck_settings_bloc.dart';
import 'package:delern_flutter/views/base/base_view.dart';
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
  final TextEditingController _deckNameController = TextEditingController();
  DeckSettingsBloc _bloc;
  bool _isDeckChanged = false;

  DeckSettingsModel _settingsModel;

  @override
  void initState() {
    _bloc = DeckSettingsBloc(deck: widget._deck);
    _bloc.showConfirmationDialog.listen(_showDeleteDeckDialog);
    _deckNameController.text = widget._deck.name;
    _settingsModel = DeckSettingsModel()
      ..deckName = widget._deck.name
      ..deckType = widget._deck.type
      ..isMarkdown = widget._deck.markdown;
    super.initState();
  }

  @override
  void didChangeDependencies() {
    final locale = AppLocalizations.of(context);
    if (_bloc?.locale != locale) {
      _bloc.localeSink.add(locale);
    }
    super.didChangeDependencies();
  }

  void _showDeleteDeckDialog(deleteDeckQuestion) async {
    var deleteDeckDialog = await showSaveUpdatesDialog(
        context: context,
        changesQuestion: deleteDeckQuestion,
        yesAnswer: AppLocalizations.of(context).delete,
        noAnswer: MaterialLocalizations.of(context).cancelButtonLabel);
    if (deleteDeckDialog) {
      _bloc.deleteDeckSink.add(null);
    }
  }

  @override
  Widget build(BuildContext context) => BaseView(
        onWillPop: () async {
          if (_isDeckChanged) {
            _bloc.saveDeckSink.add(_settingsModel);
          }
          // TODO(ksheremet): If error occurred it won't be visible for users
          return true;
        },
        appBar: AppBar(title: Text(_settingsModel.deckName), actions: <Widget>[
          IconButton(
            icon: const Icon(Icons.delete),
            onPressed: () async {
              _bloc.deleteDeckIntentionSink.add(null);
            },
          ),
        ]),
        body: _buildBody(),
        bloc: _bloc,
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

  @override
  void dispose() {
    _bloc.dispose();
    super.dispose();
  }
}
