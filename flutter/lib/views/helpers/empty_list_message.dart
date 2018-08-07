import 'package:flutter/material.dart';

class EmptyListMessage extends StatelessWidget {
  final String _displayText;

  EmptyListMessage(this._displayText);

  @override
  Widget build(BuildContext context) => Center(
        child: Text(_displayText),
      );
}
