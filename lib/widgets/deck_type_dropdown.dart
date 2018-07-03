import 'package:flutter/material.dart';

import '../models/deck.dart';

typedef void DeckTypeCallback(DeckType t);

class DeckTypeDropdown extends StatefulWidget {
  final DeckType value;
  final DeckTypeCallback valueChanged;

  DeckTypeDropdown({@required this.value, @required this.valueChanged})
      : super() {
    assert(valueChanged != null);
  }

  @override
  State<StatefulWidget> createState() => _DeckTypeDropdownState();
}

class _DeckTypeDropdownState extends State<DeckTypeDropdown> {
  @override
  Widget build(BuildContext context) => DropdownButton<DeckType>(
        hint: Text('Select'),
        // Provide default value.
        value: widget.value,
        items: (DeckType.values).map((DeckType value) {
          return new DropdownMenuItem<DeckType>(
            child: buildDropdownItem(value),
            value: value,
          );
        }).toList(),
        onChanged: (DeckType newValue) {
          setState(() {
            widget.valueChanged(newValue);
          });
        },
      );

  Widget buildDropdownItem(DeckType deckType) {
    String text;

    switch (deckType) {
      case DeckType.basic:
        text = 'basic';
        break;
      case DeckType.german:
        text = 'german';
        break;
      case DeckType.swiss:
        text = 'swiss';
        break;
    }
    // TODO(ksheremet): Expand to fill parent
    return Text(text);
  }
}
