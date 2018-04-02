import '../models/observable_list.dart';
import '../models/persistables_list_mixin.dart';
import '../models/keyed_event_list_mixin.dart';

abstract class PersistableKeyedItem<T>
    implements KeyedListItem, Persistable<T> {}

class PersistableKeyedItemsList<T extends PersistableKeyedItem<T>>
    extends ObservableList<T>
    with PersistablesListMixin<T>, KeyedEventListMixin<T> {
  PersistableKeyedItemsList(List<T> base) : super(base);
}
