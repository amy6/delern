import 'package:flutter/material.dart';

class ShareDeckPage extends StatelessWidget {
  final String _deckName;

  ShareDeckPage(this._deckName);

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(title: new Text(_deckName)),
        body: new Center(child: new Text('Sharing deck will be here')),
      );
}
