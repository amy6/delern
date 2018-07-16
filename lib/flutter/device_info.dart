import 'dart:async';
import 'dart:io';

import 'package:device_info/device_info.dart';

import '../remote/error_reporting.dart';

class DeviceInfo {
  static Future<String> getDeviceManufactureName() async {
    try {
      var deviceInfo = DeviceInfoPlugin();
      if (Platform.isAndroid) {
        var info = await deviceInfo.androidInfo;
        return '${info.manufacturer} ${info.model}';
      } else if (Platform.isIOS) {
        var info = await deviceInfo.iosInfo;
        return info.model.toString();
      }
    } catch (e, stackTrace) {
      reportError('DeviceInfo', e, stackTrace);
    }
    return 'Unknown';
  }
}
