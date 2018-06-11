import 'package:flutter/material.dart';

class DisplayCard extends StatelessWidget {
  final String _front;
  final String _back;

  DisplayCard(this._front, this._back);

  @override
  Widget build(BuildContext context) {
    // TODO(ksheremet): Make content scrollable
    return new Card(
      color: Colors.greenAccent,
      margin: const EdgeInsets.all(8.0),
      child: new Padding(
        padding: const EdgeInsets.all(15.0),
        child: new Column(
          children: <Widget>[
            placeText(_front),
            new Padding(
              padding: const EdgeInsets.only(top: 15.0, bottom: 15.0),
              child: Divider(
                height: 1.0,
              ),
            ),
            placeText(_back),
          ],
        ),
      ),
    );
  }

  Widget placeText(String text) {
    return new Text(
      text,
      textAlign: TextAlign.center,
      style: new TextStyle(
        fontSize: 18.0,
      ),
    );
  }
}
