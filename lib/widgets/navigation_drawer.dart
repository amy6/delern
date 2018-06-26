import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:package_info/package_info.dart';

import '../flutter/localization.dart';
import '../widgets/send_invite.dart';

class NavigationDrawer extends StatefulWidget {
  final FirebaseUser user;
  final Function signOutCallback;
  NavigationDrawer(this.user, this.signOutCallback);

  @override
  _NavDrawerState createState() => new _NavDrawerState();
}

class _NavDrawerState extends State<NavigationDrawer> {
  String versionCode;

  @override
  void initState() {
    super.initState();
    PackageInfo.fromPlatform().then((packageInfo) => setState(() {
          versionCode = packageInfo.version;
        }));
  }

  @override
  Widget build(BuildContext context) {
    return new Drawer(
        child: new Column(
      children: <Widget>[
        new UserAccountsDrawerHeader(
          accountName: new Text(widget.user.displayName),
          accountEmail: new Text(widget.user.email),
          currentAccountPicture: new CircleAvatar(
            backgroundImage: new NetworkImage(widget.user.photoUrl),
          ),
        ),
        new ListTile(
          leading: new Icon(Icons.perm_identity),
          title: new Text(AppLocalizations.of(context).navigationDrawerSignOut),
          onTap: () {
            widget.signOutCallback();
            Navigator.pop(context);
          },
        ),
        new Divider(height: 1.0),
        new ListTile(
          title: new Text(
            AppLocalizations.of(context).navigationDrawerCommunicateGroup,
            style: new TextStyle(
              fontWeight: FontWeight.w600,
              color: Colors.grey[600],
            ),
          ),
        ),
        new ListTile(
          leading: new Icon(Icons.contact_mail),
          title: new Text(
              AppLocalizations.of(context).navigationDrawerInviteFriends),
          onTap: () {
            sendInvite(context);
            Navigator.pop(context);
          },
        ),
        new ListTile(
          leading: new Icon(Icons.live_help),
          title:
              new Text(AppLocalizations.of(context).navigationDrawerContactUs),
          onTap: () {
            Navigator.pop(context);
          },
        ),
        new ListTile(
          leading: new Icon(Icons.attach_money),
          title: new Text(
              AppLocalizations.of(context).navigationDrawerSupportDevelopment),
          onTap: () {
            Navigator.pop(context);
          },
        ),
        new Divider(
          height: 1.0,
        ),
        new AboutListTile(
          icon: new Icon(Icons.perm_device_information),
          child: new Text(AppLocalizations.of(context).navigationDrawerAbout),
          applicationIcon: new Image.asset('images/ic_launcher.png'),
          applicationVersion: versionCode,
          applicationLegalese: 'GNU General Public License v3.0',
        ),
      ],
    ));
  }
}
