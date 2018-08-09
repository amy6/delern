import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';

typedef void SearchCallback(String input);

class SearchBarWidget extends StatefulWidget implements PreferredSizeWidget {
  final String title;
  final SearchCallback search;

  SearchBarWidget({this.title, this.search});

  @override
  State<StatefulWidget> createState() => SearchBarWidgetState();

  // Flutter documentation for AppBar.preferredSize says:
  // "the sum of kToolbarHeight and the bottom widget's preferred height".
  @override
  Size get preferredSize => Size.fromHeight(kToolbarHeight);
}

class SearchBarWidgetState extends State<SearchBarWidget> {
  TextEditingController _searchController = TextEditingController();
  bool _isSearchMode = false;

  _searchTextChanged() {
    if (_isSearchMode) {
      widget.search(_searchController.text);
    } else {
      widget.search(null);
    }
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
    Widget appBarTitle;
    Icon actionIcon;

    if (_isSearchMode) {
      actionIcon = Icon(Icons.close);
      appBarTitle = TextField(
        autofocus: true,
        controller: _searchController,
        style: AppStyles.searchBarText,
        decoration: InputDecoration(
            border: InputBorder.none,
            hintText: AppLocalizations.of(context).searchHint,
            hintStyle: AppStyles.searchBarText),
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
              if (_isSearchMode) {
                // TODO(ksheremet): would the user always want to clear it?
                _searchController.clear();
                _isSearchMode = false;
              } else {
                _isSearchMode = true;
              }
            });
          },
        )
      ],
    );
  }
}
