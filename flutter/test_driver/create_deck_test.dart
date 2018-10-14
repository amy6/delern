import 'package:flutter_driver/flutter_driver.dart';
import 'package:test/test.dart';

void main() {
  group('create deck', () {
    FlutterDriver driver;

    setUpAll(() async {
      driver = await FlutterDriver.connect(
          dartVmServiceUrl: 'http://127.0.0.1:33401/');
    });

    tearDownAll(() async {
      driver?.close();
    });

    test('from main window', () async {
      final button = find.text('Continue Anonymously');
      await driver.waitFor(button);
      await driver.tap(button);

      final fab = find.byType('FloatingActionButton');
      await driver.waitFor(fab);
      await driver.tap(fab);

      final add = find.text('ADD');
      await driver.waitFor(add);
      await driver.enterText('My Test Deck');
      await driver.tap(add);

      final deck = find.text('My Test Deck');
      await driver.waitFor(deck);
    });
  });
}
