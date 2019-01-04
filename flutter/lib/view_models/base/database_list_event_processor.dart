import 'package:delern_flutter/models/base/database_list_event.dart';
import 'package:delern_flutter/models/base/keyed_list_item.dart';
import 'package:delern_flutter/view_models/base/keyed_list_event_processor.dart';
import 'package:meta/meta.dart';

/// A processor (see [KeyedListEventProcessor]) that handles (Firebase) Database
/// events for list initial onValue and subsequent onChild* events, and builds
/// and updates internal [list] representation of how that list looks like.
class DatabaseListEventProcessor<T extends KeyedListItem>
    extends KeyedListEventProcessor<T, DatabaseListEvent<T>> {
  DatabaseListEventProcessor(StreamGetter<DatabaseListEvent<T>> source)
      : super(source);

  @protected
  void processEvent(DatabaseListEvent<T> event) {
    switch (event.eventType) {
      case ListEventType.itemAdded:
        if (list.indexOfKey(event.value.key) < 0) {
          final index = list.indexOfKey(event.previousSiblingKey) + 1;
          list.insert(index, event.value);
        } else {
          // With Firebase, we subscribe to onValue, which delivers all data,
          // and then onChild* events. onChildAdded events are also initially
          // delivered for every child. We must therefore skip keys that we
          // already have.
          assert(event.previousSiblingKey == null ||
              list.indexOfKey(event.previousSiblingKey) >= 0);
          // TODO(dotdoom): should we update here?
        }
        break;
      case ListEventType.itemRemoved:
        list.removeAt(list.indexOfKey(event.value.key));
        break;
      case ListEventType.itemChanged:
        // With Firebase, some events may be delivered twice - by different
        // listeners. E.g. "remove(X)" then "change(X -> null)", in which case
        // the item will no longer exist by the time "change(...)" arrives.
        var index = list.indexOfKey(event.value.key);
        if (index >= 0) {
          list.setAt(index, event.value);
        }
        break;
      case ListEventType.itemMoved:
        list.move(list.indexOfKey(event.value.key),
            list.indexOfKey(event.previousSiblingKey) + 1);
        break;
      case ListEventType.setAll:
        list.setAll(event.fullListValueForSet);
        break;
    }
  }
}
