import 'dart:async';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../models/card.dart' as cardModel;
import '../../models/deck.dart';
import '../../view_models/card_list_view_model.dart';
import '../card_create_update/card_create_update.dart';
import '../card_preview/card_preview.dart';
import 'observing_grid_view.dart';

class CardsListPage extends StatefulWidget {
  final Deck _deck;

  CardsListPage(this._deck);

  @override
  _CardsListState createState() => new _CardsListState();
}

class _CardsListState extends State<CardsListPage> {
  bool _active = false;
  CardListViewModel _viewModel;
  StreamSubscription<void> _updates;
  TextEditingController _searchController = new TextEditingController();

  _searchTextChanged() {
    _viewModel.cards.filter = (c) =>
        c.card.front
            .toLowerCase()
            .contains(_searchController.text.toLowerCase()) ||
        c.card.back
            .toLowerCase()
            .contains(_searchController.text.toLowerCase());
  }

  @override
  void initState() {
    _viewModel = CardListViewModel(widget._deck);
    _searchController.addListener(_searchTextChanged);
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
    _searchController.removeListener(_searchTextChanged);
    _searchController.dispose();
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

    return new Scaffold(
      appBar: _buildAppBarWithSearch(),
      body: new ObservingGrid(
        maxCrossAxisExtent: 240.0,
        items: _viewModel.cards,
        itemBuilder: (item) => new CardGridItem(item, _viewModel.deck),
        numberOfCardsLabel: AppLocalizations.of(context).numberOfCards,
      ),
      floatingActionButton: new FloatingActionButton(
        onPressed: () => Navigator.push(
            context,
            new MaterialPageRoute(
                builder: (context) => new CreateUpdateCard(
                    cardModel.Card(deck: _viewModel.deck)))),
        child: new Icon(Icons.add),
      ),
    );
  }

  Widget _buildAppBarWithSearch() {
    Widget appBarTitle;
    Icon actionIcon;

    if (_viewModel.cards.filter != null) {
      actionIcon = Icon(Icons.close);
      appBarTitle = TextField(
        controller: _searchController,
        style: TextStyle(color: Colors.white, fontSize: 19.0),
        decoration: InputDecoration(
            border: InputBorder.none,
            hintText: AppLocalizations.of(context).searchHint,
            hintStyle: TextStyle(color: Colors.white)),
      );
    } else {
      appBarTitle = Text(_viewModel.deck.name);
      actionIcon = Icon(Icons.search);
    }

    return AppBar(
      title: appBarTitle,
      actions: <Widget>[
        IconButton(
          icon: actionIcon,
          onPressed: () {
            setState(() {
              //TODO(ksheremet): Show keyboard when user press on search
              if (actionIcon.icon == Icons.search) {
                _searchTextChanged();
              } else {
                _searchController.clear();
                _viewModel.cards.filter = null;
              }
            });
          },
        )
      ],
    );
  }
}

class CardGridItem extends StatelessWidget {
  final CardListItemViewModel viewModel;
  final Deck deck;

  CardGridItem(this.viewModel, this.deck);

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
                  builder: (context) => new CardPreview(viewModel.card))),
          child: new Container(
            padding: const EdgeInsets.all(5.0),
            child: new Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                new Text(
                  viewModel.card.front,
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
                    viewModel.card.back ?? '',
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
