import 'package:delern_flutter/main.dart' as app;
import 'package:flutter_driver/driver_extension.dart';

void main() {
  // This line enables the extension
  enableFlutterDriverExtension();

  // Call the `main()` of your app or call `runApp` with whatever widget
  // you are interested in testing.
  app.main();
}
