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
  State<StatefulWidget> createState() => _DeckSharingState();
}

class _DeckSharingState extends State<DeckSharingPage> {
  final TextEditingController _textController = TextEditingController();
  AccessType _accessValue = AccessType.write;

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          title: Text(widget._deck.name),
          actions: <Widget>[
            IconButton(
                icon: Icon(Icons.send),
                onPressed:
                    _isEmailCorrect() ? () => _shareDeck(_accessValue) : null)
          ],
        ),
        body: Column(
          children: <Widget>[
            Padding(
              padding: EdgeInsets.only(left: 8.0, top: 8.0),
              child: Row(
                children: <Widget>[
                  Text(AppLocalizations.of(context).peopleLabel),
                ],
              ),
            ),
            _sharingEmail(),
            Expanded(child: DeckUsersWidget(widget._deck)),
          ],
        ),
      );

  Widget _sharingEmail() => ListTile(
        title: TextField(
          controller: _textController,
          onChanged: (String text) {
            setState(() {});
          },
          decoration: InputDecoration(
            hintText: AppLocalizations.of(context).emailAddressHint,
          ),
        ),
        trailing: DeckAccessDropdown(
          value: _accessValue,
          filter: (AccessType access) =>
              (access != AccessType.owner && access != null),
          valueChanged: (AccessType access) => setState(() {
                _accessValue = access;
              }),
        ),
      );

  bool _isEmailCorrect() => _textController.text.contains('@');

  // TODO(ksheremet): Disable sharing button
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
        await DeckAccessesViewModel.shareDeck(
            DeckAccess(deck: widget._deck, uid: uid, access: deckAccess));
      }
    } catch (e, stackTrace) {
      // TODO(ksheremet): Scaffold.of is unavailable here
      UserMessages.showError(() => Scaffold.of(context), e, stackTrace);
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
    _deckAccessesViewModel = DeckAccessesViewModel(widget._deck);
    _deckAccessesViewModel.deckAccesses.comparator = (a, b) {
      if (a.deckAccess.access == b.deckAccess.access) {
        return (a.user?.name ?? '').compareTo(b.user?.name ?? '');
      }

      switch (a.deckAccess.access) {
        case AccessType.owner:
          return -1;
        case AccessType.write:
          return b.deckAccess.access == AccessType.owner ? 1 : -1;
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
    return Column(
      children: <Widget>[
        Padding(
          padding: EdgeInsets.only(left: 8.0, top: 8.0),
          child: Row(
            children: <Widget>[
              Text(AppLocalizations.of(context).whoHasAccessLabel),
            ],
          ),
        ),
        Expanded(
          child: ObservingAnimatedList(
              list: _deckAccessesViewModel.deckAccesses,
              itemBuilder: (context, item, animation, index) => SizeTransition(
                    child: _buildUserAccessInfo(item),
                    sizeFactor: animation,
                  )),
        ),
      ],
    );
  }

  Widget _buildUserAccessInfo(DeckAccessViewModel accessViewModel) {
    Function filter;
    if (accessViewModel.deckAccess.access == AccessType.owner) {
      filter = (AccessType access) => access == AccessType.owner;
    } else {
      filter = (AccessType access) => access != AccessType.owner;
    }

    return ListTile(
      leading: (accessViewModel.user == null)
          ? null
          : CircleAvatar(
              backgroundImage: NetworkImage(accessViewModel.user.photoUrl),
            ),
      title: (accessViewModel.user == null)
          ? Center(
              // TODO(ksheremet): use the shared ProgressIndicator
              child: CircularProgressIndicator(),
            )
          : Text(accessViewModel.user.name),
      trailing: DeckAccessDropdown(
        value: accessViewModel.deckAccess.access,
        filter: filter,
        valueChanged: (AccessType access) => setState(() {
              DeckAccessesViewModel.shareDeck(DeckAccess(
                  deck: _deckAccessesViewModel.deck,
                  uid: accessViewModel.deckAccess.uid,
                  access: access));
            }),
      ),
    );
  }
}
