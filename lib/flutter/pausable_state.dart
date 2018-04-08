import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

abstract class PausableState<T extends StatefulWidget> extends State<T>
    with WidgetsBindingObserver {
  // TODO(dotdoom): trigger events when maintainState=true under an overlay.
  bool _appLifecycleResumed = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    //didChangeAppLifecycleState(AppLifecycleState.resumed);
  }

  @override
  void deactivate() {
    didChangeAppLifecycleState(AppLifecycleState.paused);
    super.deactivate();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    didChangeAppLifecycleState(AppLifecycleState.resumed);
    return super.build(context);
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
