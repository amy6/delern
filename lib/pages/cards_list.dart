import 'package:flutter/material.dart';

class CardsListPage extends StatelessWidget {
  final String _deckName;
  final String _key;

  CardsListPage(this._deckName, this._key);

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(title: new Text(_deckName)),
        body: new Column(
          children: <Widget>[
            new Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: <Widget>[
                new Text(
                  'Number of cards: 340',
                ),
              ],
            ),
            new Expanded(
              child: new GridView.extent(
                maxCrossAxisExtent: 240.0,
                children: <Widget>[
                  buildCard(context, 'die Mutter', 'mother'),
                  buildCard(context, 'der Vater', 'father'),
                  buildCard(context, 'uncle', 'der Onkel'),
                  buildCard(context, 'die Tante', 'aunt'),
                  buildCard(context, 'Revolution is coming...', 'bllblblblb'),
                  buildCard(context, 'Revolution, they...', 'dfdfdfdf'),
                  buildCard(
                      context, 'He\'d have you all unravel at the', 'the'),
                  buildCard(context, 'Heed not the rabble', '66666'),
                  buildCard(context, 'Sound of screams but the', 'ddddddd'),
                  buildCard(context, 'Who scream', 'rufen'),
                  buildCard(context, 'Revolution is coming...',
                      'die Revolution kommt'),
                  buildCard(
                      context,
                      'Revolution, they...hghghg ghghg hggh hghg hghg hghg hg hgh',
                      'k1212121ghg hg hg hg h hg hg hghghg hg hghg hgg h hg ghg h 212'),
                ],
              ),
            ),
          ],
        ),
        floatingActionButton: new FloatingActionButton(
          onPressed: null,
          child: new Icon(Icons.add),
        ),
      );

  Widget buildCard(BuildContext context, String frontSide, String backSide) {
    return new Card(
      color: Colors.transparent,
      child: new Material(
        color: Colors.greenAccent,
        child: new InkWell(
          splashColor: Theme.of(context).splashColor,
          onTap: () => print(frontSide),
          child: new Container(
            padding: const EdgeInsets.all(5.0),
            child: new Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                new Text(
                  frontSide,
                  maxLines: 3,
                  softWrap: true,
                  textAlign: TextAlign.center,
                  style: new TextStyle(
                    fontSize: 18.0,
                  ),
                ),
                new Container(
                  padding: const EdgeInsets.only(top: 10.0),
                  child: new Text(
                    backSide,
                    maxLines: 3,
                    softWrap: true,
                    textAlign: TextAlign.center,
                    style: new TextStyle(
                      fontSize: 14.0,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
