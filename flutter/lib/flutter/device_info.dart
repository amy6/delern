import 'dart:async';
import 'dart:io';

import 'package:delern_flutter/remote/error_reporting.dart';
import 'package:device_info/device_info.dart';
import 'package:flutter/material.dart';
import 'package:meta/meta.dart';

class DeviceInfo {
  final String userFriendlyName;
  final int sdk;

  static DeviceInfo _instance;

  DeviceInfo._({@required this.userFriendlyName, @required this.sdk});

  static Future<DeviceInfo> getDeviceInfo() async {
    if (_instance == null) {
      _instance = DeviceInfo._(
          userFriendlyName: Platform.operatingSystem,
          sdk: Platform.numberOfProcessors);

      try {
        var deviceInfo = DeviceInfoPlugin();
        if (Platform.isAndroid) {
          var info = await deviceInfo.androidInfo;
          _instance = DeviceInfo._(
              userFriendlyName: '${info.manufacturer} ${info.model}',
              sdk: info.version.sdkInt);
        } else if (Platform.isIOS) {
          var info = await deviceInfo.iosInfo;
          _instance =
              DeviceInfo._(userFriendlyName: info.model.toString(), sdk: 0);
        }
      } catch (e, stackTrace) {
        // Can't report directly because ErrorReporting uses this method.
        scheduleMicrotask(() {
          ErrorReporting.report('getDeviceInfo', e, stackTrace);
        });
      }
    }
    return _instance;
  }

  /// Method check whether device is small. Found by trial and error
  static bool isDeviceSmall(BuildContext context) {
    final mediaData = MediaQuery.of(context);
    if (mediaData.orientation == Orientation.portrait) {
      return mediaData.size.height < 620;
    }
    // In landscape device is small to prevent overlapping
    return true;
  }
}
