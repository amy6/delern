import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:delern_flutter/models/card_model.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/view_models/card_list_view_model.dart';
import 'package:delern_flutter/views/card_create_update/card_create_update.dart';
import 'package:delern_flutter/views/card_preview/card_preview.dart';
import 'package:delern_flutter/views/cards_list/observing_grid_widget.dart';
import 'package:delern_flutter/views/helpers/card_background_specifier.dart';
import 'package:delern_flutter/views/helpers/search_bar_widget.dart';
import 'package:flutter/material.dart';

class CardsList extends StatefulWidget {
  final DeckModel deck;
  final bool allowEdit;

  const CardsList({@required this.deck, @required this.allowEdit})
      : assert(deck != null),
        assert(allowEdit != null);

  @override
  _CardsListState createState() => _CardsListState();
}

class _CardsListState extends State<CardsList> {
  CardListViewModel _cardListViewModel;

  void _searchTextChanged(String input) {
    if (input == null) {
      _cardListViewModel.filter = null;
      return;
    }
    input = input.toLowerCase();
    _cardListViewModel.filter = (c) =>
        c.front.toLowerCase().contains(input) ||
        c.back.toLowerCase().contains(input);
  }

  @override
  void initState() {
    _cardListViewModel = CardListViewModel(deckKey: widget.deck.key);
    super.initState();
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: SearchBarWidget(
            title: widget.deck.name, search: _searchTextChanged),
        body: ObservingGridWidget(
          maxCrossAxisExtent: 240.0,
          items: _cardListViewModel.list,
          itemBuilder: (item) => CardGridItem(
                card: item,
                deck: widget.deck,
                allowEdit: widget.allowEdit,
              ),
          // TODO(ksheremet): Consider to remove this field
          emptyGridUserMessage: AppLocalizations.of(context).emptyCardsList,
        ),
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
                          builder: (context) => CardCreateUpdate(
                                card: CardModel(deckKey: widget.deck.key),
                                deck: widget.deck,
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
  final CardModel card;
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
