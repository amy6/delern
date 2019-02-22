import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/view_models/deck_settings_bloc_2.dart';
import 'package:delern_flutter/views/base/screen_bloc_view.dart';
import 'package:delern_flutter/views/deck_settings/deck_type_dropdown_widget.dart';
import 'package:delern_flutter/views/helpers/save_updates_dialog.dart';
import 'package:flutter/material.dart';

// This view doesn't use any ui state object. For every change there is a sink.
class DeckSettings extends StatefulWidget {
  final DeckModel _deck;

  const DeckSettings(this._deck);

  @override
  State<StatefulWidget> createState() => _DeckSettingsState();
}

class _DeckSettingsState extends State<DeckSettings> {
  final TextEditingController _deckNameController = TextEditingController();
  DeckSettingsBloc _bloc;

  // I cannot get rid of this field because it is used in 2 places: app bar
  // and "rename deck" field.
  String _deckName;
  DeckType _deckType;
  bool _isMarkdown;

  @override
  void initState() {
    _deckName = widget._deck.name;
    _deckType = widget._deck.type;
    _isMarkdown = widget._deck.markdown;
    _bloc = DeckSettingsBloc(deck: widget._deck);
    _bloc.showConfirmationDialog.listen(_showDeleteDeckDialog);
    _deckNameController.text = _deckName;
    super.initState();
  }

  @override
  void didChangeDependencies() {
    // TODO(ksheremet): Locale must be somewhere in ScreenBlocView
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
  Widget build(BuildContext context) => ScreenBlocView(
        appBar: AppBar(title: Text(_deckName), actions: <Widget>[
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
                    _deckName = text;
                    _bloc.deckName.add(text);
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
                    value: _deckType,
                    valueChanged: (newDeckType) => setState(() {
                          _deckType = newDeckType;
                          _bloc.deckType.add(newDeckType);
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
                    value: _isMarkdown,
                    onChanged: (newValue) {
                      setState(() {
                        _isMarkdown = newValue;
                        _bloc.isMarkdown.add(newValue);
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
