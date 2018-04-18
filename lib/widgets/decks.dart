import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../pages/cards.dart';
import '../pages/deck_settings.dart';
import '../pages/edit_cards.dart';
import '../pages/share_deck.dart';
import '../view_models/deck_view_model.dart';
import 'observing_animated_list.dart';

class DecksWidget extends StatefulWidget {
  final String uid;

  DecksWidget(this.uid);

  @override
  _DecksWidgetState createState() => new _DecksWidgetState();
}

class _DecksWidgetState extends State<DecksWidget> {
  DecksViewModel viewModel;
  bool _active = false;

  @override
  void initState() {
    viewModel = new DecksViewModel()
      ..decks.comparator = (d1, d2) => d1.key.compareTo(d2.key);
    super.initState();
  }

  @override
  void deactivate() {
    viewModel.detach();
    _active = false;
    super.deactivate();
  }

  @override
  Widget build(BuildContext context) {
    if (!_active) {
      viewModel.attachTo(widget.uid);
      _active = true;
    }
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
  final DeckViewModel model;

  DeckListItem(this.model);

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
                  builder: (context) => new CardsPage(model?.name)),
            ),
        child: new Container(
          padding: const EdgeInsets.only(
              top: 14.0, bottom: 14.0, left: 8.0, right: 8.0),
          child: new Text(
            model?.name ?? 'Loading...',
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
      child: new Text(model?.cardsToLearn?.toString() ?? 'N/A',
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
        child: new PopupMenuButton<DeckMenu>(
          onSelected: _select,
          itemBuilder: (BuildContext context) {
            return buildMenu(context).map((DeckMenu menuItem) {
              return new PopupMenuItem<DeckMenu>(
                value: menuItem,
                child: new Text(menuItem.title),
              );
            }).toList();
          },
        ),
      ),
    );
  }

  void _select(DeckMenu menuItem) {
    if (menuItem.title ==
        AppLocalizations.of(menuItem.context).editCardsDeckMenu) {
      Navigator.push(
        menuItem.context,
        new MaterialPageRoute(
            builder: (context) => new EditCardsPage(model?.name)),
      );
    }
    if (menuItem.title ==
        AppLocalizations.of(menuItem.context).settingsDeckMenu) {
      Navigator.push(
        menuItem.context,
        new MaterialPageRoute(
            builder: (context) => new DeckSettingsPage(model?.name)),
      );
    }
    if (menuItem.title == AppLocalizations.of(menuItem.context).shareDeckMenu) {
      Navigator.push(
        menuItem.context,
        new MaterialPageRoute(
            builder: (context) => new ShareDeckPage(model?.name)),
      );
    }
  }
}

class DeckMenu {
  DeckMenu({this.title, this.context});
  String title;
  BuildContext context;
}

List<DeckMenu> buildMenu(BuildContext context) {
  return <DeckMenu>[
    new DeckMenu(
        title: AppLocalizations.of(context).editCardsDeckMenu,
        context: context),
    new DeckMenu(
        title: AppLocalizations.of(context).settingsDeckMenu, context: context),
    new DeckMenu(
        title: AppLocalizations.of(context).shareDeckMenu, context: context),
  ];
}
