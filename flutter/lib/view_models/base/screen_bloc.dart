import 'dart:async';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/user_messages.dart';
import 'package:meta/meta.dart';

abstract class ScreenBloc {
  AppLocalizations _locale;

  ScreenBloc() {
    _onLocaleController.stream.listen((locale) {
      _locale = locale;
    });
    _onCloseScreenController.stream.listen((_) async {
      if (await userClosesScreen()) {
        notifyPop();
      }
    });
  }

  /// Contains internationalized messages. It used to show user messages
  AppLocalizations get locale => _locale;

  /// A stream that emit an event when screen must be closed
  Stream<void> get doPop => _doPopController.stream;
  final _doPopController = StreamController<void>();

  /// A stream that emits an error message when an error occurs.
  Stream<String> get doShowError => _doShowErrorController.stream;
  final _doShowErrorController = StreamController<String>();

  /// Sink to write when locale is changed
  Sink<AppLocalizations> get onLocale => _onLocaleController.sink;
  final _onLocaleController = StreamController<AppLocalizations>();

  /// Sink to write an event when user decides to leave a screen
  Sink<void> get onCloseScreen => _onCloseScreenController.sink;
  final _onCloseScreenController = StreamController<void>();

  /// Call when any errors occur
  @protected
  void notifyErrorOccurred(Exception e) {
    _doShowErrorController
        .add(UserMessages.formUserFriendlyErrorMessage(locale, e));
  }

  /// Method that checks whether it is ok to close the screen.
  /// On default method always allows to close a screen. To add more
  /// functionality it should be overwritten in a subclass.
  @protected
  Future<bool> userClosesScreen() async => true;

  /// Internal method that called by BLoC when screen must be closed
  @protected
  void notifyPop() {
    _doPopController.add(null);
  }

  /// Method releases resources
  @mustCallSuper
  void dispose() {
    _doPopController.close();
    _doShowErrorController.close();
    _onLocaleController.close();
    _onCloseScreenController.close();
  }
}
