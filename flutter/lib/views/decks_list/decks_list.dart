import 'dart:collection';
import 'dart:math';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:delern_flutter/models/card_model.dart';
import 'package:delern_flutter/models/deck_access_model.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/view_models/decks_list_bloc.dart';
import 'package:delern_flutter/views/card_create_update/card_create_update.dart';
import 'package:delern_flutter/views/cards_learning/cards_learning.dart';
import 'package:delern_flutter/views/cards_list/cards_list.dart';
import 'package:delern_flutter/views/deck_settings/deck_settings.dart';
import 'package:delern_flutter/views/deck_sharing/deck_sharing.dart';
import 'package:delern_flutter/views/decks_list/create_deck_widget.dart';
import 'package:delern_flutter/views/decks_list/navigation_drawer.dart';
import 'package:delern_flutter/views/helpers/empty_list_message_widget.dart';
import 'package:delern_flutter/views/helpers/observing_animated_list_widget.dart';
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

enum ArrowAttachPosition {
  right,
  left,
  bottom,
  top,
}

typedef BoundariesGetter = Rect Function();

class _ArrowToFloatingActionButton extends CustomPainter {
  static const _marginFraction = 0.1;

  final BoundariesGetter sourceRect;
  final BoundariesGetter destinationRect;
  final ArrowAttachPosition sourceAttach;
  final ArrowAttachPosition destinationAttach;

  _ArrowToFloatingActionButton({
    @required this.destinationRect,
    @required this.destinationAttach,
    this.sourceRect,
    this.sourceAttach,
  })  : assert(destinationRect != null),
        assert(destinationAttach != null),
        assert((sourceRect == null) == (sourceAttach == null));

  static Offset _rectAttachPoint(
      Rect rect, ArrowAttachPosition attach, Size bounds) {
    final marginX =
        min(rect.width * _marginFraction, bounds.width / 2 * _marginFraction);
    final marginY =
        min(rect.height * _marginFraction, bounds.height / 2 * _marginFraction);
    switch (attach) {
      case ArrowAttachPosition.right:
        return rect.centerRight.translate(marginX, 0.0);
      case ArrowAttachPosition.left:
        return rect.centerLeft.translate(-marginX, 0.0);
      case ArrowAttachPosition.bottom:
        return rect.bottomCenter.translate(0.0, marginY);
      case ArrowAttachPosition.top:
        return rect.topCenter.translate(0.0, -marginY);
    }
    return null;
  }

  @override
  void paint(Canvas canvas, Size size) {
    var startPoint = size.center(Offset.zero);
    if (sourceRect != null) {
      startPoint = _rectAttachPoint(sourceRect(), sourceAttach, size);
    }

    final endPoint =
        _rectAttachPoint(destinationRect(), destinationAttach, size);

    final y1 = (startPoint.dy * 3 + endPoint.dy * 2) / 5;
    final y2 = (startPoint.dy * 2 + endPoint.dy * 3) / 5;

    var actualSourceAttach = sourceAttach;
    if (actualSourceAttach == null) {
      if (destinationAttach == ArrowAttachPosition.right) {
        actualSourceAttach = ArrowAttachPosition.left;
      } else {
        actualSourceAttach = ArrowAttachPosition.right;
      }
    }

    final x1 =
        actualSourceAttach == ArrowAttachPosition.left ? 0.0 : size.width;
    final x2 = size.width - x1;

    final curve = Path()..moveTo(startPoint.dx, startPoint.dy);
    if (sourceAttach == destinationAttach) {
      curve.quadraticBezierTo(x1, y1, endPoint.dx, endPoint.dy);
    } else {
      curve.cubicTo(x1, y1, x2, y2, endPoint.dx, endPoint.dy);
    }

    canvas.drawPath(
        curve,
        Paint()
          ..style = PaintingStyle.stroke
          ..strokeWidth = 2.0
          ..strokeCap = StrokeCap.round);
  }

  @override
  bool shouldRepaint(_ArrowToFloatingActionButton oldDelegate) =>
      destinationAttach != oldDelegate.destinationAttach ||
      destinationRect() != oldDelegate.destinationRect() ||
      sourceAttach != oldDelegate.sourceAttach ||
      ((sourceRect == null) != (oldDelegate.sourceRect == null)) ||
      (sourceRect != null && sourceRect() != oldDelegate.sourceRect());
}

