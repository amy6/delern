import 'dart:async';

import 'package:firebase_database/firebase_database.dart';

import 'model.dart';

class Transaction {
  final List<Model> _toSave = List<Model>();
  final List<Model> _toDelete = List<Model>();

  static bool _isOnline = false;

  static void subscribeToOnlineStatus() {
    FirebaseDatabase.instance
        .reference()
        .child('.info/connected')
        .onValue
        .listen((event) {
      _isOnline = event.snapshot.value;
    });
  }

  void save(Model m) => _toSave.add(m);

  void delete(Model m) {
    assert(m.key != null);
    _toDelete.add(m);
  }

  void deleteAll(Model m) {
    assert(m.key == null);
    _toDelete.add(m);
  }

  Future<void> commit() {
    var root = FirebaseDatabase.instance.reference();
    var updates = Map<String, dynamic>();
    _toSave.forEach((m) {
      if (m.key == null) {
        m.key = root.child(m.rootPath).push().key;
        updates.addAll(m.toMap(true));
      } else {
        updates.addAll(m.toMap(false));
      }
    });
    _toDelete.forEach((m) {
      if (m.key == null) {
        updates['${m.rootPath}'] = null;
      } else {
        updates['${m.rootPath}/${m.key}'] = null;
      }
    });
    // TODO(dotdoom): log updates on failure.
    var updateFuture = root.update(updates);
    // Firebase update() does not return until it gets response from the server.
    return _isOnline ? updateFuture : Future.value();
  }
}
