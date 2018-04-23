import 'dart:async';

import 'package:flutter/material.dart';
import 'package:meta/meta.dart';

import '../models/observable_list.dart';

typedef Widget ObservingGridItemBuilder<T>(
  BuildContext context,
  T item,
);

class ObservingGrid<T> extends StatefulWidget {
  ObservingGrid({
    Key key,
    @required this.items,
    @required this.itemBuilder,
    @required this.maxCrossAxisExtent,
  }) : super(key: key);

  final ObservableList<T> items;
  final ObservingGridItemBuilder<T> itemBuilder;
  final double maxCrossAxisExtent;

  @override
  ObservingGridState<T> createState() => new ObservingGridState<T>();
}

class ObservingGridState<T> extends State<ObservingGrid<T>> {
  StreamSubscription<ListEvent<T>> _listSubscription;

  @override
  void didChangeDependencies() {
    _listSubscription?.cancel();
    _listSubscription = widget.items.events.listen(_processListEvent);
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
      case ListEventType.itemRemoved:
      case ListEventType.set:
      // Note: number of items must not change here (unless it's the first
      // update; we validate this in proxy_keyed_list.dart).
      case ListEventType.itemChanged:
      case ListEventType.itemMoved:
        setState(() {});
        break;
    }
  }

  Widget _buildItem(BuildContext context, T item) {
    return widget.itemBuilder(context, item);
  }

  @override
  Widget build(BuildContext context) {
    if (!widget.items.changed) {
      return new Center(child: new CircularProgressIndicator());
    }

    // TODO(ksheremet): for an empty list, return 'Add your items'

    return new GridView.extent(
      maxCrossAxisExtent: widget.maxCrossAxisExtent,
      children:
          new List.of(widget.items.map((entry) => _buildItem(context, entry))),
    );
  }
}
