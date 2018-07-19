import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../widgets/create_deck.dart';
import '../widgets/decks.dart';
import '../widgets/navigation_drawer.dart';

class DecksListPage extends StatefulWidget {
  final FirebaseUser user;
  final String title;

  DecksListPage({this.user, this.title});

  @override
  State<StatefulWidget> createState() => _DecksListState();
}

class _DecksListState extends State<DecksListPage> {
  Widget _appBarTitle;
  Icon _actionIcon;
  TextEditingController _searchController = new TextEditingController();
  String _searchText = '';

  _searchTextChanged() {
    setState(() {
      _searchText = _searchController.text;
    });
  }

  @override
  void initState() {
    _appBarTitle = Text(widget.title);
    _actionIcon = Icon(Icons.search);
    _searchController.addListener(_searchTextChanged);
    super.initState();
  }

  @override
  void dispose() {
    _searchController.removeListener(_searchTextChanged);
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBarWithSearch(),
      drawer: NavigationDrawer(widget.user),
      body: DecksWidget(uid: widget.user.uid, searchText: _searchText),
      floatingActionButton: CreateDeck(widget.user),
    );
  }

  Widget _buildAppBarWithSearch() {
    return AppBar(
      title: _appBarTitle,
      actions: <Widget>[
        IconButton(
          icon: _actionIcon,
          onPressed: () {
            setState(() {
              if (_actionIcon.icon == Icons.search) {
                _actionIcon = Icon(Icons.close);
                _appBarTitle = TextField(
                  controller: _searchController,
                  style: TextStyle(color: Colors.white, fontSize: 16.0),
                  decoration: InputDecoration(
                      prefixIcon: Icon(Icons.search, color: Colors.white),
                      hintText: AppLocalizations.of(context).searchHint,
                      hintStyle: TextStyle(color: Colors.white)),
                );
              } else {
                _searchController.clear();
                _actionIcon = Icon(Icons.search);
                _appBarTitle = Text(widget.title);
              }
            });
          },
        )
      ],
    );
  }
}
