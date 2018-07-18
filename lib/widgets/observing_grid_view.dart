import 'dart:async';

import 'package:flutter/material.dart';
import 'package:meta/meta.dart';

import '../models/base/observable_list.dart';
import '../widgets/progress_indicator.dart' as progressBar;

typedef Widget ObservingGridItemBuilder<T>(
  T item,
);

class ObservingGrid<T> extends StatefulWidget {
  ObservingGrid({
    Key key,
    @required this.items,
    @required this.itemBuilder,
    @required this.maxCrossAxisExtent,
    @required this.numberOfCardsLabel,
  }) : super(key: key);

  final ObservableList<T> items;
  final ObservingGridItemBuilder<T> itemBuilder;
  final double maxCrossAxisExtent;
  // TODO(dotdoom): make this more abstract or rename to 'ObservingCardsGridView'
  final String numberOfCardsLabel;

  @override
  ObservingGridState<T> createState() => new ObservingGridState<T>();
}

class ObservingGridState<T> extends State<ObservingGrid<T>> {
  StreamSubscription<ListEvent<T>> _listSubscription;

  @override
  void initState() {
    _listSubscription?.cancel();
    _listSubscription = widget.items.events.listen(_processListEvent);
    super.initState();
  }

  @override
  void dispose() {
    _listSubscription?.cancel();
    super.dispose();
  }

  void _processListEvent(ListEvent<T> event) {
    setState(() {});
  }

  Widget _buildItem(T item) {
    return widget.itemBuilder(item);
  }

  @override
  Widget build(BuildContext context) {
    if (!widget.items.changed) {
      return progressBar.ProgressIndicator();
    }

    // TODO(ksheremet): for an empty list, return 'Add your items'

    return new Column(
      children: <Widget>[
        new Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: <Widget>[
            new Text(
              '${widget.numberOfCardsLabel} ${widget.items.length}',
            ),
          ],
        ),
        new Expanded(
          child: new GridView.extent(
            maxCrossAxisExtent: widget.maxCrossAxisExtent,
            children:
                new List.of(widget.items.map((entry) => _buildItem(entry))),
          ),
        ),
      ],
    );
  }
}
