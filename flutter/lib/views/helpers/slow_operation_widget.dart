import 'dart:async';

import 'package:flutter/material.dart';

typedef ChildCallback = Future<void> Function();
typedef ChildCallbackBuilder = ChildCallback Function(
    ChildCallback originalFunction);
typedef ChildBuilder = Widget Function(
    ChildCallbackBuilder childCallbackBuilder);

class SlowOperationWidget<T extends Function> extends StatefulWidget {
  final ChildBuilder childBuilder;

  const SlowOperationWidget(this.childBuilder);

  @override
  State<StatefulWidget> createState() => _SlowOperationWidgetState();
}

class _SlowOperationWidgetState extends State<SlowOperationWidget> {
  bool _inProgress = false;

  ChildCallback _childCallbackBuilder(ChildCallback originalFunction) {
    if (_inProgress) {
      return null;
    }
    return () async {
      setState(() => _inProgress = true);
      try {
        return await originalFunction();
      } finally {
        if (mounted) {
          setState(() => _inProgress = false);
        }
      }
    };
  }

  @override
  Widget build(BuildContext context) =>
      widget.childBuilder(_childCallbackBuilder);
}
