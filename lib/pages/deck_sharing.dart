import 'package:flutter/material.dart';

import '../flutter/localization.dart';

enum _SharingDeckPermissionsType {
  write,
  read,
}

enum _UsersDeckPermissionsType {
  write,
  read,
  no_access,
}

class DeckSharingPage extends StatefulWidget {
  final String _deckName;

  DeckSharingPage(this._deckName);

  @override
  State<StatefulWidget> createState() => new _DeckSharingState();
}

class _DeckSharingState extends State<DeckSharingPage> {
  final TextEditingController _textController = new TextEditingController();
  _SharingDeckPermissionsType _permissionsValue =
      _SharingDeckPermissionsType.write;

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(
          title: new Text(widget._deckName),
          actions: <Widget>[
            new IconButton(
                icon: new Icon(Icons.send),
                onPressed: _isEmailCorrect()
                    ? () => _shareDeck(_permissionsValue)
                    : null)
          ],
        ),
        body: new Column(
          children: <Widget>[
            new Padding(
              padding: const EdgeInsets.only(left: 8.0, top: 8.0),
              child: new Row(
                children: <Widget>[
                  new Text(AppLocalizations.of(context).peopleLabel),
                ],
              ),
            ),
            _sharingEmail(),
            new DeckUsersWidget(),
          ],
        ),
      );

  Widget _sharingEmail() {
    return new ListTile(
      title: new TextField(
        controller: _textController,
        onChanged: (String text) {
          setState(() {});
        },
        decoration: new InputDecoration(
          hintText: AppLocalizations.of(context).emailAddressHint,
        ),
      ),
      trailing: new DropdownButton<_SharingDeckPermissionsType>(
        value: _permissionsValue,
        items: _SharingDeckPermissionsType.values
            .map((_SharingDeckPermissionsType value) {
          return new DropdownMenuItem<_SharingDeckPermissionsType>(
              value: value, child: _buildPermissionsDropDownItem(value));
        }).toList(),
        onChanged: (_SharingDeckPermissionsType newValue) {
          setState(() {
            _permissionsValue = newValue;
          });
        },
      ),
    );
  }

  Widget _buildPermissionsDropDownItem(_SharingDeckPermissionsType permission) {
    String text;
    Icon icon;
    switch (permission) {
      case _SharingDeckPermissionsType.read:
        text = AppLocalizations.of(context).canEdit;
        icon = new Icon(Icons.edit);
        break;
      case _SharingDeckPermissionsType.write:
        text = AppLocalizations.of(context).canView;
        icon = new Icon(Icons.remove_red_eye);
        break;
    }
    return new Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: <Widget>[
        new Text(text),
        icon,
      ],
    );
  }

  bool _isEmailCorrect() {
    return _textController.text.contains('@');
  }

  _shareDeck(_SharingDeckPermissionsType deckAccess) {
    print("Share deck: " + deckAccess.toString() + _textController.text);
    setState(() {
      _textController.clear();
    });
  }
}

class DeckUsersWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _DeckUsersState();
}

class _DeckUsersState extends State<DeckUsersWidget> {
  _UsersDeckPermissionsType sharedPermission = _UsersDeckPermissionsType.write;

  @override
  Widget build(BuildContext context) {
    return new Column(
      children: <Widget>[
        new Padding(
          padding: const EdgeInsets.only(left: 8.0, top: 8.0),
          child: new Row(
            children: <Widget>[
              new Text('Who has access'),
            ],
          ),
        ),
        new ListTile(
          leading: new CircleAvatar(
            backgroundColor: Colors.greenAccent,
            child: new Text('Test'.substring(0, 1)),
          ),
          title: new Text('Katarina'),
          trailing: new DropdownButton<_UsersDeckPermissionsType>(
            value: sharedPermission,
            items: _UsersDeckPermissionsType.values
                .map((_UsersDeckPermissionsType value) {
              return new DropdownMenuItem<_UsersDeckPermissionsType>(
                  value: value,
                  child: _buildUsersPermissionsDropDownItem(value));
            }).toList(),
            onChanged: (_UsersDeckPermissionsType newValue) {
              setState(() {
                sharedPermission = newValue;
              });
            },
          ),
        ),
      ],
    );
  }

  Widget _buildUsersPermissionsDropDownItem(
      _UsersDeckPermissionsType permission) {
    String text;
    Icon icon;
    switch (permission) {
      case _UsersDeckPermissionsType.read:
        text = AppLocalizations.of(context).canEdit;
        icon = new Icon(Icons.edit);
        break;
      case _UsersDeckPermissionsType.write:
        text = AppLocalizations.of(context).canView;
        icon = new Icon(Icons.remove_red_eye);
        break;
      case _UsersDeckPermissionsType.no_access:
        text = 'No access';
        icon = new Icon(Icons.clear);
    }
    return new Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: <Widget>[
        new Text(text),
        icon,
      ],
    );
  }
}
