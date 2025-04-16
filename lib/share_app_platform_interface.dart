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

  /// Share the APK file of the app with the given package name.
  ///
  /// [packageName] is the package name of the app whose APK file will be shared.
  /// [apkFileName] is the name of the APK file to be shared.
  ///
  /// Returns true if the APK file was shared successfully, false otherwise.
  ///
  Future<bool> shareAPK(String packageName, {String apkFileName = ''}) {
    throw UnimplementedError('shareAPK() has not been implemented.');
  }

  /// Share the app URL with the given title and URL. It gets the icon from the app.
  ///
  /// [title] is the title of the app to be shared.
  /// [url] is the URL of the app to be shared.
  ///
  /// Returns true if the app URL was shared successfully, false otherwise.
  ///
  Future<bool> shareAppUrl(String title, String url) {
    throw UnimplementedError('shareAppUrl() has not been implemented.');
  }
}
