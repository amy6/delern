import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/views/helpers/card_background_specifier.dart';
import 'package:delern_flutter/views/helpers/card_display_widget.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('Display card', (tester) async {
    const frontSide = 'die Mutter';
    const backSide = 'mother';

    // Widget must be wrapped in MaterialApp widget because it uses material
    // related classes.
    await tester.pumpWidget(MaterialApp(
      home: CardDisplayWidget(
        front: frontSide,
        back: backSide,
        backgroundColor: AppStyles.cardBackgroundColors[Gender.feminine],
        isMarkdown: false,
        showBack: true,
      ),
    ));

    final frontFinder = find.text(frontSide);
    final backFinder = find.text(backSide);

    expect(frontFinder, findsOneWidget);
    expect(backFinder, findsOneWidget);
  });
}
