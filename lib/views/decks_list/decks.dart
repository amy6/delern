import 'dart:collection';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../view_models/deck_list_view_model.dart';
import '../cards_learning/cards_learning.dart';
import '../cards_list/cards_list.dart';
import '../deck_settings/deck_settings.dart';
import '../deck_sharing/deck_sharing.dart';
import '../helpers/observing_animated_list.dart';

class DecksWidget extends StatefulWidget {
  final String uid;
  final String searchText;

  DecksWidget({@required this.uid, @required this.searchText}) {
    assert(this.uid != null);
    assert(this.searchText != null);
  }

  @override
  _DecksWidgetState createState() => new _DecksWidgetState();
}

class _DecksWidgetState extends State<DecksWidget> {
  DeckListViewModel viewModel;
  bool _active = false;

  @override
  void initState() {
    viewModel = new DeckListViewModel(widget.uid)
      ..decks.comparator = (d1, d2) => d1.key.compareTo(d2.key);
    super.initState();
  }

  @override
  void deactivate() {
    viewModel.deactivate();
    _active = false;
    super.deactivate();
  }

  @override
  Widget build(BuildContext context) {
    if (!_active) {
      viewModel.activate();
      _active = true;
    }

    viewModel.decks.filter = (d) =>
        // Case insensitive filter
        d.deck.name.toLowerCase().contains(widget.searchText.toLowerCase());

    return new ObservingAnimatedList(
      list: viewModel.decks,
      itemBuilder: (context, item, animation, index) => new SizeTransition(
            child: new DeckListItem(item),
            sizeFactor: animation,
          ),
    );
  }

  @override
  void dispose() {
    super.dispose();
    viewModel.dispose();
  }
}

class DeckListItem extends StatelessWidget {
  final DeckListItemViewModel viewModel;

  DeckListItem(this.viewModel);

  @override
  Widget build(BuildContext context) {
    return new Column(
      children: <Widget>[
        new Container(
          child: new Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              new Expanded(
                child: _buildDeckName(context),
              ),
              _buildNumberOfCards(),
              _buildDeckMenu(context),
            ],
          ),
        ),
        new Divider(height: 1.0),
      ],
    );
  }

  Widget _buildDeckName(BuildContext context) {
    return new Material(
      child: new InkWell(
        splashColor: Theme.of(context).splashColor,
        onTap: () => Navigator.push(
              context,
              new MaterialPageRoute(
                  builder: (context) => new CardsLearning(viewModel.deck)),
            ),
        child: new Container(
          padding: const EdgeInsets.only(
              top: 14.0, bottom: 14.0, left: 8.0, right: 8.0),
          child: new Text(
            viewModel.deck.name,
            style: new TextStyle(
              fontSize: 18.0,
              fontWeight: FontWeight.w400,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildNumberOfCards() {
    return new Container(
      child: new Text(viewModel.cardsToLearn?.toString() ?? 'N/A',
          style: new TextStyle(
            fontSize: 18.0,
          )),
    );
  }

  Widget _buildDeckMenu(BuildContext context) {
    return new Material(
      child: new InkResponse(
        splashColor: Theme.of(context).splashColor,
        radius: 15.0,
        onTap: () {},
        child: new PopupMenuButton<_DeckMenuItemType>(
          onSelected: (itemType) => _onDeckMenuItemSelected(context, itemType),
          itemBuilder: (BuildContext context) {
            return _buildMenu(context)
                .entries
                .map((entry) => new PopupMenuItem<_DeckMenuItemType>(
                      value: entry.key,
                      child: new Text(entry.value),
                    ))
                .toList();
          },
        ),
      ),
    );
  }

  void _onDeckMenuItemSelected(BuildContext context, _DeckMenuItemType item) {
    switch (item) {
      case _DeckMenuItemType.edit:
        Navigator.push(
          context,
          new MaterialPageRoute(
              builder: (context) => new CardsListPage(viewModel.deck)),
        );
        break;
      case _DeckMenuItemType.setting:
        Navigator.push(
          context,
          new MaterialPageRoute(
              builder: (context) =>
                  new DeckSettingsPage(viewModel.deck, viewModel.access)),
        );
        break;
      case _DeckMenuItemType.share:
        Navigator.push(
          context,
          new MaterialPageRoute(
              builder: (context) => new DeckSharingPage(viewModel.deck)),
        );
        break;
    }
  }
}

enum _DeckMenuItemType { edit, setting, share }

Map<_DeckMenuItemType, String> _buildMenu(BuildContext context) =>
    new LinkedHashMap<_DeckMenuItemType, String>()
      ..[_DeckMenuItemType.edit] =
          AppLocalizations.of(context).editCardsDeckMenu
      ..[_DeckMenuItemType.setting] =
          AppLocalizations.of(context).settingsDeckMenu
      ..[_DeckMenuItemType.share] = AppLocalizations.of(context).shareDeckMenu;
