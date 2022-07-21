import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'share_app_platform_interface.dart';

/// An implementation of [ShareAppPlatform] that uses method channels.
class MethodChannelShareApp extends ShareAppPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('orality_platform.share_app/share');

  @override
  Future<bool> shareAPK(String packageName, {String apkFileName = ''}) async {
    final data = <String, String> {
      'packageName': packageName,
      'apkFileName': apkFileName,
    };
    return await methodChannel.invokeMethod('shareAPK', data);
  }
}
