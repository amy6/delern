import 'package:flutter/material.dart';

class DeckSharingPage extends StatelessWidget {
  final String _deckName;

  DeckSharingPage(this._deckName);

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(title: new Text(_deckName)),
        body: new Center(child: new Text('Sharing deck will be here')),
      );
}
