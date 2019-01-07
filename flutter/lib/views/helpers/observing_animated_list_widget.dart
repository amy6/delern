import 'dart:async';

import 'package:delern_flutter/models/base/delayed_initialization.dart';
import 'package:delern_flutter/views/helpers/progress_indicator_widget.dart';
import 'package:flutter/material.dart';
import 'package:meta/meta.dart';
import 'package:observable/observable.dart';

typedef ObservingAnimatedListItemBuilder<T> = Widget Function(
  BuildContext context,
  T item,
  Animation<double> animation,
  // TODO(dotdoom): consider usefulness of index.
  int index,
);

typedef WidgetBuilder = Widget Function();

class ObservingAnimatedListWidget<T> extends StatefulWidget {
  const ObservingAnimatedListWidget({
    @required this.list,
    @required this.itemBuilder,
    @required this.emptyMessageBuilder,
    Key key,
  })  : assert(itemBuilder != null),
        super(key: key);

  final DelayedInitializationObservableList<T> list;
  final ObservingAnimatedListItemBuilder<T> itemBuilder;
  final WidgetBuilder emptyMessageBuilder;

  @override
  ObservingAnimatedListWidgetState<T> createState() =>
      ObservingAnimatedListWidgetState<T>();
}

class ObservingAnimatedListWidgetState<T>
    extends State<ObservingAnimatedListWidget<T>> {
  final GlobalKey<AnimatedListState> _animatedListKey =
      GlobalKey<AnimatedListState>();

  StreamSubscription<List<ListChangeRecord<T>>> _listSubscription;

  @override
  void initState() {
    _listSubscription = widget.list.listChanges.listen(_processListChanges);
    super.initState();
  }

  @override
  void dispose() {
    _listSubscription.cancel();
    super.dispose();
  }

  void _processListChanges(List<ListChangeRecord<T>> changes) {
    if (_animatedListKey.currentState == null) {
      // The list state is not available because the widget has not been created
      // yet. This happens when the data was empty (no items) and we showed an
      // 'empty list' message instead of the list widget. Now that we got some
      // data, create the list widget!
      setState(() {});
      return;
    }

    changes.forEach((change) {
      change.removed.forEach((removedValue) => _animatedListKey.currentState
          .removeItem(
              change.index,
              (context, animation) => widget.itemBuilder(
                  context, removedValue, animation, change.index)));

      for (var i = 0; i < change.addedCount; ++i) {
        _animatedListKey.currentState.insertItem(change.index + i);
      }

      // TODO(dotdoom): detect individual item changes rather than remove + add.
    });
  }

  Widget _buildItem(
          BuildContext context, int index, Animation<double> animation) =>
      widget.itemBuilder(context, widget.list[index], animation, index);

  @override
  Widget build(BuildContext context) => FutureBuilder(
      future: widget.list.initializationComplete,
      builder: (context, snapshot) {
        if (snapshot.connectionState != ConnectionState.done) {
          return ProgressIndicatorWidget();
        }

        if (widget.list.isEmpty) {
          return widget.emptyMessageBuilder();
        }

        return AnimatedList(
          key: _animatedListKey,
          itemBuilder: _buildItem,
          initialItemCount: widget.list.length,
        );
      });
}
