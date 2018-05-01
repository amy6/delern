import 'package:flutter/material.dart';

enum _SharingDeckPermissionsType {
  write,
  read,
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
                  new Text('People'),
                ],
              ),
            ),
            _sharingEmail(),
          ],
        ),
      );

  Widget _sharingEmail() {
    return new Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        new Expanded(
          child: new Padding(
            padding: const EdgeInsets.only(left: 8.0, right: 8.0),
            child: new TextField(
              controller: _textController,
              onChanged: (String text) {
                setState(() {});
              },
              decoration: new InputDecoration(hintText: "Email address"),
            ),
          ),
        ),
        new DropdownButton<_SharingDeckPermissionsType>(
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
      ],
    );
  }

  Widget _buildPermissionsDropDownItem(_SharingDeckPermissionsType permission) {
    String text;
    Icon icon;
    if (permission == _SharingDeckPermissionsType.write) {
      text = 'Can Edit';
      icon = new Icon(Icons.remove_red_eye);
    } else {
      text = 'Can View';
      icon = new Icon(Icons.edit);
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
