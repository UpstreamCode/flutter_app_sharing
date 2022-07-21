import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'share_app_method_channel.dart';

abstract class ShareAppPlatform extends PlatformInterface {
  /// Constructs a ShareAppPlatform.
  ShareAppPlatform() : super(token: _token);

  static final Object _token = Object();

  static ShareAppPlatform _instance = MethodChannelShareApp();

  /// The default instance of [ShareAppPlatform] to use.
  ///
  /// Defaults to [MethodChannelShareApp].
  static ShareAppPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [ShareAppPlatform] when
  /// they register themselves.
  static set instance(ShareAppPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool> shareAPK(String packageName, {String apkFileName = ''}) {
    throw UnimplementedError('share() has not been implemented.');
  }
}
