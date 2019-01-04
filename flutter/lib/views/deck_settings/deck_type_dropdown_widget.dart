import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:flutter/material.dart';

typedef DeckTypeCallback = void Function(DeckType t);

class DeckTypeDropdownWidget extends StatefulWidget {
  final DeckType value;
  final DeckTypeCallback valueChanged;

  const DeckTypeDropdownWidget(
      {@required this.value, @required this.valueChanged})
      : assert(valueChanged != null);

  @override
  State<StatefulWidget> createState() => _DeckTypeDropdownWidgetState();
}

class _DeckTypeDropdownWidgetState extends State<DeckTypeDropdownWidget> {
  @override
  Widget build(BuildContext context) => DropdownButton<DeckType>(
        // Provide default value.
        value: widget.value,
        items: (DeckType.values)
            .map((value) => DropdownMenuItem<DeckType>(
                  child: _buildDropdownItem(value),
                  value: value,
                ))
            .toList(),
        onChanged: (newValue) {
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
    return Text(
      text,
      style: AppStyles.primaryText,
    );
  }
}
