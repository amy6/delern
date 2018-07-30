// This line imports the extension
import 'package:flutter_driver/driver_extension.dart';

import '../lib/main.dart' as app;

void main() {
  // This line enables the extension
  enableFlutterDriverExtension();

  // Call the `main()` of your app or call `runApp` with whatever widget
  // you are interested in testing.
  app.main();
}
