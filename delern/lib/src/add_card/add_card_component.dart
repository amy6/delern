import 'dart:async';

import 'package:angular/angular.dart';
import 'package:angular_components/material_icon/material_icon.dart';
import 'package:angular_components/material_input/material_input.dart';
import 'package:angular_components/material_button/material_fab.dart';
import 'package:angular_components/material_checkbox/material_checkbox.dart';

import 'package:angular_components/angular_components.dart';

import 'package:firebase/firebase.dart';

import 'add_card_service.dart';

@Component(
  selector: 'add-card',
  styleUrls: ['add_card_component.css'],
  templateUrl: 'add_card_component.html',
  directives: [
    MaterialCheckboxComponent,
    MaterialFabComponent,
    MaterialIconComponent,
    materialInputDirectives,
    NgFor,
    NgIf,
  ],
  providers: [const ClassProvider(AddCardService)],
)
class AddCardComponent implements OnInit {
  final AddCardService todoListService;

  List<String> items = [];
  String newTodo = '';

  AddCardComponent(this.todoListService);

  @override
  Future<Null> ngOnInit() async {
    items = await todoListService.getTodoList();
    items.insert(0, 'Hello, ' + auth().currentUser.displayName);
  }

  void add() {
    items.add(newTodo);
    auth().signInWithPopup(new GoogleAuthProvider());
    newTodo = '';
  }

  String remove(int index) => items.removeAt(index);
}
