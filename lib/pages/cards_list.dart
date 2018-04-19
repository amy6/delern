import 'package:flutter/material.dart';

class CardsListPage extends StatelessWidget {
  final String _deckName;

  CardsListPage(this._deckName);

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(title: new Text(_deckName)),
        body: new Center(child: new Text('Edit Cards will be here')),
      );
}
