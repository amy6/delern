import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:package_info/package_info.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';
import '../../flutter/user_messages.dart';
import '../../remote/sign_in.dart';
import '../../views/support_dev/support_development.dart';
import '../helpers/send_invite.dart';
import '../helpers/sign_in.dart';

class NavigationDrawer extends StatefulWidget {
  @override
  _NavigationDrawerState createState() => _NavigationDrawerState();
}

class _NavigationDrawerState extends State<NavigationDrawer> {
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
    var user = CurrentUserWidget.of(context).user;
    return Drawer(
        child: Column(
      children: <Widget>[
        UserAccountsDrawerHeader(
          accountName: Text(user.displayName),
          accountEmail: Text(user.email),
          currentAccountPicture: CircleAvatar(
            backgroundImage: NetworkImage(user.photoUrl),
          ),
        ),
        ListTile(
          leading: Icon(Icons.perm_identity),
          title: Text(AppLocalizations.of(context).navigationDrawerSignOut),
          onTap: () {
            signOut();
            Navigator.pop(context);
          },
        ),
        Divider(height: 1.0),
        ListTile(
          title: Text(
            AppLocalizations.of(context).navigationDrawerCommunicateGroup,
            style: AppStyles.navigationDrawerGroupText,
          ),
        ),
        ListTile(
          leading: Icon(Icons.contact_mail),
          title:
              Text(AppLocalizations.of(context).navigationDrawerInviteFriends),
          onTap: () {
            sendInvite(context);
            Navigator.pop(context);
          },
        ),
        ListTile(
          leading: Icon(Icons.live_help),
          title: Text(AppLocalizations.of(context).navigationDrawerContactUs),
          onTap: () async {
            Navigator.pop(context);
            try {
              await launch("mailto:delern@dasfoo.org?subject=Delern%20Support",
                  forceSafariVC: false);
            } catch (e, stackTrace) {
              UserMessages.showError(() => Scaffold.of(context),
                  AppLocalizations.of(context).installEmailApp, stackTrace);
            }
          },
        ),
        ListTile(
          leading: Icon(Icons.developer_board),
          title: Text(
              AppLocalizations.of(context).navigationDrawerSupportDevelopment),
          onTap: () {
            Navigator.pop(context);
            Navigator.push(context,
                MaterialPageRoute(builder: (context) => SupportDevelopment()));
          },
        ),
        Divider(
          height: 1.0,
        ),
        AboutListTile(
          icon: Icon(Icons.perm_device_information),
          child: Text(AppLocalizations.of(context).navigationDrawerAbout),
          applicationIcon: Image.asset('images/ic_launcher.png'),
          applicationVersion: versionCode,
          applicationLegalese: 'GNU General Public License v3.0',
        ),
      ],
    ));
  }
}
