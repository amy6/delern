import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

abstract class PausableState<T extends StatefulWidget> extends State<T>
    with WidgetsBindingObserver {
  bool _appLifecycleResumed = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    didChangeAppLifecycleState(AppLifecycleState.resumed);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    didChangeAppLifecycleState(AppLifecycleState.paused);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);
    if (state == AppLifecycleState.resumed && !_appLifecycleResumed) {
      _appLifecycleResumed = true;
      resumeState();
    } else if (state == AppLifecycleState.paused && _appLifecycleResumed) {
      _appLifecycleResumed = false;
      pauseState();
    }
  }

  void resumeState() {}

  void pauseState() {}
}
