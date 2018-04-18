import 'package:flutter/material.dart';

class EditCardsPage extends StatelessWidget {
  final String _deckName;

  EditCardsPage(this._deckName);

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(title: new Text(_deckName)),
        body: new Center(child: new Text('Edit Cards will be here')),
      );
}
