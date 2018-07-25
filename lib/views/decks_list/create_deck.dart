import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/user_messages.dart';
import '../../models/card.dart' as cardModel;
import '../../models/deck.dart';
import '../../view_models/deck_list_view_model.dart';
import '../card_create_update/card_create_update.dart';

class CreateDeck extends StatelessWidget {
  final FirebaseUser _user;

  CreateDeck(this._user);

  @override
  Widget build(BuildContext context) {
    return new FloatingActionButton(
      child: new Icon(Icons.add),
      onPressed: () async {
        Deck newDeck = await showDialog<Deck>(
          context: context,
          // User must tap a button to dismiss dialog
          barrierDismissible: false,
          builder: (_) => new _CreateDeckDialog(_user),
        );
        if (newDeck != null) {
          try {
            await DeckListViewModel.createDeck(newDeck);
          } catch (e, stackTrace) {
            UserMessages.showError(Scaffold.of(context), e, stackTrace);
            return;
          }
          Navigator.push(
              context,
              new MaterialPageRoute(
                  builder: (context) =>
                      new CreateUpdateCard(cardModel.Card(deck: newDeck))));
        }
      },
    );
  }
}

class _CreateDeckDialog extends StatefulWidget {
  final FirebaseUser _user;

  _CreateDeckDialog(this._user);

  @override
  _CreateDeckDialogState createState() => new _CreateDeckDialogState();
}

class _CreateDeckDialogState extends State<_CreateDeckDialog> {
  final TextEditingController _textController = new TextEditingController();

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
              Navigator.of(context).pop(null);
            },
            child: new Text(AppLocalizations.of(context).cancel.toUpperCase())),
        new FlatButton(
            child: new Text(AppLocalizations.of(context).add.toUpperCase()),
            onPressed: _textController.text.isEmpty
                ? null
                : () {
                    Navigator.of(context).pop(new Deck(
                        uid: widget._user.uid, name: _textController.text));
                  }),
      ],
    );
  }
}
