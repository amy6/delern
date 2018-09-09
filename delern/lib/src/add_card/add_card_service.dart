import 'dart:async';

import 'package:angular/core.dart';

import 'package:firebase/firebase.dart';

/// Mock service emulating access to a to-do list stored on a server.
@Injectable()
class AddCardService {
  Future<List<String>> getTodoList() async {
    var ref = database().ref('decks').child(auth().currentUser.uid);
    var data = await ref.onValue.first;
    return data.snapshot
        .val()
        .values
        .map<String>((v) => v['name'].toString())
        .toList();
  }
}
