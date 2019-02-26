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
    _closeScreenController.stream.listen((_) async {
      if (await userClosesScreen()) {
        notifyPop();
      }
    });
  }

  /// Contains internationalized messages. It used to show user messages
  AppLocalizations get locale => _locale;

  /// A stream that emit an event when screen must be closed
  Stream<void> get doPop => _onPopController.stream;
  final _onPopController = StreamController<void>();

  /// A stream that emits an error message when an error occurs.
  Stream<String> get doShowError => _onErrorController.stream;
  final _onErrorController = StreamController<String>();

  /// Sink to write when locale is changed
  Sink<AppLocalizations> get onLocale => _localeController.sink;
  final _localeController = StreamController<AppLocalizations>();

  /// Sink to write an event when user decides to leave a screen
  Sink<void> get onCloseScreen => _closeScreenController.sink;
  final _closeScreenController = StreamController<void>();

  /// Call when any errors occur
  @protected
  void notifyErrorOccurred(Exception e) {
    _onErrorController
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
    _onPopController.add(null);
  }

  /// Method releases resources
  @mustCallSuper
  void dispose() {
    _onPopController.close();
    _onErrorController.close();
    _localeController.close();
    _closeScreenController.close();
  }
}
