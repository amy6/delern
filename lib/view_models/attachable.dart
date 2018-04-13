abstract class Attachable<T> {
  void detach();
  void attachTo(T owner);
}
