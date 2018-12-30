import 'dart:async';

import 'package:delern_flutter/models/base/database_list_event.dart';
import 'package:delern_flutter/models/base/keyed_list_item.dart';
import 'package:delern_flutter/view_models/base/filtered_sorted_keyed_list_processor.dart';
import 'package:delern_flutter/view_models/base/observable_keyed_list.dart';
import 'package:delern_flutter/views/helpers/helper_progress_indicator.dart';
import 'package:flutter/material.dart';
import 'package:meta/meta.dart';

typedef ObservingAnimatedListItemBuilder<T extends KeyedListItem> = Widget
    Function(
  BuildContext context,
  T item,
  Animation<double> animation,
  int index,
);

typedef WidgetBuilder = Widget Function();

class ObservingAnimatedList<T extends KeyedListItem> extends StatefulWidget {
  const ObservingAnimatedList({
    @required this.list,
    @required this.itemBuilder,
    @required this.emptyMessageBuilder,
    Key key,
  })  : assert(itemBuilder != null),
        super(key: key);

  final ObservableKeyedList<T> list;
  final ObservingAnimatedListItemBuilder<T> itemBuilder;
  final WidgetBuilder emptyMessageBuilder;

  @override
  ObservingAnimatedListState<T> createState() =>
      ObservingAnimatedListState<T>();
}

class ObservingAnimatedListState<T extends KeyedListItem>
    extends State<ObservingAnimatedList<T>> {
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
    _listSubscription.cancel();
    super.dispose();
  }

  static const _defaultAnimationDuration = Duration(milliseconds: 300);
  static const _filterAnimationDuration = Duration(milliseconds: 0);

  void _processListEvent(ListEvent<T> event) {
    if (_animatedListKey.currentState == null) {
      // The list state is not available because the widget has not been created
      // yet. This may happen when the data is empty and we show an 'empty list'
      // message instead of the list widget.
      setState(() {});
      return;
    }

    switch (event.eventType) {
      case ListEventType.itemAdded:
        _animatedListKey.currentState.insertItem(event.index,
            duration: event.eventSource == FilteredSortedKeyedListProcessor
                ? _filterAnimationDuration
                : _defaultAnimationDuration);
        break;
      case ListEventType.itemRemoved:
        _animatedListKey.currentState.removeItem(
            event.index,
            (context, animation) => widget.itemBuilder(
                context, event.previousValue, animation, event.index),
            duration: event.eventSource == FilteredSortedKeyedListProcessor
                ? _filterAnimationDuration
                : _defaultAnimationDuration);
        break;
      case ListEventType.setAll:
      // Note: number of items must not change here (unless it's the first
      // update; we validate this in proxy_keyed_list.dart).
      case ListEventType.itemChanged:
      case ListEventType.itemMoved:
        setState(() {});
        break;
    }
  }

  Widget _buildItem(
          BuildContext context, int index, Animation<double> animation) =>
      widget.itemBuilder(context, widget.list.value[index], animation, index);

  @override
  Widget build(BuildContext context) {
    if (widget.list == null || widget.list.value == null) {
      return HelperProgressIndicator();
    }

    if (widget.list.value.isEmpty) {
      return widget.emptyMessageBuilder();
    }

    return AnimatedList(
      key: _animatedListKey,
      itemBuilder: _buildItem,
      initialItemCount: widget.list.value.length,
    );
  }
}
