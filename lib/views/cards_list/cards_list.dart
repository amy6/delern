import 'dart:async';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../models/card.dart' as cardModel;
import '../../models/deck.dart';
import '../../view_models/card_list_view_model.dart';
import '../card_create_update/card_create_update.dart';
import '../card_preview/card_preview.dart';
import '../helpers/search_bar.dart';
import 'observing_grid_view.dart';

class CardsListPage extends StatefulWidget {
  final Deck _deck;

  CardsListPage(this._deck);

  @override
  _CardsListState createState() => _CardsListState();
}

class _CardsListState extends State<CardsListPage> {
  bool _active = false;
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
    _viewModel = CardListViewModel(widget._deck);
    super.initState();
  }

  @override
  void deactivate() {
    _viewModel.deactivate();
    _active = false;

    _updates?.cancel();
    _updates = null;

    super.deactivate();
  }

  @override
  void dispose() {
    super.dispose();
    _viewModel.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!_active) {
      _viewModel.activate();
      _active = true;
    }
    if (_updates == null) {
      _updates = _viewModel.updates.listen((_) => setState(() {}));
    }

    return Scaffold(
      appBar: SearchBarWidget(
          title: _viewModel.deck.name, search: _searchTextChanged),
      body: ObservingGrid(
        maxCrossAxisExtent: 240.0,
        items: _viewModel.cards,
        itemBuilder: (item) => CardGridItem(item, _viewModel.deck),
        numberOfCardsLabel: AppLocalizations.of(context).numberOfCards,
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => Navigator.push(
            context,
            MaterialPageRoute(
                builder: (context) =>
                    CreateUpdateCard(cardModel.Card(deck: _viewModel.deck)))),
        child: Icon(Icons.add),
      ),
    );
  }
}

class CardGridItem extends StatelessWidget {
  final CardListItemViewModel viewModel;
  final Deck deck;

  CardGridItem(this.viewModel, this.deck);

  @override
  Widget build(BuildContext context) {
    return Card(
      color: Colors.transparent,
      child: Material(
        color: Colors.greenAccent,
        child: InkWell(
          splashColor: Theme.of(context).splashColor,
          onTap: () => Navigator.push(
              context,
              MaterialPageRoute(
                  builder: (context) => CardPreview(viewModel.card))),
          child: Container(
            padding: EdgeInsets.all(5.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Text(
                  viewModel.card.front,
                  maxLines: 3,
                  softWrap: true,
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 18.0,
                  ),
                ),
                Container(
                  padding: EdgeInsets.only(top: 10.0),
                  child: Text(
                    viewModel.card.back ?? '',
                    maxLines: 3,
                    softWrap: true,
                    textAlign: TextAlign.center,
                    style: TextStyle(
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
