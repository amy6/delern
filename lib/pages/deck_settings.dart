import 'dart:async';

import 'package:flutter/material.dart';

import '../models/deck.dart';

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
                  // TODO(ksheremet): Show dialog
                  if (await _deleteDeck()) {
                    Navigator.of(context).pop();
                  }
                })
          ],
        ),
        body: new Center(child: new Text('Settings of deck will be here')),
      );

  Future<bool> _deleteDeck() async {
    try {
      await _deck.delete();
      // TODO(ksheremet): print user message
      return true;
    } catch (e) {
      // TODO(ksheremet): print user message
      return false;
    }
  }
}
