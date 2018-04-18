import 'package:flutter/material.dart';

class DeckSettingsPage extends StatelessWidget {
  final String _deckName;

  DeckSettingsPage(this._deckName);

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(title: new Text(_deckName)),
        body: new Center(child: new Text('Settings of deck will be here')),
      );
}
