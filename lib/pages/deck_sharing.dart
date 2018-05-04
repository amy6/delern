import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../widgets/deck_permission_dropdown_item.dart';

class DeckSharingPage extends StatefulWidget {
  final String _deckName;

  DeckSharingPage(this._deckName);

  @override
  State<StatefulWidget> createState() => new _DeckSharingState();
}

class _DeckSharingState extends State<DeckSharingPage> {
  final TextEditingController _textController = new TextEditingController();
  SharingDeckPermissionsType _permissionsValue =
      SharingDeckPermissionsType.write;

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
      trailing: new DropdownButton<SharingDeckPermissionsType>(
        value: _permissionsValue,
        items: SharingDeckPermissionsType.values
            .where(
                (permission) => permission != SharingDeckPermissionsType.owner)
            .map((SharingDeckPermissionsType value) {
          return new DropdownMenuItem<SharingDeckPermissionsType>(
            child: DeckPermissionDropdownItem(value),
            value: value,
          );
        }).toList(),
        onChanged: (SharingDeckPermissionsType newValue) {
          setState(() {
            _permissionsValue = newValue;
          });
        },
      ),
    );
  }

  bool _isEmailCorrect() {
    return _textController.text.contains('@');
  }

  _shareDeck(SharingDeckPermissionsType deckAccess) {
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
  SharingDeckPermissionsType _sharedPermission =
      SharingDeckPermissionsType.write;

  @override
  Widget build(BuildContext context) {
    return new Column(
      children: <Widget>[
        new Padding(
          padding: const EdgeInsets.only(left: 8.0, top: 8.0),
          child: new Row(
            children: <Widget>[
              new Text(AppLocalizations.of(context).whoHasAccessLabel),
            ],
          ),
        ),
        new ListTile(
          leading: new CircleAvatar(
            backgroundColor: Colors.greenAccent,
            child: new Text('Test'.substring(0, 1)),
          ),
          title: new Text('Katarina'),
          trailing: new DropdownButton<SharingDeckPermissionsType>(
            value: _sharedPermission,
            items: (SharingDeckPermissionsType.values + [null])
                .where((permission) =>
                    permission != SharingDeckPermissionsType.owner)
                .map((SharingDeckPermissionsType value) {
              return new DropdownMenuItem<SharingDeckPermissionsType>(
                  value: value, child: new DeckPermissionDropdownItem(value));
            }).toList(),
            onChanged: (SharingDeckPermissionsType newValue) {
              setState(() {
                _sharedPermission = newValue;
              });
            },
          ),
        ),
      ],
    );
  }
}
