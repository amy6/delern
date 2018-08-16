import 'package:flutter/material.dart';
import 'package:flutter_markdown/flutter_markdown.dart';

import '../../flutter/localization.dart';
import '../../flutter/styles.dart';

class SupportDevelopment extends StatelessWidget {
  final TextStyle linkStyle = AppStyles.linkText;

  static const String _markdownData = """
### Please tell us what we can do to make your experience with Delern better!

### If you have any questions or suggestions please contact us:
### [delern@dasfoo.org](mailto:delern@dasfoo.org) 

### Follow latest news on:

- ### [Facebook](https://fb.me/das.delern) 
- ### [Twitter](https://twitter.com/dasdelern)
- ### [Google+](https://plus.google.com/communities/104603840044649051798)
- ### [VK](https://vk.com/delern)

### To see the source code for this app, please visit the [Delern guthub repo](https://github.com/dasfoo/delern).
""";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
            title: Text(AppLocalizations
                .of(context)
                .navigationDrawerSupportDevelopment)),
        body: Builder(
          builder: (context) => Markdown(data: _markdownData),
        ));
  }
}
