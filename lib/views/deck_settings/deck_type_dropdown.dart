import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../models/deck.dart';

typedef void DeckTypeCallback(DeckType t);

class DeckTypeDropdown extends StatefulWidget {
  final DeckType value;
  final DeckTypeCallback valueChanged;

  DeckTypeDropdown({@required this.value, @required this.valueChanged})
      : assert(valueChanged != null);

  @override
  State<StatefulWidget> createState() => _DeckTypeDropdownState();
}

class _DeckTypeDropdownState extends State<DeckTypeDropdown> {
  @override
  Widget build(BuildContext context) => DropdownButton<DeckType>(
        // Provide default value.
        value: widget.value,
        items: (DeckType.values).map((DeckType value) {
          return new DropdownMenuItem<DeckType>(
            child: _buildDropdownItem(value),
            value: value,
          );
        }).toList(),
        onChanged: (DeckType newValue) {
          setState(() {
            widget.valueChanged(newValue);
          });
        },
      );

  Widget _buildDropdownItem(DeckType deckType) {
    String text;
    var locale = AppLocalizations.of(context);

    switch (deckType) {
      case DeckType.basic:
        text = locale.basicDeckType;
        break;
      case DeckType.german:
        text = locale.germanDeckType;
        break;
      case DeckType.swiss:
        text = locale.swissDeckType;
        break;
    }
    // TODO(ksheremet): Expand to fill parent
    return Text(text);
  }
}
