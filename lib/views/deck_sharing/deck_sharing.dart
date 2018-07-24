import 'dart:async';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/user_messages.dart';
import '../../models/deck.dart';
import '../../models/deck_access.dart';
import '../../remote/user_lookup.dart';
import '../../view_models/deck_access_view_model.dart';
import '../helpers/observing_animated_list.dart';
import '../helpers/save_updates_dialog.dart';
import '../helpers/send_invite.dart';
import 'deck_access_dropdown.dart';

class DeckSharingPage extends StatefulWidget {
  final Deck _deck;

  DeckSharingPage(this._deck);

  @override
  State<StatefulWidget> createState() => new _DeckSharingState();
}

class _DeckSharingState extends State<DeckSharingPage> {
  final TextEditingController _textController = new TextEditingController();
  AccessType _accessValue = AccessType.write;

  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: new AppBar(
          title: new Text(widget._deck.name),
          actions: <Widget>[
            new IconButton(
                icon: new Icon(Icons.send),
                onPressed:
                    _isEmailCorrect() ? () => _shareDeck(_accessValue) : null)
          ],
        ),
        body: new Column(
          children: <Widget>[
            new Padding(
              padding: const EdgeInsets.only(left: 8.0, top: 8.0),
              child: new Row(
                children: <Widget>[
                  new Text(AppLocalizations.of(context).peopleLabel),
                ],
              ),
            ),
            _sharingEmail(),
            new Expanded(child: new DeckUsersWidget(widget._deck)),
          ],
        ),
      );

  Widget _sharingEmail() {
    return new ListTile(
      title: new TextField(
        controller: _textController,
        onChanged: (String text) {
          setState(() {});
        },
        decoration: new InputDecoration(
          hintText: AppLocalizations.of(context).emailAddressHint,
        ),
      ),
      trailing: new DeckAccessDropdown(
        value: _accessValue,
        filter: (AccessType access) =>
            (access != AccessType.owner && access != null),
        valueChanged: (AccessType access) => setState(() {
              _accessValue = access;
            }),
      ),
    );
  }

  bool _isEmailCorrect() {
    return _textController.text.contains('@');
  }

  //TODO(ksheremet): Disable sharing button
  Future<void> _shareDeck(AccessType deckAccess) async {
    print("Share deck: " + deckAccess.toString() + _textController.text);
    try {
      String uid = await userLookup(_textController.text.toString());
      if (uid == null) {
        if (await _inviteUser()) {
          setState(() {
            _textController.clear();
          });
        }
        // Do not clear the field if user didn't send an invite.
        // Maybe user made a typo in email address and needs to correct it.
      } else {
        //TODO(ksheremet): Share deck
      }
    } catch (e, stackTrace) {
      // TODO(ksheremet): Scaffold.of is unavailable here
      UserMessages.showError(Scaffold.of(context), e, stackTrace);
    }
  }

  Future<bool> _inviteUser() async {
    var locale = AppLocalizations.of(context);
    var inviteUser = await showSaveUpdatesDialog(
        context: context,
        changesQuestion: locale.appNotInstalledSharingDeck,
        yesAnswer: locale.send,
        noAnswer: locale.cancel);
    if (inviteUser) {
      await sendInvite(context);
      return true;
    }
    return false;
  }
}

class DeckUsersWidget extends StatefulWidget {
  final Deck _deck;

  DeckUsersWidget(this._deck);

  @override
  State<StatefulWidget> createState() => _DeckUsersState();
}

class _DeckUsersState extends State<DeckUsersWidget> {
  DeckAccessesViewModel _deckAccessesViewModel;
  bool _active = false;

  @override
  void initState() {
    _deckAccessesViewModel = new DeckAccessesViewModel(widget._deck);
    _deckAccessesViewModel.deckAccesses.comparator = (a, b) {
      if (a.access == b.access) {
        return (a.user?.name ?? '').compareTo(b.user?.name ?? '');
      }

      switch (a.access) {
        case AccessType.owner:
          return -1;
        case AccessType.write:
          return b.access == AccessType.owner ? 1 : -1;
        default:
          return 1;
      }
    };
    super.initState();
  }

  @override
  void deactivate() {
    _deckAccessesViewModel.deactivate();
    _active = false;
    super.deactivate();
  }

  @override
  void dispose() {
    super.dispose();
    _deckAccessesViewModel.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!_active) {
      _deckAccessesViewModel.activate();
      _active = true;
    }
    return new Column(
      children: <Widget>[
        new Padding(
          padding: const EdgeInsets.only(left: 8.0, top: 8.0),
          child: new Row(
            children: <Widget>[
              new Text(AppLocalizations.of(context).whoHasAccessLabel),
            ],
          ),
        ),
        new Expanded(
          child: new ObservingAnimatedList(
              list: _deckAccessesViewModel.deckAccesses,
              itemBuilder: (context, item, animation, index) =>
                  new SizeTransition(
                    child: _buildUserAccessInfo(item),
                    sizeFactor: animation,
                  )),
        ),
      ],
    );
  }

  Widget _buildUserAccessInfo(DeckAccessViewModel accessViewModel) {
    Function filter;
    if (accessViewModel.access == AccessType.owner) {
      filter = (AccessType access) => access == AccessType.owner;
    } else {
      filter = (AccessType access) => access != AccessType.owner;
    }

    return new ListTile(
      leading: (accessViewModel.user == null)
          ? null
          : new CircleAvatar(
              backgroundImage: new NetworkImage(accessViewModel.user.photoUrl),
            ),
      title: (accessViewModel.user == null)
          ? new Center(
              child: new CircularProgressIndicator(),
            )
          : new Text(accessViewModel.user.name),
      trailing: new DeckAccessDropdown(
        value: accessViewModel.access,
        filter: filter,
        valueChanged: (AccessType access) => setState(() {
              // TODO(ksheremet): Save new access to deck.
            }),
      ),
    );
  }
}
