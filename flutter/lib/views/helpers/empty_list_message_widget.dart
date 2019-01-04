import 'package:delern_flutter/flutter/styles.dart';
import 'package:flutter/material.dart';

class EmptyListMessageWidget extends StatelessWidget {
  final String _displayText;

  const EmptyListMessageWidget(this._displayText);

  @override
  Widget build(BuildContext context) => Center(
        child: Text(
          _displayText,
          style: AppStyles.secondaryText,
        ),
      );
}
