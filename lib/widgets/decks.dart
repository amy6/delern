import 'dart:async';

import 'package:flutter/material.dart';

import '../view_models/deck_view_model.dart';
import 'vm_view.dart';

class DecksWidget extends VMViewWidget<DecksViewModel> {
  DecksWidget(Stream<DecksViewModel> s) : super(s);

  @override
  _DecksWidgetState createState() => new _DecksWidgetState();
}

class _DecksWidgetState extends VMViewState<DecksViewModel, DecksWidget> {
  @override
  Widget build(BuildContext context) {
    if (model == null) {
      return new Text('Loading...');
    }

    return new ListView.builder(
      padding: new EdgeInsets.all(8.0),
      itemCount: model.decks.length,
      itemBuilder: (context, pos) => new DeckListItem(model.decks[pos]),
    );
  }
}

class DeckListItem extends VMViewWidget<DeckViewModel> {
  DeckListItem(Stream<DeckViewModel> s) : super(s);

  @override
  _DeckListItemState createState() => new _DeckListItemState();
}

class _DeckListItemState extends VMViewState<DeckViewModel, DeckListItem> {
  @override
  Widget build(BuildContext context) {
    return new Column(
      children: <Widget>[
        new Container(
          padding: const EdgeInsets.only(top: 3.0, bottom: 3.0),
          decoration: new BoxDecoration(
            color: Theme.of(context).cardColor,
          ),
          child: new Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              new Expanded(
                child: new Container(
                  child: new Text(
                    model?.name ?? 'Loading...',
                    style: new TextStyle(
                      fontSize: 18.0,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ),
              new Container(
                child: new Text(model?.cardsToLearn?.toString() ?? 'N/A',
                    style: new TextStyle(
                      fontSize: 18.0,
                    )),
              ),
              new IconButton(icon: new Icon(Icons.more_vert), onPressed: null),
            ],
          ),
        ),
        new Divider(height: 1.0),
      ],
    );
  }
}
