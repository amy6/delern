import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../widgets/create_deck.dart';
import '../widgets/decks.dart';
import '../widgets/navigation_drawer.dart';

class DecksListPage extends StatefulWidget {
  final FirebaseUser user;
  final String title;

  DecksListPage({@required this.user, @required this.title})
      : assert(user != null),
        assert(title != null);

  @override
  State<StatefulWidget> createState() => _DecksListPageState();
}

class _DecksListPageState extends State<DecksListPage> {
  TextEditingController _searchController = new TextEditingController();
  bool _isSearchMode = false;

  _searchTextChanged() {
    setState(() {});
  }

  @override
  void initState() {
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
      body:
          DecksWidget(uid: widget.user.uid, searchText: _searchController.text),
      floatingActionButton: CreateDeck(widget.user),
    );
  }

  Widget _buildAppBarWithSearch() {
    Widget appBarTitle;
    Icon actionIcon;

    if (_isSearchMode) {
      actionIcon = Icon(Icons.close);
      appBarTitle = TextField(
        controller: _searchController,
        style: TextStyle(color: Colors.white, fontSize: 19.0),
        decoration: InputDecoration(
            border: InputBorder.none,
            hintText: AppLocalizations.of(context).searchHint,
            hintStyle: TextStyle(color: Colors.white)),
      );
    } else {
      appBarTitle = Text(widget.title);
      actionIcon = Icon(Icons.search);
    }

    return AppBar(
      title: appBarTitle,
      actions: <Widget>[
        IconButton(
          icon: actionIcon,
          onPressed: () {
            setState(() {
              //TODO(ksheremet): Show keyboard when user press on search
              if (actionIcon.icon == Icons.search) {
                _isSearchMode = true;
              } else {
                _searchController.clear();
                _isSearchMode = false;
              }
            });
          },
        )
      ],
    );
  }
}
