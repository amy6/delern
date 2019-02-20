import 'dart:async';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:meta/meta.dart';

abstract class ScreenBloc {
  AppLocalizations _locale;

  ScreenBloc() {
    _localeController.stream.listen((locale) {
      _locale = locale;
    });
  }

  /// Contains internationalized messages. It used to show user messages
  AppLocalizations get locale => _locale;

  final _onPopController = StreamController<void>();

  /// A stream that emit an event when screen must be closed
  Stream<void> get pop => _onPopController.stream;

  final _onErrorController = StreamController<String>();

  /// A stream that emits an error message when an error occurs.
  Stream<String> get showError => _onErrorController.stream;

  final _localeController = StreamController<AppLocalizations>();

  /// Sink to write when locale is changed
  Sink<AppLocalizations> get localeSink => _localeController.sink;

  /// Call when any errors occur
  void notifyErrorOccurred(Exception e) {
    _onErrorController
        .add(UserMessages.formUserFriendlyErrorMessage(locale, e));
  }

  /// Internal method that called by BLoC when screen must be closed
  @protected
  void notifyPop() {
    _onPopController.add(null);
  }

  void closeScreen();

  /// Method releases resources
  @mustCallSuper
  void dispose() {
    _onPopController.close();
    _onErrorController.close();
    _localeController.close();
  }
}
