import 'package:meta/meta.dart';

import '../../models/base/events.dart';
import '../../models/base/keyed_list_item.dart';
import 'keyed_list_event_processor.dart';
import 'observable_keyed_list.dart';

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
          list.update(index, event.value);
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
