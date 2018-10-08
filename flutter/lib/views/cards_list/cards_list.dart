import 'dart:async';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';
import '../../models/card.dart' as card_model;
import '../../models/deck.dart';
import '../../view_models/card_list_view_model.dart';
import '../../views/helpers/card_background.dart';
import '../card_create_update/card_create_update.dart';
import '../card_preview/card_preview.dart';
import '../helpers/search_bar.dart';
import 'observing_grid_view.dart';

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
    _viewModel = CardListViewModel(widget.deck)
      ..cards.comparator = (d1, d2) => d1.key.compareTo(d2.key);
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
    _updates ??= _viewModel.updates.listen((_) => setState(() {}));

    return Scaffold(
      appBar: SearchBarWidget(
          title: _viewModel.deck.name, search: _searchTextChanged),
      body: ObservingGrid(
        maxCrossAxisExtent: 240.0,
        items: _viewModel.cards,
        itemBuilder: (item) => CardGridItem(
              viewModel: item,
              deck: _viewModel.deck,
              allowEdit: widget.allowEdit,
            ),
        // TODO(ksheremet): Consider to remove this field
        emptyGridUserMessage: AppLocalizations.of(context).emptyCardsList,
      ),
      floatingActionButton: Builder(
        builder: (context) => FloatingActionButton(
              onPressed: () {
                if (widget.allowEdit) {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          settings: const RouteSettings(name: '/cards/new'),
                          builder: (context) => CreateUpdateCard(
                              card_model.Card(deck: _viewModel.deck))));
                } else {
                  UserMessages.showMessage(
                      Scaffold.of(context),
                      AppLocalizations.of(context)
                          .noAddingWithReadAccessUserMessage);
                }
              },
              child: const Icon(Icons.add),
            ),
      ),
    );
  }
}

class CardGridItem extends StatelessWidget {
  final CardListItemViewModel viewModel;
  final Deck deck;
  final bool allowEdit;

  const CardGridItem(
      {@required this.viewModel, @required this.deck, @required this.allowEdit})
      : assert(viewModel != null),
        assert(deck != null),
        assert(allowEdit != null);

  @override
  Widget build(BuildContext context) => Card(
        color: Colors.transparent,
        child: Material(
          color: specifyCardBackground(deck.type, viewModel.card.back),
          child: InkWell(
            splashColor: Theme.of(context).splashColor,
            onTap: () => Navigator.push(
                context,
                MaterialPageRoute(
                    settings: const RouteSettings(name: '/cards/preview'),
                    builder: (context) => CardPreview(
                          card: viewModel.card,
                          allowEdit: allowEdit,
                        ))),
            child: Container(
              padding: const EdgeInsets.all(5.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  Text(
                    viewModel.card.front,
                    maxLines: 3,
                    softWrap: true,
                    textAlign: TextAlign.center,
                    style: AppStyles.primaryText,
                  ),
                  Container(
                    padding: const EdgeInsets.only(top: 10.0),
                    child: Text(
                      viewModel.card.back ?? '',
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
