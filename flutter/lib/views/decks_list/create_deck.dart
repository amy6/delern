import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';
import '../../models/card.dart' as card_model;
import '../../models/deck.dart';
import '../../view_models/deck_list_view_model.dart';
import '../card_create_update/card_create_update.dart';
import '../helpers/sign_in.dart';

class CreateDeck extends StatelessWidget {
  @override
  Widget build(BuildContext context) => FloatingActionButton(
        child: const Icon(Icons.add),
        onPressed: () async {
          var newDeck = await showDialog<Deck>(
            context: context,
            // User must tap a button to dismiss dialog
            barrierDismissible: false,
            builder: (_) => _CreateDeckDialog(),
          );
          if (newDeck != null) {
            try {
              await DeckListViewModel.createDeck(newDeck);
            } catch (e, stackTrace) {
              UserMessages.showError(() => Scaffold.of(context), e, stackTrace);
              return;
            }
            Navigator.push(
                context,
                MaterialPageRoute(
                    builder: (context) =>
                        CreateUpdateCard(card_model.Card(deck: newDeck))));
          }
        },
      );
}

class _CreateDeckDialog extends StatefulWidget {
  @override
  _CreateDeckDialogState createState() => _CreateDeckDialogState();
}

class _CreateDeckDialogState extends State<_CreateDeckDialog> {
  final TextEditingController _textController = TextEditingController();

  @override
  Widget build(BuildContext context) => AlertDialog(
        title: Text(
          AppLocalizations.of(context).deck,
          style: const TextStyle(fontWeight: FontWeight.w600),
        ),
        content: SingleChildScrollView(
          child: TextField(
            autofocus: true,
            controller: _textController,
            onChanged: (text) {
              setState(() {});
            },
            style: AppStyles.primaryText,
          ),
        ),
        actions: <Widget>[
          FlatButton(
              onPressed: () {
                Navigator.of(context).pop(null);
              },
              child: Text(MaterialLocalizations.of(context)
                  .cancelButtonLabel
                  .toUpperCase())),
          FlatButton(
              child: Text(AppLocalizations.of(context).add.toUpperCase()),
              onPressed: _textController.text.isEmpty
                  ? null
                  : () {
                      Navigator.of(context).pop(Deck(
                          uid: CurrentUserWidget.of(context).user.uid,
                          name: _textController.text));
                    }),
        ],
      );
}
