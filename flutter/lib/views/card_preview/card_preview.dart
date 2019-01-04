import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:delern_flutter/models/card_model.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/view_models/card_preview_bloc.dart';
import 'package:delern_flutter/views/card_create_update/card_create_update.dart';
import 'package:delern_flutter/views/helpers/card_background_specifier.dart';
import 'package:delern_flutter/views/helpers/card_display_widget.dart';
import 'package:delern_flutter/views/helpers/save_updates_dialog.dart';
import 'package:delern_flutter/views/helpers/sign_in_widget.dart';
import 'package:flutter/material.dart';

class CardPreview extends StatefulWidget {
  final CardModel card;
  final DeckModel deck;
  final bool allowEdit;

  const CardPreview(
      {@required this.card, @required this.deck, @required this.allowEdit})
      : assert(card != null),
        assert(deck != null),
        assert(allowEdit != null);

  @override
  State<StatefulWidget> createState() => _CardPreviewState();
}

class _CardPreviewState extends State<CardPreview> {
  CardPreviewBloc _cardPreviewBloc;

  @override
  void initState() {
    super.initState();
    // TODO(dotdoom): replace with a simple assignment.
    _cardPreviewBloc = CardPreviewBloc(card: widget.card, deck: widget.deck);
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          title: Text(_cardPreviewBloc.deckNameValue),
          actions: <Widget>[
            Builder(
              builder: (context) => IconButton(
                  icon: const Icon(Icons.delete),
                  onPressed: () async {
                    if (widget.allowEdit) {
                      var locale = AppLocalizations.of(context);
                      var saveChanges = await showSaveUpdatesDialog(
                          context: context,
                          changesQuestion: locale.deleteCardQuestion,
                          yesAnswer: locale.delete,
                          noAnswer: MaterialLocalizations.of(context)
                              .cancelButtonLabel);
                      if (saveChanges) {
                        _cardPreviewBloc.deleteCard
                            .add(CurrentUserWidget.of(context).user.uid);
                        // TODO(ksheremet): It would be better to close
                        //  screen in StreamBuilder when
                        //  snapshot.connectionState == Done, but StreamBuilder
                        //   requires a Widget in return statement.
                        Navigator.of(context).pop();
                      }
                    } else {
                      UserMessages.showMessage(
                          Scaffold.of(context),
                          AppLocalizations.of(context)
                              .noDeletingWithReadAccessUserMessage);
                    }
                  }),
            )
          ],
        ),
        body: Column(
          children: <Widget>[
            Expanded(
                child: StreamBuilder<CardViewModel>(
                    stream: _cardPreviewBloc.cardStream,
                    initialData: _cardPreviewBloc.cardValue,
                    builder: (context, snapshot) => CardDisplayWidget(
                        front: snapshot.requireData.card.front,
                        back: snapshot.requireData.card.back,
                        showBack: true,
                        backgroundColor: specifyCardBackground(
                            snapshot.requireData.deck.type,
                            snapshot.requireData.card.back),
                        isMarkdown: snapshot.requireData.deck.markdown))),
            const Padding(padding: EdgeInsets.only(bottom: 100.0))
          ],
        ),
        floatingActionButton: Builder(
          builder: (context) => FloatingActionButton(
              child: const Icon(Icons.edit),
              onPressed: () {
                if (widget.allowEdit) {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          // 'name' is used by Firebase Analytics to log events.
                          // TODO(dotdoom): consider better route names.
                          settings: const RouteSettings(name: '/cards/edit'),
                          builder: (context) => CardCreateUpdate(
                              card: widget.card, deck: widget.deck)));
                } else {
                  UserMessages.showMessage(
                      Scaffold.of(context),
                      AppLocalizations.of(context)
                          .noEditingWithReadAccessUserMessage);
                }
              }),
        ),
      );
}
