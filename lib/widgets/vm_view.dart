import 'dart:async';

import 'package:flutter/material.dart';

import '../flutter/pausable_state.dart';

abstract class VMViewWidget<T> extends StatefulWidget {
  final Stream<T> viewModelStream;

  VMViewWidget(this.viewModelStream) : super();
}

abstract class VMViewState<T, W extends VMViewWidget<T>>
    extends PausableState<W> {
  StreamSubscription<T> _subscription;
  T model;

  @override
  void pauseState() {
    super.pauseState();
    _subscription.cancel();
  }

  @override
  void resumeState() {
    super.resumeState();
    _subscription = widget.viewModelStream.listen(
      (T newModel) {
        setState(() => model = newModel);
      },
    );
  }
}
