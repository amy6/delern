import 'dart:async';

import 'package:flutter/material.dart';

Future<bool> showSaveUpdatesDialog(
    {@required context,
    @required changesQuestion,
    @required yesAnswer,
    @required noAnswer}) {
  return showDialog<bool>(
    context: context,
    // user must tap button!
    barrierDismissible: false,
    builder: (BuildContext context) {
      return new AlertDialog(
        title: new Text(changesQuestion),
        actions: <Widget>[
          new FlatButton(
              onPressed: () => Navigator.of(context).pop(false),
              child: new Text(noAnswer.toUpperCase())),
          new FlatButton(
            child: new Text(yesAnswer.toUpperCase()),
            onPressed: () => Navigator.of(context).pop(true),
          )
        ],
      );
    },
  );
}
