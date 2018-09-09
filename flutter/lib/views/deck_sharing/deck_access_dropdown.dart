import 'package:flutter/material.dart';
import 'package:meta/meta.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../models/deck_access.dart';

typedef AccessTypeFilter = bool Function(AccessType t);
typedef AccessTypeCallback = void Function(AccessType t);

class DeckAccessDropdown extends StatefulWidget {
  final AccessType value;
  final AccessTypeCallback valueChanged;
  final AccessTypeFilter filter;

  const DeckAccessDropdown(
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
  Widget build(BuildContext context) => DropdownButton<AccessType>(
        // Provide default value.
        value: widget.value,
        items: (AccessType.values + [null])
            .where(widget.filter)
            .map((value) => DropdownMenuItem<AccessType>(
                  child: buildDropdownItem(value),
                  value: value,
                ))
            .toList(),
        onChanged: (newValue) {
          setState(() {
            widget.valueChanged(newValue);
          });
        },
      );

  Widget buildDropdownItem(AccessType access) {
    String text;
    Icon icon;
    if (access == null) {
      text = AppLocalizations.of(context).noAccess;
      icon = const Icon(Icons.clear);
    } else {
      switch (access) {
        case AccessType.write:
          text = AppLocalizations.of(context).canEdit;
          icon = const Icon(Icons.edit);
          break;
        case AccessType.read:
          text = AppLocalizations.of(context).canView;
          icon = const Icon(Icons.remove_red_eye);
          break;
        case AccessType.owner:
          text = AppLocalizations.of(context).owner;
          icon = const Icon(Icons.person);
          break;
      }
    }
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: <Widget>[
        Text(
          text,
          style: AppStyles.secondaryText,
        ),
        icon,
      ],
    );
  }
}
