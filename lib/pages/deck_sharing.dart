import 'package:flutter/material.dart';

import '../flutter/localization.dart';
import '../models/deck_access.dart';
import '../widgets/deck_access_dropdown.dart';

class DeckSharingPage extends StatefulWidget {
  final String _deckName;

  DeckSharingPage(this._deckName);

  @override
  State<StatefulWidget> createState() => new _DeckSharingState();
}

class _DeckSharingState extends State<DeckSharingPage> {
  final TextEditingController _textController = new TextEditingController();
  AccessType _accessValue = AccessType.write;

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(
          title: new Text(widget._deckName),
          actions: <Widget>[
            new IconButton(
                icon: new Icon(Icons.send),
                onPressed:
                    _isEmailCorrect() ? () => _shareDeck(_accessValue) : null)
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
      trailing: new DeckAccessDropdown(
        value: _accessValue,
        filter: (AccessType access) =>
            (access != AccessType.owner && access != null),
        valueChanged: (AccessType access) => setState(() {
              _accessValue = access;
            }),
      ),
    );
  }

  bool _isEmailCorrect() {
    return _textController.text.contains('@');
  }

  _shareDeck(AccessType deckAccess) {
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
  AccessType _sharedAccess = AccessType.write;

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
          trailing: new DeckAccessDropdown(
            value: _sharedAccess,
            filter: (AccessType access) => access != AccessType.owner,
            valueChanged: (AccessType access) => setState(() {
                  _sharedAccess = access;
                }),
          ),
        ),
      ],
    );
  }
}
