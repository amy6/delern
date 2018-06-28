import 'package:flutter/material.dart';

import '../widgets/card_display.dart';

class CardsLearning extends StatefulWidget {
  final String _deckName;

  CardsLearning(this._deckName);

  @override
  State<StatefulWidget> createState() => CardsLearningState();
}

class CardsLearningState extends State<CardsLearning> {
  bool _isBackShown = false;
  int _watchedCount = 0;

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(title: new Text(widget._deckName)),
        body: new Column(
          children: <Widget>[
            new Expanded(child: CardDisplay("test", "test2")),
            Padding(
              padding: EdgeInsets.only(top: 25.0, bottom: 20.0),
              child: new Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: _displayButtons(),
              ),
            ),
            new Row(
              children: <Widget>[
                new Text("Watched: $_watchedCount"),
              ],
            )
          ],
        ),
      );

  List<Widget> _displayButtons() {
    if (_isBackShown) {
      _watchedCount++;
      return [
        new FloatingActionButton(
            heroTag: "dontknow",
            backgroundColor: Colors.red,
            child: new Icon(Icons.clear),
            onPressed: () {
              setState(() {
                _isBackShown = false;
              });
            }),
        new FloatingActionButton(
            heroTag: "know",
            backgroundColor: Colors.green,
            child: new Icon(Icons.check),
            onPressed: () {
              setState(() {
                _isBackShown = false;
              });
            })
      ];
    } else
      return [
        new FloatingActionButton(
            backgroundColor: Colors.orange,
            heroTag: "turn",
            child: new Icon(Icons.cached),
            onPressed: () {
              setState(() {
                _isBackShown = true;
              });
            })
      ];
  }
}
