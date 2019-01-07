import 'dart:async';

import 'package:delern_flutter/flutter/localization.dart';
import 'package:delern_flutter/flutter/styles.dart';
import 'package:delern_flutter/models/base/delayed_initialization.dart';
import 'package:delern_flutter/models/base/keyed_list_item.dart';
import 'package:delern_flutter/views/helpers/empty_list_message_widget.dart';
import 'package:delern_flutter/views/helpers/progress_indicator_widget.dart';
import 'package:flutter/material.dart';
import 'package:meta/meta.dart';
import 'package:observable/observable.dart';

typedef ObservingGridItemBuilder<T> = Widget Function(T item);

class ObservingGridWidget<T extends KeyedListItem> extends StatefulWidget {
  const ObservingGridWidget({
    @required this.items,
    @required this.itemBuilder,
    @required this.maxCrossAxisExtent,
    @required this.emptyGridUserMessage,
    Key key,
  }) : super(key: key);

  final DelayedInitializationObservableList<T> items;
  final ObservingGridItemBuilder<T> itemBuilder;
  final double maxCrossAxisExtent;
  final String emptyGridUserMessage;

  @override
  ObservingGridWidgetState<T> createState() => ObservingGridWidgetState<T>();
}

class ObservingGridWidgetState<T extends KeyedListItem>
    extends State<ObservingGridWidget<T>> {
  StreamSubscription<List<ListChangeRecord<T>>> _listSubscription;

  @override
  void initState() {
    _listSubscription = widget.items.listChanges.listen((_) => setState(() {}));
    super.initState();
  }

  @override
  void dispose() {
    _listSubscription.cancel();
    super.dispose();
  }

  Widget _buildItem(T item) => widget.itemBuilder(item);

  @override
  Widget build(BuildContext context) => FutureBuilder(
      future: widget.items.initializationComplete,
      builder: (context, snapshot) {
        if (snapshot.connectionState != ConnectionState.done) {
          return ProgressIndicatorWidget();
        }

        if (widget.items.isEmpty) {
          return EmptyListMessageWidget(widget.emptyGridUserMessage);
        }

        return Column(
          children: <Widget>[
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: <Widget>[
                Text(
                  // TODO(dotdoom): make this more abstract.
                  AppLocalizations.of(context)
                      .numberOfCards(widget.items.length),
                  style: AppStyles.secondaryText,
                ),
              ],
            ),
            Expanded(
              child: GridView.extent(
                  maxCrossAxisExtent: widget.maxCrossAxisExtent,
                  children:
                      widget.items.map(_buildItem).toList(growable: false)),
            ),
          ],
        );
      });
}
