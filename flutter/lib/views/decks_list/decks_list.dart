import 'dart:collection';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:delern_flutter/models/card_model.dart';
import 'package:delern_flutter/models/deck_access_model.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/view_models/deck_list_view_model.dart';
import 'package:delern_flutter/views/card_create_update/card_create_update.dart';
import 'package:delern_flutter/views/cards_learning/cards_learning.dart';
import 'package:delern_flutter/views/cards_list/cards_list.dart';
import 'package:delern_flutter/views/deck_settings/deck_settings.dart';
import 'package:delern_flutter/views/deck_sharing/deck_sharing.dart';
import 'package:delern_flutter/views/decks_list/create_deck_widget.dart';
import 'package:delern_flutter/views/decks_list/navigation_drawer.dart';
import 'package:delern_flutter/views/helpers/empty_list_message_widget.dart';
import 'package:delern_flutter/views/helpers/observing_animated_list_widget.dart';
import 'package:delern_flutter/views/helpers/scheduled_cards_bloc_holder_widget.dart';
import 'package:delern_flutter/views/helpers/search_bar_widget.dart';
import 'package:delern_flutter/views/helpers/sign_in_widget.dart';
import 'package:flutter/material.dart';

class DecksList extends StatefulWidget {
  final String title;

  const DecksList({@required this.title, Key key})
      : assert(title != null),
        super(key: key);

  @override
  DecksListState createState() => DecksListState();
}

class _ArrowToFloatingActionButton extends CustomPainter {
  final BuildContext scaffoldContext;
  final GlobalKey fabKey;

  _ArrowToFloatingActionButton(this.scaffoldContext, this.fabKey);

  static const _margin = 20.0;

  @override
  void paint(Canvas canvas, Size size) {
    final RenderBox scaffoldBox = scaffoldContext.findRenderObject();
    final RenderBox fabBox = fabKey.currentContext.findRenderObject();
    final fabRect =
        scaffoldBox.globalToLocal(fabBox.localToGlobal(Offset.zero)) &
            fabBox.size;
    final center = size.center(Offset.zero);

    final curve = Path()
      ..moveTo(center.dx, center.dy + _margin)
      ..cubicTo(
          center.dx - _margin,
          center.dy + _margin * 2,
          _margin - center.dx,
          (fabRect.center.dy - center.dy) * 2 / 3 + center.dy,
          fabRect.centerLeft.dx - _margin,
          fabRect.center.dy)
      ..moveTo(fabRect.centerLeft.dx - _margin, fabRect.center.dy)
      ..lineTo(
          fabRect.centerLeft.dx - _margin * 2.5, fabRect.center.dy - _margin)
      ..moveTo(fabRect.centerLeft.dx - _margin, fabRect.center.dy)
      ..lineTo(fabRect.centerLeft.dx - _margin * 2.5,
          fabRect.center.dy + _margin / 2);

    canvas.drawPath(
        curve,
        Paint()
          ..style = PaintingStyle.stroke
          ..strokeWidth = 3.0
          ..strokeCap = StrokeCap.round);
  }

  @override
  bool shouldRepaint(_ArrowToFloatingActionButton oldDelegate) =>
      scaffoldContext != oldDelegate.scaffoldContext ||
      fabKey != oldDelegate.fabKey;
}

class ArrowToFloatingActionButtonWidget extends StatelessWidget {
  final Widget child;
  final GlobalKey fabKey;

  const ArrowToFloatingActionButtonWidget({@required this.fabKey, this.child});

  @override
  Widget build(BuildContext context) => Container(
      child: CustomPaint(
          painter: _ArrowToFloatingActionButton(context, fabKey),
          child: child));
}

class DecksListState extends State<DecksList> {
  DeckListViewModel viewModel;

  @override
  void didChangeDependencies() {
    viewModel ??= DeckListViewModel(CurrentUserWidget.of(context).user.uid);
    super.didChangeDependencies();
  }

  void setFilter(String input) {
    if (input == null) {
      viewModel.filter = null;
      return;
    }
    input = input.toLowerCase();
    viewModel.filter = (d) =>
        // Case insensitive filter
        d.name.toLowerCase().contains(input);
  }

  GlobalKey fabKey = GlobalKey();

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: SearchBarWidget(title: widget.title, search: setFilter),
        drawer: NavigationDrawer(),
        body: ObservingAnimatedListWidget(
          list: viewModel.list,
          itemBuilder: (context, item, animation, index) => SizeTransition(
                child: DeckListItemWidget(item),
                sizeFactor: animation,
              ),
          emptyMessageBuilder: () => ArrowToFloatingActionButtonWidget(
              fabKey: fabKey,
              child: EmptyListMessageWidget(
                  AppLocalizations.of(context).emptyDecksList)),
        ),
        floatingActionButton: CreateDeckWidget(key: fabKey),
      );
}

class DeckListItemWidget extends StatelessWidget {
  final DeckModel deck;

  const DeckListItemWidget(this.deck);

