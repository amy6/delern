import 'dart:collection';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';
import '../../models/deck_access.dart';
import '../../view_models/deck_list_view_model.dart';
import '../cards_learning/cards_learning.dart';
import '../cards_list/cards_list.dart';
import '../deck_settings/deck_settings.dart';
import '../deck_sharing/deck_sharing.dart';
import '../helpers/empty_list_message.dart';
import '../helpers/observing_animated_list.dart';
import '../helpers/search_bar.dart';
import '../helpers/sign_in_widget.dart';
import 'create_deck.dart';
import 'navigation_drawer.dart';

class DecksListPage extends StatefulWidget {
  final String title;

  const DecksListPage({@required this.title, Key key})
      : assert(title != null),
        super(key: key);

  @override
  DecksListPageState createState() => DecksListPageState();
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

class DecksListPageState extends State<DecksListPage> {
  DeckListViewModel viewModel;
  bool _active = false;

  @override
  void didChangeDependencies() {
    // TODO(dotdoom): find out deactivate/build/didChangeDependencies flow.
    viewModel ??= DeckListViewModel(CurrentUserWidget.of(context).user.uid)
      ..decks.comparator = (d1, d2) => d1.key.compareTo(d2.key);
    super.didChangeDependencies();
  }

  @override
  void deactivate() {
    viewModel.deactivate();
    _active = false;
    super.deactivate();
  }

  void setFilter(String input) {
    if (input == null) {
      viewModel.decks.filter = null;
      return;
    }
    input = input.toLowerCase();
    viewModel.decks.filter = (d) =>
        // Case insensitive filter
        d.deck.name.toLowerCase().contains(input);
  }

  GlobalKey fabKey = GlobalKey();

  @override
  Widget build(BuildContext context) {
    if (!_active) {
      viewModel.activate();
      _active = true;
    }

    return Scaffold(
      appBar: SearchBarWidget(title: widget.title, search: setFilter),
      drawer: NavigationDrawer(),
      body: ObservingAnimatedList(
        list: viewModel.decks,
        itemBuilder: (context, item, animation, index) => SizeTransition(
              child: DeckListItem(item),
              sizeFactor: animation,
            ),
        emptyMessageBuilder: () => ArrowToFloatingActionButtonWidget(
            fabKey: fabKey,
            child:
                EmptyListMessage(AppLocalizations.of(context).emptyDecksList)),
      ),
      floatingActionButton: CreateDeck(key: fabKey),
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

  const DeckListItem(this.viewModel);

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
                _buildNumberOfCards(),
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
                        deck: viewModel.deck,
                        allowEdit:
                            // Not allow to edit or delete cards with read
                            // access. If some error occurred when retrieving
                            // DeckAccess and it is null access we still give
                            // a try to edit for a user. If user doesn't have
                            // permissions they will see "Permission denied".
                            viewModel.access?.access != AccessType.read,
                      )),
            );
            if (anyCardsShown == false) {
              UserMessages.showMessage(
                  Scaffold.of(context),
                  AppLocalizations.of(context).emptyDeckUserMessage(
                      AppLocalizations.of(context).editCardsDeckMenu));
            }
          },
          child: Container(
            padding: const EdgeInsets.only(
                top: 14.0, bottom: 14.0, left: 8.0, right: 8.0),
            child: Text(
              viewModel.deck.name,
              style: AppStyles.primaryText,
            ),
          ),
        ),
      );

  Widget _buildNumberOfCards() {
    String numberOfCards;
    if (viewModel.cardsToLearn != null &&
        viewModel.cardsToLearn > viewModel.maxNumberOfCards) {
      numberOfCards = '${viewModel.maxNumberOfCards}+';
    } else {
      numberOfCards = viewModel.cardsToLearn?.toString() ?? 'N/A';
    }
    return Container(
      child: Text(numberOfCards, style: AppStyles.primaryText),
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
    switch (item) {
      case _DeckMenuItemType.edit:
        Navigator.push(
          context,
          MaterialPageRoute(
              settings: const RouteSettings(name: '/decks/view'),
              builder: (context) => CardsListPage(
                    deck: viewModel.deck,
                    allowEdit:
                        // Not allow to edit or delete cards with read access
                        // If some error occurred and it is null access
                        // we still give a try to edit for a user. If user
                        // doesn't have permissions they will see "Permission
                        // denied".
                        viewModel.access?.access != AccessType.read,
                  )),
        );
        break;
      case _DeckMenuItemType.setting:
        Navigator.push(
          context,
          MaterialPageRoute(
              settings: const RouteSettings(name: '/decks/settings'),
              builder: (context) =>
                  DeckSettingsPage(viewModel.deck, viewModel.access)),
        );
        break;
      case _DeckMenuItemType.share:
        if (viewModel.access.access == AccessType.owner) {
          Navigator.push(
            context,
            MaterialPageRoute(
                settings: const RouteSettings(name: '/decks/share'),
                builder: (context) => DeckSharingPage(viewModel.deck)),
          );
        } else {
          UserMessages.showMessage(Scaffold.of(context),
              AppLocalizations.of(context).noSharingAccessUserMessage);
        }
        break;
    }
  }
}

enum _DeckMenuItemType { edit, setting, share }

Map<_DeckMenuItemType, String> _buildMenu(BuildContext context) {
  // We want this Map to be ordered.
  // ignore: prefer_collection_literals
  var deckMenu = LinkedHashMap<_DeckMenuItemType, String>()
    ..[_DeckMenuItemType.edit] = AppLocalizations.of(context).editCardsDeckMenu
    ..[_DeckMenuItemType.setting] =
        AppLocalizations.of(context).settingsDeckMenu;

  if (!CurrentUserWidget.of(context).user.isAnonymous) {
    deckMenu[_DeckMenuItemType.share] =
        AppLocalizations.of(context).shareDeckMenu;
  }
  return deckMenu;
}
