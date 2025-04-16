import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'share_app_platform_interface.dart';

/// An implementation of [ShareAppPlatform] that uses method channels.
class MethodChannelShareApp extends ShareAppPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('orality_platform.share_app/share');

  /// Share the APK file of the app with the given package name.
  ///
  /// [packageName] is the package name of the app whose APK file will be shared.
  /// [apkFileName] is the name of the APK file to be shared.
  ///
  /// Returns true if the APK file was shared successfully, false otherwise.
  ///
  @override
  Future<bool> shareAPK(String packageName, {String apkFileName = ''}) async {
    final data = <String, String>{
      'packageName': packageName,
      'apkFileName': apkFileName,
    };
    return await methodChannel.invokeMethod('shareAPK', data);
  }

  /// Share the app URL with the given title and URL. It gets the icon from the app.
  ///
  /// [title] is the title of the app to be shared.
  /// [url] is the URL of the app to be shared.
  ///
  /// Returns true if the app URL was shared successfully, false otherwise.
  ///
  @override
  Future<bool> shareAppUrl(String title, String url) async {
    final data = <String, String>{
      'title': title,
      'url': url,
    };
    return await methodChannel.invokeMethod('shareAppUrl', data);
  }
}
