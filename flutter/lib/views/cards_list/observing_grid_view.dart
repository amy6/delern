import 'dart:async';

import 'package:flutter/material.dart';
import 'package:meta/meta.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../models/base/observable_list.dart';
import '../../views/helpers/empty_list_message.dart';
import '../helpers/progress_indicator.dart';

typedef Widget ObservingGridItemBuilder<T>(
  T item,
);

class ObservingGrid<T> extends StatefulWidget {
  ObservingGrid({
    @required this.items,
    @required this.itemBuilder,
    @required this.maxCrossAxisExtent,
    @required this.emptyGridUserMessage,
    Key key,
  }) : super(key: key);

  final ObservableList<T> items;
  final ObservingGridItemBuilder<T> itemBuilder;
  final double maxCrossAxisExtent;
  // TODO(dotdoom): make this more abstract or rename to 'ObservingCardsGridView'
  final String emptyGridUserMessage;

  @override
  ObservingGridState<T> createState() => ObservingGridState<T>();
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

  Widget _buildItem(T item) => widget.itemBuilder(item);

  @override
  Widget build(BuildContext context) {
    if (!widget.items.changed) {
      return HelperProgressIndicator();
    }

    if (widget.items.isEmpty) {
      return EmptyListMessage(widget.emptyGridUserMessage);
    }

    return Column(
      children: <Widget>[
        Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: <Widget>[
            Text(
              AppLocalizations.of(context).numberOfCards(widget.items.length),
              style: AppStyles.secondaryText,
            ),
          ],
        ),
        Expanded(
          child: GridView.extent(
            maxCrossAxisExtent: widget.maxCrossAxisExtent,
            children: List.of(widget.items.map(_buildItem)),
          ),
        ),
      ],
    );
  }
}
