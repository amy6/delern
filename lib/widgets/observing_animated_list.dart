import 'dart:async';

import 'package:flutter/material.dart';
import 'package:meta/meta.dart';

import '../models/observable_list.dart';

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
  }) : super(key: key) {
    assert(itemBuilder != null);
  }

  final ObservableList<T> list;
  final ObservingAnimatedListItemBuilder<T> itemBuilder;

  @override
  ObservingAnimatedListState<T> createState() =>
      new ObservingAnimatedListState<T>();
}

class ObservingAnimatedListState<T> extends State<ObservingAnimatedList<T>> {
  final GlobalKey<AnimatedListState> _animatedListKey =
      new GlobalKey<AnimatedListState>();

  StreamSubscription<ListEvent<T>> _listSubscription;

  @override
  void didChangeDependencies() {
    if (_listSubscription != null) {
      _listSubscription.cancel();
    }
    _listSubscription = widget.list.events.listen(_onListEvent);
    super.didChangeDependencies();
  }

  @override
  void dispose() {
    super.dispose();
  }

  void _onListEvent(ListEvent<T> event) {
    switch (event.eventType) {
      case ListEventType.itemAdded:
        _animatedListKey.currentState.insertItem(event.index,
            duration: const Duration(milliseconds: 300));
        break;
      case ListEventType.itemRemoved:
        _animatedListKey.currentState.removeItem(
          event.index,
          (BuildContext context, Animation<double> animation) {
            return widget.itemBuilder(
                context, event.previousValue, animation, event.index);
          },
          duration: const Duration(milliseconds: 300),
        );
        break;
      case ListEventType.itemChanged:
      case ListEventType.itemMoved:
      case ListEventType.set:
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
    return new AnimatedList(
      key: _animatedListKey,
      itemBuilder: _buildItem,
      initialItemCount: widget.list.length,
    );
  }
}
