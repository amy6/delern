import 'dart:async';

import 'package:flutter/material.dart';

import '../flutter/pausable_state.dart';

abstract class VMViewWidget<T> extends StatefulWidget {
  final Future<T> viewModel;

  VMViewWidget(this.viewModel) : super();
}

abstract class VMViewState<T, W extends VMViewWidget<T>>
    extends PausableState<W> {
  //StreamSubscription<T> _subscription;
  T model;

  @override
  void pauseState() {
    super.pauseState();
    // TODO(dotdoom): _subscription.cancel();
  }

  @override
  void resumeState() {
    super.resumeState();
    widget.viewModel.then((m) => setState(() => model = m));
  }
}
