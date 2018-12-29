import 'dart:async';

import 'package:meta/meta.dart';

import '../../models/base/keyed_list_item.dart';
import 'observable_keyed_list.dart';

abstract class KeyedListEventProcessor<TElement extends KeyedListItem,
    TInputEvent> {
  @protected
  ObservableKeyedList<TElement> list;

  Stream<ListEvent<TElement>> get events => list.events;
  List<TElement> get value => list.value;

  StreamSubscription<TInputEvent> _sub;
  final StreamGetter<TInputEvent> source;

  KeyedListEventProcessor(this.source) {
    // Has to be synchronous to wait on the next update before value is changed.
    list = ObservableKeyedList<TElement>(StreamController.broadcast(
        onListen: () => _sub = source().listen((event) => processEvent(event)),
        onCancel: _sub.cancel,
        sync: true));
  }

  @protected
  void processEvent(TInputEvent event);
}
