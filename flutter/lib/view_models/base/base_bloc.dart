import 'dart:async';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/user_messages.dart';

abstract class BaseBloc {
  final AppLocalizations locale;

  BaseBloc([this.locale]);

  final _onPopController = StreamController<void>();
  Stream<void> get onPop => _onPopController.stream;

  final _onErrorController = StreamController<String>();
  Stream<String> get onErrorOccurred => _onErrorController.stream;

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
  }
}
