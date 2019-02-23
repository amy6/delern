import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/models/deck_model.dart';
import 'package:delern_flutter/views/helpers/card_background_specifier.dart';
import 'package:test/test.dart';

void main() {
  group('Swiss German', () {
    const swissDeckType = DeckType.swiss;

    test('feminine', () {
      expect(specifyCardBackground(swissDeckType, 'd Muetter'),
          AppStyles.cardBackgroundColors[Gender.feminine]);

      expect(specifyCardBackground(swissDeckType, 'e Mappe'),
          AppStyles.cardBackgroundColors[Gender.feminine]);
    });

    test('masculine', () {
      expect(specifyCardBackground(swissDeckType, 'de Vater'),
          AppStyles.cardBackgroundColors[Gender.masculine]);

      expect(specifyCardBackground(swissDeckType, 'en Vater'),
          AppStyles.cardBackgroundColors[Gender.masculine]);
    });

    test('neuter', () {
      expect(specifyCardBackground(swissDeckType, 's Madchen'),
          AppStyles.cardBackgroundColors[Gender.neuter]);

      expect(specifyCardBackground(swissDeckType, 'es Madchen'),
          AppStyles.cardBackgroundColors[Gender.neuter]);
    });

    test('noGender', () {
      expect(specifyCardBackground(swissDeckType, 'laufen'),
          AppStyles.cardBackgroundColors[Gender.noGender]);
    });
  });

  group('High German', () {
    const germanDeckType = DeckType.german;

    test('feminine', () {
      expect(specifyCardBackground(germanDeckType, 'die Mutter'),
          AppStyles.cardBackgroundColors[Gender.feminine]);

      expect(specifyCardBackground(germanDeckType, 'eine Lampe'),
          AppStyles.cardBackgroundColors[Gender.feminine]);
    });

    test('masculine', () {
      expect(specifyCardBackground(germanDeckType, 'der Vater'),
          AppStyles.cardBackgroundColors[Gender.masculine]);
    });

    test('neuter', () {
      expect(specifyCardBackground(germanDeckType, 'das Madchen'),
          AppStyles.cardBackgroundColors[Gender.neuter]);
    });

    test('noGender', () {
      expect(specifyCardBackground(germanDeckType, 'laufen'),
          AppStyles.cardBackgroundColors[Gender.noGender]);
    });
  });

  group('Basic', () {
    const basicDeckType = DeckType.basic;

    test('noColor', () {
      expect(specifyCardBackground(basicDeckType, 'die Mutter'),
          AppStyles.cardBackgroundColors[Gender.noGender]);
    });
  });
}
