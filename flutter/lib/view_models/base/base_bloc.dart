import 'dart:async';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/user_messages.dart';

class BaseBloc {
  AppLocalizations _locale;

  BaseBloc() {
    _localeController.stream.listen((locale) {
      _locale = locale;
    });
  }

  /// Contains internationalized messages. It used to show user messages
  AppLocalizations get locale => _locale;

  final _onPopController = StreamController<void>();

  /// A stream that emit an event when user leaves the screen by pressing
  /// back button.
  Stream<void> get onPop => _onPopController.stream;

  final _onErrorController = StreamController<String>();

  /// A stream that emits an error message when an error occurs.
  Stream<String> get onErrorOccurred => _onErrorController.stream;

  final _localeController = StreamController<AppLocalizations>();

  /// Sink to write when locale is changed
  Sink<AppLocalizations> get localeSink => _localeController.sink;

  /// Call when any errors occur
  void notifyErrorOccurred(Exception e) {
    _onErrorController
        .add(UserMessages.formUserFriendlyErrorMessage(locale, e));
  }

  /// Call when user leaves the screen
  void notifyCloseScreen() {
    _onPopController.add(null);
  }

  /// Method releases resources
  void dispose() {
    _onPopController.close();
    _onErrorController.close();
    _localeController.close();
  }
}
