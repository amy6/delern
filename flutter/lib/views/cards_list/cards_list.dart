import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';
import '../../models/card.dart' as card_model;
import '../../models/deck.dart';
import '../../view_models/card_list_block.dart';
import '../../views/helpers/card_background.dart';
import '../../views/helpers/empty_list_message.dart';
import '../../views/helpers/helper_progress_indicator.dart';
import '../card_create_update/card_create_update.dart';
import '../card_preview/card_preview.dart';

class CardsListPage extends StatefulWidget {
  final Deck deck;
  final bool allowEdit;

  const CardsListPage({@required this.deck, @required this.allowEdit})
      : assert(deck != null),
        assert(allowEdit != null);

  @override
  _CardsListState createState() => _CardsListState();
}

class _CardsListState extends State<CardsListPage> {
  CardListViewModel _viewModel;
  StreamSubscription<void> _updates;

  void _searchTextChanged(String input) {
    if (input == null) {
      _viewModel.cards.filter = null;
      return;
    }
    input = input.toLowerCase();
    _viewModel.cards.filter = (c) =>
        c.card.front.toLowerCase().contains(input) ||
        c.card.back.toLowerCase().contains(input);
  }

  @override
  void initState() {
    _cardListBlock = CardListBlock(deckKey: widget.deck.key);
    super.initState();
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          title: Text(widget.deck.name),
        ),
        body: StreamBuilder<List<card_model.CardModel>>(
            initialData: _cardListBlock.cardValue,
            stream: _cardListBlock.cardStream,
            builder: (buildContext, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return HelperProgressIndicator();
              }
              if (snapshot.requireData.isEmpty) {
                return EmptyListMessage(
                    AppLocalizations.of(context).emptyCardsList);
              }
              return Column(
                children: <Widget>[
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: <Widget>[
                      Text(
                        AppLocalizations.of(context)
                            .numberOfCards(_cardListBlock.cardValue.length),
                        style: AppStyles.secondaryText,
                      ),
                    ],
                  ),
                  Expanded(
                    child: GridView.extent(
                      maxCrossAxisExtent: 240,
                      children: List.of(
                          _cardListBlock.cardValue.map((item) => CardGridItem(
                                card: item,
                                deck: DeckModel.copyFromLegacy(widget.deck),
                                allowEdit: widget.allowEdit,
                              ))),
                    ),
                  ),
                ],
              );
            }),
        floatingActionButton: buildAddCard(),
      );

  Builder buildAddCard() => Builder(
        builder: (context) => FloatingActionButton(
              onPressed: () {
                if (widget.allowEdit) {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          settings: const RouteSettings(name: '/cards/new'),
                          builder: (context) => CreateUpdateCard(
                                card: card_model.CardModel(
                                    deckKey: widget.deck.key),
                                deck: DeckModel.copyFromLegacy(widget.deck),
                              )));
                } else {
                  UserMessages.showMessage(
                      Scaffold.of(context),
                      AppLocalizations.of(context)
                          .noAddingWithReadAccessUserMessage);
                }
              },
              child: const Icon(Icons.add),
            ),
      );
}

class CardGridItem extends StatelessWidget {
  final card_model.CardModel card;
  final DeckModel deck;
  final bool allowEdit;

  const CardGridItem(
      {@required this.card, @required this.deck, @required this.allowEdit})
      : assert(card != null),
        assert(deck != null),
        assert(allowEdit != null);

  @override
  Widget build(BuildContext context) => Card(
        color: Colors.transparent,
        child: Material(
          color: specifyCardBackground(deck.type, card.back),
          child: InkWell(
            splashColor: Theme.of(context).splashColor,
            onTap: () => Navigator.push(
                context,
                MaterialPageRoute(
                    settings: const RouteSettings(name: '/cards/preview'),
                    builder: (context) => CardPreview(
                          card: card,
                          deck: deck,
                          allowEdit: allowEdit,
                        ))),
            child: Container(
              padding: const EdgeInsets.all(5.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  Text(
                    card.front,
                    maxLines: 3,
                    softWrap: true,
                    textAlign: TextAlign.center,
                    style: AppStyles.primaryText,
                  ),
                  Container(
                    padding: const EdgeInsets.only(top: 10.0),
                    child: Text(
                      card.back ?? '',
                      maxLines: 3,
                      softWrap: true,
                      textAlign: TextAlign.center,
                      style: AppStyles.secondaryText,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      );
}
