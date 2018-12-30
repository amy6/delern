import 'package:delern_flutter/view_models/scheduled_cards_bloc.dart';
import 'package:delern_flutter/views/helpers/sign_in_widget.dart';
import 'package:flutter/material.dart';

class ScheduledCardsBlocHolderWidget extends StatefulWidget {
  final Widget child;

  const ScheduledCardsBlocHolderWidget({this.child});

  @override
  State<StatefulWidget> createState() => _ScheduledCardsBlocHolderWidgetState();
}

class _ScheduledCardsBlocHolderWidgetState
    extends State<ScheduledCardsBlocHolderWidget> {
  ScheduledCardsBloc _bloc;

  @override
  Widget build(BuildContext context) {
    final uid = CurrentUserWidget.of(context).user.uid;
    if (_bloc == null || _bloc.uid != uid) {
      _bloc?.dispose();
      _bloc = ScheduledCardsBloc(uid);
    }

    return ScheduledCardsBlocWidget(bloc: _bloc, child: widget.child);
  }
}

class ScheduledCardsBlocWidget extends InheritedWidget {
  final ScheduledCardsBloc bloc;

  static ScheduledCardsBlocWidget of(BuildContext context) =>
      context.inheritFromWidgetOfExactType(ScheduledCardsBlocWidget);

  const ScheduledCardsBlocWidget({@required this.bloc, Key key, Widget child})
      : assert(bloc != null),
        super(key: key, child: child);

  @override
  bool updateShouldNotify(ScheduledCardsBlocWidget oldWidget) =>
      // bloc instance holds stream subscriptions. If it changes (normally
      // because of the change of uid), dependants have to rebuild.
      bloc != oldWidget.bloc;
}
