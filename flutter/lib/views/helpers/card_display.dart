import 'package:flutter/material.dart';

class CardDisplay extends StatelessWidget {
  final String _front;
  final String _back;
  final bool _showBack;

  CardDisplay(this._front, this._back, this._showBack);

  @override
  Widget build(BuildContext context) => Card(
        color: Colors.greenAccent,
        margin: EdgeInsets.all(8.0),
        child: ListView(
          padding: EdgeInsets.all(20.0),
          children: _buildCardBody(),
        ),
      );

  List<Widget> _buildCardBody() {
    List<Widget> widgetList = [
      _sideText(_front),
    ];

    if (_showBack) {
      widgetList.add(Padding(
        padding: EdgeInsets.symmetric(vertical: 15.0),
        child: Divider(
          height: 1.0,
        ),
      ));
      widgetList.add(_sideText(_back));
    }

    return widgetList;
  }

  Widget _sideText(String text) => Text(
        text,
        textAlign: TextAlign.center,
        style: TextStyle(
          fontSize: 18.0,
        ),
      );
}
