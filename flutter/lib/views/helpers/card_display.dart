import 'package:flutter/material.dart';

class CardDisplay extends StatelessWidget {
  final String front;
  final String back;
  final bool showBack;
  final Color backgroundColor;

  CardDisplay({this.front, this.back, this.showBack, this.backgroundColor});

  @override
  Widget build(BuildContext context) => Card(
        color: backgroundColor,
        margin: EdgeInsets.all(8.0),
        child: ListView(
          padding: EdgeInsets.all(20.0),
          children: _buildCardBody(),
        ),
      );

  List<Widget> _buildCardBody() {
    List<Widget> widgetList = [
      _sideText(front),
    ];

    if (showBack) {
      widgetList.add(Padding(
        padding: EdgeInsets.symmetric(vertical: 15.0),
        child: Divider(
          height: 1.0,
        ),
      ));
      widgetList.add(_sideText(back));
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
