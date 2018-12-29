import 'dart:async';

import 'package:flutter/material.dart';
import 'package:meta/meta.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../models/base/keyed_list_item.dart';
import '../../view_models/base/observable_keyed_list.dart';
import '../../views/helpers/empty_list_message.dart';
import '../helpers/helper_progress_indicator.dart';

typedef ObservingGridItemBuilder<T> = Widget Function(T item);

class ObservingGrid<T extends KeyedListItem> extends StatefulWidget {
  const ObservingGrid({
    @required this.items,
    @required this.itemBuilder,
    @required this.maxCrossAxisExtent,
    @required this.emptyGridUserMessage,
    Key key,
  }) : super(key: key);

  final ObservableKeyedList<T> items;
  final ObservingGridItemBuilder<T> itemBuilder;
  final double maxCrossAxisExtent;
  // TODO(dotdoom): make this more abstract or rename to ObservingCardsGridView
  final String emptyGridUserMessage;

  @override
  ObservingGridState<T> createState() => ObservingGridState<T>();
}

class ObservingGridState<T extends KeyedListItem>
    extends State<ObservingGrid<T>> {
  StreamSubscription<ListEvent<T>> _listSubscription;

  @override
  void initState() {
    _listSubscription = widget.items.events.listen((_) => setState(() {}));
    super.initState();
  }

  @override
  void dispose() {
    _listSubscription.cancel();
    super.dispose();
  }

  Widget _buildItem(T item) => widget.itemBuilder(item);

  @override
  Widget build(BuildContext context) {
    if (widget.items.value == null) {
      return HelperProgressIndicator();
    }

    if (widget.items.value.isEmpty) {
      return EmptyListMessage(widget.emptyGridUserMessage);
    }

    return Column(
      children: <Widget>[
        Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: <Widget>[
            Text(
              AppLocalizations.of(context)
                  .numberOfCards(widget.items.value.length),
              style: AppStyles.secondaryText,
            ),
          ],
        ),
        Expanded(
          child: GridView.extent(
              maxCrossAxisExtent: widget.maxCrossAxisExtent,
              children:
                  widget.items.value.map(_buildItem).toList(growable: false)),
        ),
      ],
    );
  }
}
