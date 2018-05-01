import 'package:flutter/material.dart';

enum _SharingDeckPermissionsType {
  Edit,
  View,
}

class DeckSharingPage extends StatelessWidget {
  final String _deckName;

  DeckSharingPage(this._deckName);

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(
          title: new Text(_deckName),
          actions: <Widget>[
            new IconButton(icon: new Icon(Icons.send), onPressed: null)
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
            new SharingEmailWidget(),
          ],
        ),
      );
}

class SharingEmailWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _SharingEmailList();
}

class _SharingEmailList extends State<SharingEmailWidget> {
  _SharingDeckPermissionsType permissionsValue =
      _SharingDeckPermissionsType.Edit;

  @override
  Widget build(BuildContext context) {
    return new Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        new Expanded(
          child: new Padding(
            padding: const EdgeInsets.only(left: 8.0, right: 8.0),
            child: new TextField(
              decoration: new InputDecoration(hintText: "Email address"),
            ),
          ),
        ),
        new DropdownButton<_SharingDeckPermissionsType>(
          value: permissionsValue,
          items: _SharingDeckPermissionsType.values
              .map((_SharingDeckPermissionsType value) {
            return new DropdownMenuItem<_SharingDeckPermissionsType>(
                value: value, child: buildPermissionsDropDownItem(value));
          }).toList(),
          onChanged: (_SharingDeckPermissionsType newValue) {
            setState(() {
              permissionsValue = newValue;
            });
          },
        ),
      ],
    );
  }

  Widget buildPermissionsDropDownItem(_SharingDeckPermissionsType permission) {
    String text;
    Icon icon;
    if (permission == _SharingDeckPermissionsType.Edit) {
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
}
