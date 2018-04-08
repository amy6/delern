import 'package:flutter/material.dart';

class CardsPage extends StatelessWidget {
  final String _deckName;

  CardsPage(this._deckName);

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(title: new Text(_deckName)),
        body: new Center(child: new Text('Cards will be here')),
      );
}
