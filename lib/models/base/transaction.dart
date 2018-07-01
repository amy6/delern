import 'dart:async';

import 'package:firebase_database/firebase_database.dart';

import 'model.dart';

class Transaction {
  final List<Model> _toSave = List<Model>();
  final List<Model> _toDelete = List<Model>();

  void save(Model m) => _toSave.add(m);
  void delete(Model m) {
    assert(m.key != null);
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
    _toDelete.forEach((m) => updates['${m.rootPath}/${m.key}'] = null);
    return root.update(updates);
  }
}
