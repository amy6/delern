import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../models/deck.dart';
import '../pages/card_create_update.dart';

class CreateDeck extends StatelessWidget {
  final FirebaseUser _user;

  CreateDeck(this._user);

  @override
  Widget build(BuildContext context) {
    return new FloatingActionButton(
      child: new Icon(Icons.add),
      onPressed: () {
        showDialog<Null>(
          context: context,
          // User must tap a button to dismiss dialog
          barrierDismissible: false,
          builder: (_) => new CreateDeckButton(_user),
        );
      },
    );
  }
}

class CreateDeckButton extends StatefulWidget {
  final FirebaseUser _user;

  CreateDeckButton(this._user);

  @override
  _CreateDeckDialogState createState() => new _CreateDeckDialogState();
}

class _CreateDeckDialogState extends State<CreateDeckButton> {
  final TextEditingController _textController = new TextEditingController();

  Future<void> _addButtonPressed() async {
    var deck = new Deck(widget._user.uid, name: _textController.text);
    try {
      await deck.save();
      // Close Dialog.
      Navigator.of(context).pop();
      // Start adding cards to the deck.
      Navigator.push(
          context,
          new MaterialPageRoute(
              builder: (context) => new CreateUpdateCard(deck, null)));
    } catch (e) {
      // TODO(ksheremet): show snackbar
      Navigator.of(context).pop();
    }
  }

  @override
  Widget build(BuildContext context) {
    return new AlertDialog(
      title: new Text(
        AppLocalizations.of(context).deck,
        style: new TextStyle(fontWeight: FontWeight.w600),
      ),
      content: new SingleChildScrollView(
        child: new TextField(
          autofocus: true,
          controller: _textController,
          onChanged: (String text) {
            setState(() {});
          },
        ),
      ),
      actions: <Widget>[
        new FlatButton(
            onPressed: () {
              Navigator.of(context).pop();
            },
            child: new Text(AppLocalizations.of(context).cancel.toUpperCase())),
        new FlatButton(
            child: new Text(AppLocalizations.of(context).add.toUpperCase()),
            onPressed: _textController.text.isEmpty ? null : _addButtonPressed),
      ],
    );
  }
}
