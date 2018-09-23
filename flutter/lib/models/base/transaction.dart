import 'dart:async';

import 'package:firebase_database/firebase_database.dart';

import '../../remote/error_reporting.dart';
import 'model.dart';

class Transaction {
  final List<Model> _toSave = <Model>[];
  final List<Model> _toDelete = <Model>[];

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

  Future<void> commit() async {
    var root = FirebaseDatabase.instance.reference();
    var updates = <String, dynamic>{};
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

    // Firebase update() does not return until it gets response from the server.
    var updateFuture = root.update(updates);

    if (!_isOnline) {
      updateFuture.catchError((error, stackTrace) => ErrorReporting.report(
          'Applying $updates in background', error, stackTrace));
      return;
    }

    try {
      await updateFuture;
    } catch (error, stackTrace) {
      ErrorReporting.report('Applying $updates', error, stackTrace);
      rethrow;
    }
  }
}
