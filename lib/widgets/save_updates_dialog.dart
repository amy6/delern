import 'dart:async';

import 'package:flutter/material.dart';

class SaveUpdatesDialog {
  BuildContext _context;
  String _changesQuestion;
  String _yesAnswer;
  String _noAnswer;

  SaveUpdatesDialog(
      this._context, this._changesQuestion, this._yesAnswer, this._noAnswer);

  Future<bool> show() {
    return showDialog<bool>(
      context: _context,
      // user must tap button!
      barrierDismissible: false,
      builder: (BuildContext context) {
        return new AlertDialog(
          title: new Text(_changesQuestion),
          actions: <Widget>[
            new FlatButton(
                onPressed: () => Navigator.of(context).pop(false),
                child: new Text(_noAnswer.toUpperCase())),
            new FlatButton(
              child: new Text(_yesAnswer.toUpperCase()),
              onPressed: () => Navigator.of(context).pop(true),
            )
          ],
        );
      },
    );
  }
}
