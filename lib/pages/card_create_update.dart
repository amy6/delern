import 'package:flutter/material.dart';

import '../models/card.dart' as model;
import '../models/deck.dart';
import '../view_models/card_view_model.dart';

class CreateUpdateCard extends StatefulWidget {
  final Deck _deck;
  final CardViewModel _cardViewModel;

  CreateUpdateCard(this._deck, this._cardViewModel);

  @override
  State<StatefulWidget> createState() => _CreateUpdateCardState();
}

class _CreateUpdateCardState extends State<CreateUpdateCard> {
  bool _addReversedCard;
  TextEditingController _frontTextController = new TextEditingController();
  TextEditingController _backTextController = new TextEditingController();

  @override
  void initState() {
    super.initState();
    if (widget._cardViewModel != null) {
      _frontTextController.text = widget._cardViewModel.front;
      _backTextController.text = widget._cardViewModel.back;
    }
  }

  @override
  Widget build(BuildContext context) {
    return new WillPopScope(
      onWillPop: () async {
        if (widget._cardViewModel != null) {
          // TODO(ksheremet): Check callback that the card was added
          var card = widget._cardViewModel.card;
          card.front = _frontTextController.text;
          card.back = _backTextController.text;
          card.save();
        }
        return true;
      },
      child: new Scaffold(
        appBar: buildAppBar(),
        body: buildBody(),
      ),
    );
  }

  Widget buildAppBar() {
    if (widget._cardViewModel == null) {
      return new AppBar(
        title: new Text(widget._deck.name),
        actions: <Widget>[
          new IconButton(
              icon: new Icon(Icons.check),
              onPressed: (_frontTextController.text.isEmpty ||
                      _backTextController.text.isEmpty)
                  ? null
                  : () {
                      setState(() {
                        // TODO(ksheremet): Check callback that the card was added
                        new model.Card(widget._deck.key,
                            front: _frontTextController.text,
                            back: _backTextController.text)
                          ..save();
                        if (_addReversedCard == true) {
                          new model.Card(widget._deck.key,
                              front: _backTextController.text,
                              back: _frontTextController.text)
                            ..save();
                        }
                        _clearFields();
                      });
                    })
        ],
      );
    } else {
      return new AppBar(
        title: new Text(widget._deck.name),
      );
    }
  }

  Widget buildBody() {
    List<Widget> builder = [
      new TextField(
        controller: _frontTextController,
        onChanged: (String text) {
          setState(() {});
        },
        decoration: new InputDecoration(hintText: 'front side'),
      ),
      new TextField(
        controller: _backTextController,
        onChanged: (String text) {
          setState(() {});
        },
        decoration: new InputDecoration(
          hintText: 'back side',
        ),
      ),
    ];

    // Add reversed card widget it it is adding new cards
    if (widget._cardViewModel == null) {
      builder.add(new Row(
        children: <Widget>[
          new Checkbox(
              value: _addReversedCard == null ? false : _addReversedCard,
              onChanged: (bool newValue) {
                setState(() {
                  _addReversedCard = newValue;
                });
              }),
          new Text('Add reversed card'),
        ],
      ));
    }

    return new Padding(
      padding: const EdgeInsets.only(left: 8.0, right: 8.0),
      child: new Column(
        children: builder,
      ),
    );
  }

  void _clearFields() {
    _frontTextController.clear();
    _backTextController.clear();
  }
}
