import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../models/deck.dart';
import '../widgets/save_updates_dialog.dart';

class DeckSettingsPage extends StatelessWidget {
  final Deck _deck;

  DeckSettingsPage(this._deck);

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(
          title: new Text(_deck.name),
          actions: <Widget>[
            new IconButton(
                icon: new Icon(Icons.delete),
                onPressed: () async {
                  var locale = AppLocalizations.of(context);
                  var deleteDeckDialog = await showSaveUpdatesDialog(
                      context: context,
                      changesQuestion: locale.deleteDeckQuestion,
                      yesAnswer: locale.delete,
                      noAnswer: locale.cancel);
                  // TODO(ksheremet): Implement deleting deck
                  if (deleteDeckDialog) {
                    Navigator.of(context).pop();
                  }
                })
          ],
        ),
        body: new Center(child: new Text('Settings of deck will be here')),
      );
}
