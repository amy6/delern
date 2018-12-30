import 'dart:async';

import 'package:firebase_database/firebase_database.dart';

import '../../remote/error_reporting.dart';
import 'model.dart';

class Transaction {
  final _updates = <String, dynamic>{};

  static bool _isOnline = false;
  static DatabaseReference get _root => FirebaseDatabase.instance.reference();

  static void subscribeToOnlineStatus() {
    FirebaseDatabase.instance
        .reference()
        .child('.info/connected')
        .onValue
        .listen((event) {
      _isOnline = event.snapshot.value;
    });
  }

  void save(Model m) {
    if (m.key == null) {
      m.key = _root.child(m.rootPath).push().key;
      _updates.addAll(m.toMap(true));
    } else {
      _updates.addAll(m.toMap(false));
    }
  }

  void delete(Model m) {
    assert(m.key != null, 'Attempt to delete a model without a key!');
    _updates['${m.rootPath}/${m.key}'] = null;
  }

  void deleteAll(Model m) {
    assert(
        m.key == null,
        'Attempt to delete all models with the same root, but the key is '
        'specified!');
    _updates[m.rootPath] = null;
  }

  Future<void> commit() async {
    // Firebase update() does not return until it gets response from the server.
    var updateFuture = _root.update(_updates);

    if (!_isOnline) {
      updateFuture.catchError((error, stackTrace) => ErrorReporting.report(
          'Transaction', error, stackTrace,
          extra: {'updates': _updates, 'online': false}));
      return;
    }

    try {
      await updateFuture;
    } catch (error, stackTrace) {
      ErrorReporting.report('Transaction', error, stackTrace,
          extra: {'updates': _updates, 'online': true});
      rethrow;
    }
  }
}
