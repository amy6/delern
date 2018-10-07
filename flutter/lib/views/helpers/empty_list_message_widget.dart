import 'package:delern_flutter/flutter/styles.dart';
import 'package:flutter/material.dart';

class EmptyListMessageWidget extends StatelessWidget {
  final String _displayText;
  final Key textKey;

  const EmptyListMessageWidget(this._displayText, {this.textKey});

  @override
  Widget build(BuildContext context) => Center(
        child: Text(
          _displayText,
          key: textKey,
          style: AppStyles.secondaryText,
        ),
      );
}
