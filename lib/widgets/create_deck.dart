import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../models/deck.dart';
import '../pages/card_create_update.dart';

//TODO(ksheremet): Localization
class CreateDeck extends StatelessWidget {
  final FirebaseUser _user;

  CreateDeck(this._user);

  @override
  Widget build(BuildContext context) {
    // TODO(ksheremet): add ripple
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
        'Deck',
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
            child: new Text('Cancel'.toUpperCase())),
        new FlatButton(
            child: new Text('Add'.toUpperCase()),
            onPressed: _textController.text.isEmpty ? null : _addButtonPressed),
      ],
    );
  }
}
