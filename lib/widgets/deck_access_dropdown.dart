import 'package:flutter/material.dart';
import 'package:meta/meta.dart';

import '../flutter/localization.dart';
import '../models/deck_access.dart';

typedef bool AccessTypeFilter(AccessType t);
typedef void AccessTypeCallback(AccessType t);

class DeckAccessDropdown extends StatefulWidget {
  final AccessType value;
  final AccessTypeCallback valueChanged;
  final AccessTypeFilter filter;

  DeckAccessDropdown(
      {@required this.value,
      @required this.valueChanged,
      @required this.filter})
      : assert(filter != null),
        assert(valueChanged != null);

  @override
  State<StatefulWidget> createState() => _DropdownState();
}

class _DropdownState extends State<DeckAccessDropdown> {
  @override
  Widget build(BuildContext context) {
    return new DropdownButton<AccessType>(
      // Provide default value.
      value: widget.value,
      items: (AccessType.values + [null])
          .where(widget.filter)
          .map((AccessType value) {
        return new DropdownMenuItem<AccessType>(
          child: buildDropdownItem(value),
          value: value,
        );
      }).toList(),
      onChanged: (AccessType newValue) {
        setState(() {
          widget.valueChanged(newValue);
        });
      },
    );
  }

  Widget buildDropdownItem(AccessType access) {
    String text;
    Icon icon;
    if (access == null) {
      text = AppLocalizations.of(context).noAccess;
      icon = new Icon(Icons.clear);
    } else {
      switch (access) {
        case AccessType.write:
          text = AppLocalizations.of(context).canEdit;
          icon = new Icon(Icons.edit);
          break;
        case AccessType.read:
          text = AppLocalizations.of(context).canView;
          icon = new Icon(Icons.remove_red_eye);
          break;
        case AccessType.owner:
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
