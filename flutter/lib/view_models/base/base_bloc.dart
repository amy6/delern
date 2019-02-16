import 'dart:async';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/user_messages.dart';

abstract class BaseBloc {
  AppLocalizations _locale;

  BaseBloc() {
    _localeController.stream.listen((locale) {
      _locale = locale;
    });
  }

  AppLocalizations get locale => _locale;

  final _onPopController = StreamController<void>();
  Stream<void> get onPop => _onPopController.stream;

  final _onErrorController = StreamController<String>();
  Stream<String> get onErrorOccurred => _onErrorController.stream;

  final _localeController = StreamController<AppLocalizations>();
  Sink<AppLocalizations> get localeSink => _localeController.sink;

  void notifyErrorOccurred(Exception e) {
    _onErrorController
        .add(UserMessages.formUserFriendlyErrorMessage(locale, e));
  }

  void notifyCloseScreen() {
    _onPopController.add(null);
  }

  void dispose() {
    _onPopController.close();
    _onErrorController.close();
    _localeController.close();
  }
}
