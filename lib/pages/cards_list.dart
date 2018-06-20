import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../models/deck.dart';
import '../pages/card_create_update.dart';
import '../pages/card_preview.dart';
import '../view_models/card_list_view_model.dart';
import '../widgets/observing_grid_view.dart';

class CardsListPage extends StatefulWidget {
  final Deck _deck;

  CardsListPage(this._deck);

  @override
  _CardsListState createState() => new _CardsListState();
}

class _CardsListState extends State<CardsListPage> {
  CardListViewModel viewModel;
  bool _active = false;

  @override
  void initState() {
    viewModel = new CardListViewModel(widget._deck.key);
    super.initState();
  }

  @override
  void deactivate() {
    viewModel.deactivate();
    _active = false;
    super.deactivate();
  }

  @override
  void dispose() {
    super.dispose();
    viewModel.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!_active) {
      viewModel.activate();
      _active = true;
    }

    return new Scaffold(
      appBar: new AppBar(title: new Text(widget._deck.name)),
      body: new ObservingGrid(
        maxCrossAxisExtent: 240.0,
        items: viewModel.cards,
        itemBuilder: (item) => new CardGridItem(item, widget._deck),
        numberOfCardsLabel: AppLocalizations.of(context).numberOfCards,
      ),
      floatingActionButton: new FloatingActionButton(
        onPressed: () => Navigator.push(
            context,
            new MaterialPageRoute(
                builder: (context) =>
                    new CreateUpdateCard(widget._deck, null))),
        child: new Icon(Icons.add),
      ),
    );
  }
}

class CardGridItem extends StatelessWidget {
  final CardListItemViewModel card;
  final Deck deck;

  CardGridItem(this.card, this.deck);

  @override
  Widget build(BuildContext context) {
    return new Card(
      color: Colors.transparent,
      child: new Material(
        color: Colors.greenAccent,
        child: new InkWell(
          splashColor: Theme.of(context).splashColor,
          onTap: () => Navigator.push(
              context,
              new MaterialPageRoute(
                  builder: (context) => new CardPreview(deck, card))),
          child: new Container(
            padding: const EdgeInsets.all(5.0),
            child: new Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                new Text(
                  card.front,
                  maxLines: 3,
                  softWrap: true,
                  textAlign: TextAlign.center,
                  style: new TextStyle(
                    fontSize: 18.0,
                  ),
                ),
                new Container(
                  padding: const EdgeInsets.only(top: 10.0),
                  child: new Text(
                    card.back,
                    maxLines: 3,
                    softWrap: true,
                    textAlign: TextAlign.center,
                    style: new TextStyle(
                      fontSize: 14.0,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
