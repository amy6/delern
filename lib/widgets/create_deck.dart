import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';

class CreateDeck extends StatelessWidget {
  final FirebaseUser user;

  CreateDeck(this.user);

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
          builder: (_) => new CreateDeckDialog(),
        );
      },
    );
  }
}

class CreateDeckDialog extends StatefulWidget {
  @override
  _CreateDeckDialogState createState() => new _CreateDeckDialogState();
}

class _CreateDeckDialogState extends State<CreateDeckDialog> {
  final TextEditingController _textController = new TextEditingController();

  _addDeckToDb(String deckName) {
    print(deckName);
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
          onPressed: _textController.text.isNotEmpty
              ? () {
                  _addDeckToDb(_textController.text);
                  Navigator.of(context).pop();
                }
              : null,
        ),
      ],
    );
  }
}
