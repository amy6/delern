import 'dart:async';

import 'package:flutter/material.dart';

typedef ChildCallback = Future<void> Function();
typedef ChildBuilder = Widget Function(ChildCallback callback);

class SlowOperationWidget extends StatefulWidget {
  final ChildBuilder childBuilder;
  final ChildCallback childCallback;

  const SlowOperationWidget(this.childBuilder, this.childCallback);

  @override
  State<StatefulWidget> createState() => _SlowOperationWidgetState();
}

class _SlowOperationWidgetState extends State<SlowOperationWidget> {
  bool _inProgress = false;

  @override
  Widget build(BuildContext context) {
    if (_inProgress) {
      return widget.childBuilder(null);
    }
    return widget.childBuilder(() async {
      setState(() => _inProgress = true);
      try {
        return await widget.childCallback();
      } finally {
        setState(() => _inProgress = false);
      }
    });
  }
}