class ArrowToFloatingActionButtonWidget extends StatelessWidget {
  final Widget child;
  final ArrowAttachPosition childAttach;
  final GlobalKey childKey;

  final ArrowAttachPosition fabAttach;
  final GlobalKey fabKey;

  const ArrowToFloatingActionButtonWidget(
      {@required this.fabKey,
      this.fabAttach = ArrowAttachPosition.left,
      this.child,
      this.childKey,
      this.childAttach});

  Rect _getBoundsInContext(
      {@required BuildContext boundsOf, @required BuildContext localContext}) {
    RenderBox target = boundsOf.findRenderObject();
    RenderBox local = localContext.findRenderObject();
    return local.globalToLocal(target.localToGlobal(Offset.zero)) & target.size;
  }

  @override
  Widget build(BuildContext context) {
    BoundariesGetter sourceRect;
    var sourceAttach = childAttach;
    if (childKey != null) {
      sourceRect = () => _getBoundsInContext(
          boundsOf: childKey.currentContext, localContext: context);
      if (childAttach == null) {
        sourceAttach = Directionality.of(context) == TextDirection.ltr
            ? ArrowAttachPosition.right
            : ArrowAttachPosition.left;
      }
    }

    return Container(
        child: CustomPaint(
            painter: _ArrowToFloatingActionButton(
              destinationAttach: fabAttach,
              destinationRect: () => _getBoundsInContext(
                  boundsOf: fabKey.currentContext, localContext: context),
              sourceAttach: sourceAttach,
              sourceRect: sourceRect,
            ),
            child: child));
  }
}

class DecksListState extends State<DecksList> {
  DecksListBloc _bloc;

  @override
  void didChangeDependencies() {
    final uid = CurrentUserWidget.of(context).user.uid;
    if (_bloc?.uid != uid) {
      _bloc?.dispose();
      _bloc = DecksListBloc(uid: uid);
    }
    super.didChangeDependencies();
  }

  @override
  void dispose() {
    _bloc?.dispose();
    super.dispose();
  }

  void setFilter(String input) {
    if (input == null) {
      _bloc.decksListFilter = null;
      return;
    }
    input = input.toLowerCase();
    _bloc.decksListFilter = (d) =>
        // Case insensitive filter
        d.name.toLowerCase().contains(input);
  }

  final GlobalKey _fabKey = GlobalKey();
  final GlobalKey _addDecksKey = GlobalKey();

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: SearchBarWidget(title: widget.title, search: setFilter),
        drawer: NavigationDrawer(),
        body: Column(
          children: <Widget>[
            Expanded(
                child: ObservingAnimatedListWidget(
                    list: _bloc.decksList,
                    itemBuilder: (context, item, animation, index) =>
                        SizeTransition(
                          child: DeckListItemWidget(item, _bloc),
                          sizeFactor: animation,
                        ),
                    emptyMessageBuilder: () =>
                        ArrowToFloatingActionButtonWidget(
                            fabKey: _fabKey,
                            // fabAttach: ArrowAttachPosition.left,
                            childKey: _addDecksKey,
                            // childAttach: ArrowAttachPosition.right,
                            child: EmptyListMessageWidget(
                                AppLocalizations.of(context).emptyDecksList,
                                textKey: _addDecksKey)))),
            const Padding(
              padding: EdgeInsets.only(bottom: 60),
            ),
          ],
        ),
        floatingActionButton: CreateDeckWidget(key: _fabKey),
      );
}

class DeckListItemWidget extends StatelessWidget {
  final DeckModel deck;
  final DecksListBloc bloc;

  const DeckListItemWidget(this.deck, this.bloc);

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
                  // TODO(dotdoom): pass scheduled cards list to CardsLearning.
                  builder: (context) => CardsLearning(deck: deck),
                ));
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

  Widget _buildNumberOfCards(BuildContext context) => StreamBuilder<int>(
        key: Key(deck.key),
        initialData: bloc.numberOfCardsDue(deck.key).value,
        stream: bloc.numberOfCardsDue(deck.key).stream,
        builder: (context, snapshot) => Container(
              child: Text(snapshot.data?.toString() ?? 'N/A',
                  style: AppStyles.primaryText),
            ),
      );

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
