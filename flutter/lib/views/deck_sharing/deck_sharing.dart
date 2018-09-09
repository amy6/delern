import 'dart:async';

import 'package:flutter/material.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';
import '../../models/deck.dart';
import '../../models/deck_access.dart';
import '../../remote/user_lookup.dart';
import '../../view_models/deck_access_view_model.dart';
import '../../views/helpers/slow_operation_widget.dart';
import '../helpers/observing_animated_list.dart';
import '../helpers/progress_indicator.dart';
import '../helpers/save_updates_dialog.dart';
import '../helpers/send_invite.dart';
import 'deck_access_dropdown.dart';

class DeckSharingPage extends StatefulWidget {
  final Deck _deck;

  const DeckSharingPage(this._deck);

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
            Builder(
              builder: (context) => SlowOperationWidget(
                  (cb) => IconButton(
                      icon: Icon(Icons.send),
                      onPressed: _isEmailCorrect() ? cb : null),
                  () => _shareDeck(_accessValue, context)),
            )
          ],
        ),
        body: Column(
          children: <Widget>[
            Padding(
              padding: EdgeInsets.only(left: 8.0, top: 8.0),
              child: Row(
                children: <Widget>[
                  Text(
                    AppLocalizations.of(context).peopleLabel,
                    style: AppStyles.secondaryText,
                  ),
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
          onChanged: (text) {
            setState(() {});
          },
          style: AppStyles.primaryText,
          decoration: InputDecoration(
            hintText: AppLocalizations.of(context).emailAddressHint,
          ),
        ),
        trailing: DeckAccessDropdown(
          value: _accessValue,
          filter: (access) => access != AccessType.owner && access != null,
          valueChanged: (access) => setState(() {
                _accessValue = access;
              }),
        ),
      );

  bool _isEmailCorrect() => _textController.text.contains('@');

  Future<void> _shareDeck(AccessType deckAccess, BuildContext context) async {
    print('Share deck: $deckAccess: ${_textController.text}');
    try {
      var uid = await userLookup(_textController.text.toString());
      if (uid == null) {
        if (await _inviteUser()) {
          setState(_textController.clear);
        }
        // Do not clear the field if user didn't send an invite.
        // Maybe user made a typo in email address and needs to correct it.
      } else {
        await DeckAccessesViewModel.shareDeck(
            DeckAccess(deck: widget._deck, uid: uid, access: deckAccess));
      }
    } catch (e, stackTrace) {
      UserMessages.showError(() => Scaffold.of(context), e, stackTrace);
    }
  }

  Future<bool> _inviteUser() async {
    var locale = AppLocalizations.of(context);
    var inviteUser = await showSaveUpdatesDialog(
        context: context,
        changesQuestion: locale.appNotInstalledSharingDeck,
        yesAnswer: locale.send,
        noAnswer: MaterialLocalizations.of(context).cancelButtonLabel);
    if (inviteUser) {
      await sendInvite(context);
      return true;
    }
    return false;
  }
}

class DeckUsersWidget extends StatefulWidget {
  final Deck _deck;

  const DeckUsersWidget(this._deck);

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
              Text(
                AppLocalizations.of(context).whoHasAccessLabel,
                style: AppStyles.secondaryText,
              ),
            ],
          ),
        ),
        Expanded(
          child: ObservingAnimatedList(
            list: _deckAccessesViewModel.deckAccesses,
            itemBuilder: (context, item, animation, index) => SizeTransition(
                  child: _buildUserAccessInfo(item),
                  sizeFactor: animation,
                ),
            emptyListUserMessage:
                AppLocalizations.of(context).emptyUserSharingList,
          ),
        ),
      ],
    );
  }

  Widget _buildUserAccessInfo(DeckAccessViewModel accessViewModel) {
    Function filter;
    if (accessViewModel.deckAccess.access == AccessType.owner) {
      filter = (access) => access == AccessType.owner;
    } else {
      filter = (access) => access != AccessType.owner;
    }

    return ListTile(
      leading: (accessViewModel.user == null)
          ? null
          : CircleAvatar(
              backgroundImage: NetworkImage(accessViewModel.user.photoUrl),
            ),
      title: (accessViewModel.user == null)
          ? HelperProgressIndicator()
          : Text(
              accessViewModel.user.name,
              style: AppStyles.primaryText,
            ),
      trailing: DeckAccessDropdown(
        value: accessViewModel.deckAccess.access,
        filter: filter,
        valueChanged: (access) => setState(() {
              DeckAccessesViewModel.shareDeck(DeckAccess(
                  deck: _deckAccessesViewModel.deck,
                  uid: accessViewModel.deckAccess.uid,
                  access: access));
            }),
      ),
    );
  }
}
