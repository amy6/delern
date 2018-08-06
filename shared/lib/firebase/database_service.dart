class DatabaseSnapshot {}

class _Event {
  final DatabaseSnapshot snapshot;
}

class FirebaseDatabaseService {
  Stream<_Event> onValue(String path) =>
      FirebaseDatabase.reference().child(path).onValue;
}
