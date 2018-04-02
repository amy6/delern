/// This class must be killed or renamed to MovableItemsList.
abstract class LameList<T> implements List<T> {
  void setAt(int index, T value);
  void move(int index1, int index2);
}
