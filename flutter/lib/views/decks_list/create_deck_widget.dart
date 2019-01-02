import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:delern_flutter/models/card_model.dart' as card_model;
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/view_models/deck_list_view_model.dart';
import 'package:delern_flutter/views/card_create_update/card_create_update.dart';
import 'package:delern_flutter/views/helpers/sign_in_widget.dart';
import 'package:flutter/material.dart';

class CreateDeckWidget extends StatelessWidget {
  const CreateDeckWidget({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) => FloatingActionButton(
        child: const Icon(Icons.add),
        onPressed: () async {
          var newDeck = await showDialog<DeckModel>(
            context: context,
            // User must tap a button to dismiss dialog
            barrierDismissible: false,
            builder: (_) => _CreateDeckDialog(),
          );
          if (newDeck != null) {
            final currentUser = CurrentUserWidget.of(context).user;
            try {
              // TODO(dotdoom): pass DeckAccess as a second parameter, with
              // email, displayName and photoUrl filled in.
              await DeckListViewModel.createDeck(
                  newDeck, currentUser.humanFriendlyIdentifier);
            } catch (e, stackTrace) {
              UserMessages.showError(() => Scaffold.of(context), e, stackTrace);
              return;
            }
            Navigator.push(
                context,
                MaterialPageRoute(
                    settings: const RouteSettings(name: '/cards/new'),
                    builder: (context) => CardCreateUpdate(
                        card: card_model.CardModel(deckKey: newDeck.key),
                        deck: newDeck)));
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
  Widget build(BuildContext context) {
    final addDeckButton = FlatButton(
        child: Text(
          AppLocalizations.of(context).add.toUpperCase(),
          style: TextStyle(
              color: _textController.text.isEmpty
                  ? Theme.of(context).disabledColor
                  : Theme.of(context).accentColor),
        ),
        onPressed: _textController.text.isEmpty
            ? null
            : () {
                Navigator.of(context).pop(
                    DeckModel(uid: CurrentUserWidget.of(context).user.uid)
                      ..name = _textController.text);
              });

    final cancelButton = FlatButton(
        onPressed: () {
          Navigator.of(context).pop(null);
        },
        child: Text(
          MaterialLocalizations.of(context).cancelButtonLabel.toUpperCase(),
          style: TextStyle(color: Theme.of(context).accentColor),
        ));

    final deckNameTextField = TextField(
      autofocus: true,
      controller: _textController,
      onChanged: (text) {
        setState(() {});
      },
      style: AppStyles.primaryText,
    );

    if (MediaQuery.of(context).orientation == Orientation.portrait) {
      return AlertDialog(
        title: Text(
          AppLocalizations.of(context).deck,
          style: const TextStyle(fontWeight: FontWeight.w600),
        ),
        content: SingleChildScrollView(
          child: deckNameTextField,
        ),
        actions: <Widget>[
          cancelButton,
          addDeckButton,
        ],
      );
    } else {
      return _HorizontalDialog(
        child: SingleChildScrollView(
          child: Container(
              width: MediaQuery.of(context).size.width,
              child: Padding(
                padding: EdgeInsets.symmetric(
                    horizontal: MediaQuery.of(context).size.width * 0.05),
                child: Row(
                  children: <Widget>[
                    Flexible(child: deckNameTextField),
                    Column(
                      children: <Widget>[
                        addDeckButton,
                        cancelButton,
                      ],
                    )
                  ],
                ),
              )),
        ),
      );
    }
  }
}

class _HorizontalDialog extends StatelessWidget {
  const _HorizontalDialog({
    Key key,
    this.child,
  }) : super(key: key);

  final Widget child;

  @override
  Widget build(BuildContext context) {
    // The duration of the animation to show when the system keyboard intrudes
    // into the space that the dialog is placed in.
    const insetAnimationDuration = Duration(milliseconds: 100);

    // The curve to use for the animation shown when the system
    // keyboard intrudes
    // into the space that the dialog is placed in.
    const insetAnimationCurve = Curves.decelerate;

    const dialogShape = RoundedRectangleBorder(
        borderRadius: BorderRadius.all(Radius.circular(2.0)));

    return AnimatedPadding(
      padding: MediaQuery.of(context).viewInsets,
      duration: insetAnimationDuration,
      curve: insetAnimationCurve,
      child: Center(
        child: Material(
          elevation: 24.0,
          color: Theme.of(context).dialogBackgroundColor,
          type: MaterialType.card,
          child: child,
          shape: dialogShape,
        ),
      ),
    );
  }
}