  @override
  Widget build(BuildContext context) => Column(
        children: <Widget>[
          Container(
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Expanded(
                  child: _buildDeckName(context),
                ),
                _buildNumberOfCards(context),
                _buildDeckMenu(context),
              ],
            ),
          ),
          const Divider(height: 1.0),
        ],
      );

  Widget _buildDeckName(BuildContext context) => Material(
        child: InkWell(
          splashColor: Theme.of(context).splashColor,
          onTap: () async {
            var anyCardsShown = await Navigator.push(
              context,
              MaterialPageRoute(
                  settings: const RouteSettings(name: '/decks/learn'),
                  builder: (context) => CardsLearning(
                        deck: deck,
                        allowEdit:
                            // Not allow to edit or delete cards with read
                            // access. If some error occurred when retrieving
                            // DeckAccess and it is null access we still give
                            // a try to edit for a user. If user doesn't have
                            // permissions they will see "Permission denied".
                            deck.access != AccessType.read,
                      )),
            );
            if (anyCardsShown == false) {
              // If deck is empty, open a screen with adding cards
              Navigator.push(
                  context,
                  MaterialPageRoute(
                      settings: const RouteSettings(name: '/cards/new'),
                      builder: (context) => CardCreateUpdate(
                          card: CardModel(deckKey: deck.key), deck: deck)));
            }
          },
          child: Container(
            padding: const EdgeInsets.only(
                top: 14.0, bottom: 14.0, left: 8.0, right: 8.0),
            child: Text(
              deck.name,
              style: AppStyles.primaryText,
            ),
          ),
        ),
      );

  Widget _buildNumberOfCards(BuildContext context) {
    final bloc = ScheduledCardsBlocWidget.of(context).bloc;
    return StreamBuilder<int>(
      key: Key(deck.key),
      initialData: bloc.numberOfCardsDue(deck.key).value,
      stream: bloc.numberOfCardsDue(deck.key).stream,
      builder: (context, snapshot) => Container(
            child: Text((snapshot.data ?? 0).toString(),
                style: AppStyles.primaryText),
          ),
    );
  }

  Widget _buildDeckMenu(BuildContext context) => Material(
        child: InkResponse(
          splashColor: Theme.of(context).splashColor,
          radius: 15.0,
          onTap: () {},
          child: PopupMenuButton<_DeckMenuItemType>(
            onSelected: (itemType) =>
                _onDeckMenuItemSelected(context, itemType),
            itemBuilder: (context) => _buildMenu(context)
                .entries
                .map((entry) => PopupMenuItem<_DeckMenuItemType>(
                      value: entry.key,
                      child: Text(
                        entry.value,
                        style: AppStyles.secondaryText,
                      ),
                    ))
                .toList(),
          ),
        ),
      );

  void _onDeckMenuItemSelected(BuildContext context, _DeckMenuItemType item) {
    // Not allow to add/edit or delete cards with read access
    // If some error occurred and it is null access
    // we still give a try to edit for a user. If user
    // doesn't have permissions they will see "Permission
    // denied".
    var allowEdit = deck.access != AccessType.read;
    switch (item) {
      case _DeckMenuItemType.add:
        if (allowEdit) {
          Navigator.push(
              context,
              MaterialPageRoute(
                  settings: const RouteSettings(name: '/cards/new'),
                  builder: (context) => CardCreateUpdate(
                        card: CardModel(deckKey: deck.key),
                        deck: deck,
                      )));
        } else {
          UserMessages.showMessage(Scaffold.of(context),
              AppLocalizations.of(context).noAddingWithReadAccessUserMessage);
        }
        break;
      case _DeckMenuItemType.edit:
        Navigator.push(
          context,
          MaterialPageRoute(
              settings: const RouteSettings(name: '/decks/view'),
              builder: (context) => CardsList(
                    deck: deck,
                    allowEdit: allowEdit,
                  )),
        );
        break;
      case _DeckMenuItemType.setting:
        Navigator.push(
          context,
          MaterialPageRoute(
              settings: const RouteSettings(name: '/decks/settings'),
              builder: (context) => DeckSettings(deck)),
        );
        break;
      case _DeckMenuItemType.share:
        if (deck.access == AccessType.owner) {
          Navigator.push(
            context,
            MaterialPageRoute(
                settings: const RouteSettings(name: '/decks/share'),
                builder: (context) => DeckSharing(deck)),
          );
        } else {
          UserMessages.showMessage(Scaffold.of(context),
              AppLocalizations.of(context).noSharingAccessUserMessage);
        }
        break;
    }
  }
}

enum _DeckMenuItemType { add, edit, setting, share }

Map<_DeckMenuItemType, String> _buildMenu(BuildContext context) {
  // We want this Map to be ordered.
  // ignore: prefer_collection_literals
  var deckMenu = LinkedHashMap<_DeckMenuItemType, String>()
    ..[_DeckMenuItemType.add] = AppLocalizations.of(context).addCardsDeckMenu
    ..[_DeckMenuItemType.edit] = AppLocalizations.of(context).editCardsDeckMenu
    ..[_DeckMenuItemType.setting] =
        AppLocalizations.of(context).settingsDeckMenu;

  if (!CurrentUserWidget.of(context).user.isAnonymous) {
    deckMenu[_DeckMenuItemType.share] =
        AppLocalizations.of(context).shareDeckMenu;
  }
  return deckMenu;
}
