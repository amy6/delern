import 'dart:async';

import 'package:flutter/material.dart';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';

import '../flutter/pausable_state.dart';

class DecksWidget extends StatefulWidget {
  final FirebaseUser user;

  DecksWidget(this.user) : super();

  @override
  _DecksWidgetState createState() => new _DecksWidgetState();
}

// TODO(dotdoom): this is a model
class Deck {
  final String name;
  final String key;

  Deck(this.key, this.name);
}

class _DecksWidgetState extends PausableState<DecksWidget> {
  StreamSubscription<Event> _subscription;
  List<Deck> _decks;

  @override
  void initState() {
    super.initState();
  }

  @override
  void pauseState() {
    super.pauseState();
    _subscription.cancel();
  }

  @override
  void resumeState() {
    super.resumeState();
    _subscription = FirebaseDatabase.instance
        .reference()
        .child('decks')
        .child(widget.user.uid)
        .onValue
        .listen((event) {
      setState(() {
        var value = event.snapshot.value as Map;
        _decks = new List<Deck>();
        value.forEach((deckId, deck) {
          _decks.add(new Deck(deckId, deck['name']));
        });
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_decks == null) {
      return new Text('Loading...');
    }

    return new ListView.builder(
      itemCount: _decks.length,
      itemBuilder: (context, pos) => new DeckListItem(_decks[pos]),
    );
  }
}

class DeckListItem extends StatefulWidget {
  final Deck deck;

  DeckListItem(this.deck) : super();

  @override
  _DeckListItemState createState() => new _DeckListItemState();
}

class _DeckListItemState extends PausableState<DeckListItem> {
  @override
  Widget build(BuildContext context) {
    return new Text(widget.deck.name);
  }
}
