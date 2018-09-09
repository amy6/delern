import 'package:flutter/material.dart';

import '../../flutter/styles.dart';
import '../../views/helpers/non_scrolling_markdown.dart';

class CardDisplay extends StatelessWidget {
  final String front;
  final String back;
  final bool showBack;
  final Color backgroundColor;
  final bool isMarkdown;

  const CardDisplay(
      {@required this.front,
      @required this.back,
      @required this.showBack,
      @required this.backgroundColor,
      @required this.isMarkdown});

  @override
  Widget build(BuildContext context) => Card(
        color: backgroundColor,
        margin: EdgeInsets.all(8.0),
        child: ListView(
          padding: EdgeInsets.all(20.0),
          children: _buildCardBody(context),
        ),
      );

  List<Widget> _buildCardBody(BuildContext context) {
    var widgetList = [
      _sideText(front, context),
    ];

    if (showBack) {
      widgetList
        ..add(Padding(
          padding: EdgeInsets.symmetric(vertical: 15.0),
          child: Divider(
            height: 1.0,
          ),
        ))
        ..add(_sideText(back, context));
    }

    return widgetList;
  }

  Widget _sideText(String text, BuildContext context) {
    if (isMarkdown) {
      return buildNonScrollingMarkdown(text, context);
    }
    return Text(
      text,
      textAlign: TextAlign.center,
      style: AppStyles.primaryText,
    );
  }
}
