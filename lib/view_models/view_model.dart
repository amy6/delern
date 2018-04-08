import '../models/observable_list.dart';
import '../models/persistables_list_mixin.dart';
import '../models/keyed_event_list_mixin.dart';

abstract class PersistableKeyedItem implements KeyedListItem, Persistable {}

class PersistableKeyedItemsList<T extends PersistableKeyedItem>
    extends ObservableList<T>
    with PersistablesListMixin<T>, KeyedEventListMixin<T> {}
