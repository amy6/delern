import 'dart:async';

import 'package:flutter/material.dart';
import 'package:meta/meta.dart';

import '../../models/base/observable_list.dart';
import 'progress_indicator.dart' as progressBar;

typedef Widget ObservingAnimatedListItemBuilder<T>(
  BuildContext context,
  T item,
  Animation<double> animation,
  int index,
);

class ObservingAnimatedList<T> extends StatefulWidget {
  ObservingAnimatedList({
    Key key,
    @required this.list,
    @required this.itemBuilder,
  })  : assert(itemBuilder != null),
        super(key: key);

  final ObservableList<T> list;
  final ObservingAnimatedListItemBuilder<T> itemBuilder;

  @override
  ObservingAnimatedListState<T> createState() =>
      ObservingAnimatedListState<T>();
}

class ObservingAnimatedListState<T> extends State<ObservingAnimatedList<T>> {
  final GlobalKey<AnimatedListState> _animatedListKey =
      GlobalKey<AnimatedListState>();

  StreamSubscription<ListEvent<T>> _listSubscription;

  @override
  void didChangeDependencies() {
    _listSubscription?.cancel();
    _listSubscription = widget.list.events.listen(_processListEvent);
    super.didChangeDependencies();
  }

  @override
  void dispose() {
    _listSubscription?.cancel();
    super.dispose();
  }

  void _processListEvent(ListEvent<T> event) {
    switch (event.eventType) {
      case ListEventType.itemAdded:
        _animatedListKey.currentState.insertItem(event.index);
        break;
      case ListEventType.itemRemoved:
        _animatedListKey.currentState.removeItem(event.index,
            (BuildContext context, Animation<double> animation) {
          return widget.itemBuilder(
              context, event.previousValue, animation, event.index);
        });
        break;
      case ListEventType.set:
      // Note: number of items must not change here (unless it's the first
      // update; we validate this in proxy_keyed_list.dart).
      case ListEventType.itemChanged:
      case ListEventType.itemMoved:
        setState(() {});
        break;
    }
  }

  Widget _buildItem(
      BuildContext context, int index, Animation<double> animation) {
    return widget.itemBuilder(context, widget.list[index], animation, index);
  }

  @override
  Widget build(BuildContext context) {
    if (!widget.list.changed) {
      return progressBar.ProgressIndicator();
    }

    // TODO(ksheremet): for an empty list, return 'Add your items'
    return AnimatedList(
      key: _animatedListKey,
      itemBuilder: _buildItem,
      initialItemCount: widget.list.length,
    );
  }
}
