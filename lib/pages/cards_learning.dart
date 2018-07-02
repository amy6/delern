import 'dart:collection';
import 'dart:async';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../widgets/card_display.dart';
import '../models/deck.dart';
import '../view_models/learning_view_model.dart';

class CardsLearning extends StatefulWidget {
  final Deck _deck;

  CardsLearning(this._deck);

  @override
  State<StatefulWidget> createState() => CardsLearningState();
}

class CardsLearningState extends State<CardsLearning> {
  bool _isBackShown = false;
  int _watchedCount = 0;
  LearningViewModel _viewModel;
  StreamSubscription<void> _updates;

  @override
  void initState() {
    _viewModel = LearningViewModel(widget._deck);
    super.initState();
  }

  @override
  void deactivate() {
    _updates?.cancel();
    _updates = null;
    super.deactivate();
  }

  @override
  Widget build(BuildContext context) {
    if (_updates == null) {
      _updates = _viewModel.updates.listen((_) => setState(() {}),
          // TODO(ksheremet): close the route?
          onDone: () => print('All cards learned!'));
    }
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(_viewModel.deck.name),
        actions: <Widget>[_buildPopupMenu()],
      ),
      body: new Column(
        children: <Widget>[
          //TODO(ksheremet): Implement showing front and back sides
          new Expanded(
              // TODO(ksheremet): show loading spinner instead of this
              child: CardDisplay(_viewModel.card?.front ?? 'Loading...',
                  _viewModel.card?.back ?? '')),
          Padding(
            padding: EdgeInsets.only(top: 25.0, bottom: 20.0),
            child: new Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: _buildButtons(),
            ),
          ),
          new Row(
            children: <Widget>[
              new Text(
                  AppLocalizations.of(context).watchedCards + '$_watchedCount'),
            ],
          )
        ],
      ),
    );
  }

  Widget _buildPopupMenu() {
    return new PopupMenuButton<_CardMenuItemType>(
      onSelected: (itemType) => _onCardMenuItemSelected(context, itemType),
      itemBuilder: (BuildContext context) {
        return _buildMenu(context)
            .entries
            .map((entry) => new PopupMenuItem<_CardMenuItemType>(
                  value: entry.key,
                  child: new Text(entry.value),
                ))
            .toList();
      },
    );
  }

  //heroTag - https://stackoverflow.com/questions/46509553/
  List<Widget> _buildButtons() {
    if (_isBackShown) {
      return [
        new FloatingActionButton(
            heroTag: "dontknow",
            backgroundColor: Colors.red,
            child: new Icon(Icons.clear),
            onPressed: () async {
              await _viewModel.answer(false);
              setState(() {
                // TODO(ksheremet): Consider to move to separate method
                _isBackShown = false;
                _watchedCount++;
              });
            }),
        new FloatingActionButton(
            heroTag: "know",
            backgroundColor: Colors.green,
            child: new Icon(Icons.check),
            onPressed: () async {
              await _viewModel.answer(true);
              setState(() {
                _isBackShown = false;
                _watchedCount++;
              });
            })
      ];
    } else
      return [
        new FloatingActionButton(
            backgroundColor: Colors.orange,
            heroTag: "turn",
            child: new Icon(Icons.cached),
            onPressed: () {
              setState(() {
                _isBackShown = true;
              });
            })
      ];
  }

  void _onCardMenuItemSelected(BuildContext context, _CardMenuItemType item) {
    switch (item) {
      case _CardMenuItemType.edit:
        // TODO(ksheremet): Show edit card page
        break;
      case _CardMenuItemType.delete:
        // TODO(ksheremet): Delete card
        break;
    }
  }
}

enum _CardMenuItemType { edit, delete }

Map<_CardMenuItemType, String> _buildMenu(BuildContext context) =>
    new LinkedHashMap<_CardMenuItemType, String>()
      ..[_CardMenuItemType.edit] = AppLocalizations.of(context).edit
      ..[_CardMenuItemType.delete] = AppLocalizations.of(context).delete;
