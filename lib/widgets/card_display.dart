import 'package:flutter/material.dart';

class CardDisplay extends StatelessWidget {
  final String _front;
  final String _back;

  CardDisplay(this._front, this._back);

  @override
  Widget build(BuildContext context) {
    return new Card(
      color: Colors.greenAccent,
      margin: const EdgeInsets.all(8.0),
      child: new ListView(
        padding: const EdgeInsets.all(20.0),
        children: <Widget>[
          sideText(_front),
          new Padding(
            padding: const EdgeInsets.symmetric(vertical: 15.0),
            child: Divider(
              height: 1.0,
            ),
          ),
          sideText(_back),
        ],
      ),
    );
  }

  Widget sideText(String text) {
    return new Text(
      text,
      textAlign: TextAlign.center,
      style: new TextStyle(
        fontSize: 18.0,
      ),
    );
  }
}
