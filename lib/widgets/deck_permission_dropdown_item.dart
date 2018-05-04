import 'package:flutter/material.dart';

import '../flutter/localization.dart';

enum SharingDeckPermissionsType { write, read, owner }

class DeckPermissionDropdownItem extends StatelessWidget {
  final SharingDeckPermissionsType permission;

  DeckPermissionDropdownItem(this.permission);

  @override
  Widget build(BuildContext context) {
    String text;
    Icon icon;
    if (permission == null) {
      text = AppLocalizations.of(context).noAccess;
      icon = new Icon(Icons.clear);
    } else {
      switch (permission) {
        case SharingDeckPermissionsType.read:
          text = AppLocalizations.of(context).canEdit;
          icon = new Icon(Icons.edit);
          break;
        case SharingDeckPermissionsType.write:
          text = AppLocalizations.of(context).canView;
          icon = new Icon(Icons.remove_red_eye);
          break;
        case SharingDeckPermissionsType.owner:
          text = AppLocalizations.of(context).owner;
          icon = new Icon(Icons.person);
          break;
      }
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
