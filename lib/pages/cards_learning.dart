import 'dart:async';
import 'dart:collection';

import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../flutter/show_error.dart';
import '../models/deck.dart';
import '../view_models/learning_view_model.dart';
import '../widgets/card_display.dart';

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
          new Expanded(
              // TODO(ksheremet): show loading spinner instead of this
              child: CardDisplay(_viewModel.card?.front ?? 'Loading...',
                  _viewModel.card?.back ?? '', _isBackShown)),
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
        // TODO(ksheremet): Make buttons disabled when card was answered and is saving to DB
        new FloatingActionButton(
            heroTag: "dontknow",
            backgroundColor: Colors.red,
            child: new Icon(Icons.clear),
            onPressed: () {
              setState(() async {
                await _answerCard(false);
              });
            }),
        new FloatingActionButton(
            heroTag: "know",
            backgroundColor: Colors.green,
            child: new Icon(Icons.check),
            onPressed: () {
              setState(() async {
                await _answerCard(true);
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

  Future<void> _answerCard(bool answer) async {
    try {
      await _viewModel.answer(answer);
      _isBackShown = false;
      _watchedCount++;
    } catch (e, stacktrace) {
      // TODO(ksheremet): Figure out why it doesn't show Snackbar
      showError(Scaffold.of(context), e, stacktrace);
    }
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
