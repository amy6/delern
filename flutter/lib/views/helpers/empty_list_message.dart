import 'package:flutter/material.dart';

import '../../flutter/styles.dart';

class EmptyListMessage extends StatelessWidget {
  final String _displayText;

  const EmptyListMessage(this._displayText);

  @override
  Widget build(BuildContext context) => Center(
        child: Text(
          _displayText,
          style: AppStyles.secondaryText,
        ),
      );
}
