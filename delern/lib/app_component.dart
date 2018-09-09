import 'package:angular/angular.dart';
import 'package:angular_components/angular_components.dart';
import 'package:firebase/firebase.dart';
import 'package:angular_router/angular_router.dart';

import 'src/add_card/add_card_component.dart';

@Component(
  selector: 'my-app',
  styleUrls: [
    'package:angular_components/app_layout/layout.scss.css',
    'app_component.css',
  ],
  templateUrl: 'app_component.html',
  directives: [AddCardComponent, materialDirectives],
  providers: [materialProviders],
)
class AppComponent {
  final String title = 'Delern - Learn easy';

  User get currentUser => auth().currentUser;

  void signOut() => auth().signOut();
}
