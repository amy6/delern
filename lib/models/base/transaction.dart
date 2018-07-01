import 'dart:async';

import 'package:firebase_database/firebase_database.dart';

import 'model.dart';

class Transaction {
  final List<Model> toSave = List<Model>();
  final List<Model> toDelete = List<Model>();

  void save(Model m) => toSave.add(m);
  void delete(Model m) {
    assert(m.key != null);
    toDelete.add(m);
  }

  Future<void> commit() {
    var root = FirebaseDatabase.instance.reference();
    var updates = Map<String, dynamic>();
    toSave.forEach((m) {
      if (m.key == null) {
        m.key = root.child(m.rootPath).push().key;
        updates.addAll(m.toMap(true));
      } else {
        updates.addAll(m.toMap(false));
      }
    });
    toDelete.forEach((m) => updates['${m.rootPath}/${m.key}'] = null);
    return root.update(updates);
  }
}
