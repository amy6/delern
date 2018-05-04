import 'package:flutter/material.dart';

import '../flutter/localization.dart';

enum SharingDeckPermissionsType { write, read, owner }

class DeckPermissionDropdownItem extends StatelessWidget {
  final SharingDeckPermissionsType permission;

  DeckPermissionDropdownItem(this.permission);

  // TODO(ksheremet): localization
  @override
  Widget build(BuildContext context) {
    String text;
    Icon icon;
    if (permission == null) {
      text = 'No access';
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
          text = 'owner';
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
