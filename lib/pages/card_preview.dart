import 'package:flutter/material.dart';

import '../models/deck.dart';
import '../pages/card_create_update.dart';
import '../view_models/card_view_model.dart';
import '../widgets/card.dart';

class PreviewCard extends StatefulWidget {
  final Deck _deck;
  final CardViewModel _cardView;

  PreviewCard(this._deck, this._cardView);

  @override
  State<StatefulWidget> createState() => new PreviewCardState();
}

class PreviewCardState extends State<PreviewCard> {
  bool _active = false;

  @override
  void deactivate() {
    widget._cardView.deactivate();
    _active = false;
    super.deactivate();
  }

  @override
  Widget build(BuildContext context) {
    if (!_active) {
      widget._cardView.activate();
      _active = true;
    }
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(widget._deck.name),
        actions: <Widget>[
          new IconButton(icon: new Icon(Icons.delete), onPressed: _deleteCard)
        ],
      ),
      body: new Padding(
        padding: const EdgeInsets.only(bottom: 100.0),
        child: new DisplayCard(widget._cardView.front, widget._cardView.back),
      ),
      floatingActionButton: new FloatingActionButton(
        child: new Icon(Icons.edit),
        onPressed: () => Navigator.push(
            context,
            new MaterialPageRoute(
                builder: (context) =>
                    new CreateUpdateCard(widget._deck, widget._cardView))),
      ),
    );
  }

  _deleteCard() {
    //TODO(ksheremet): delete card
  }
}
