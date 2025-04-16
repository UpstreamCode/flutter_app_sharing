import 'share_app_platform_interface.dart';

class ShareApp {
  Future<bool> shareAPK(String packageName, {String apkFileName = ''}) async {
    return ShareAppPlatform.instance
        .shareAPK(packageName, apkFileName: apkFileName);
  }

  Future<bool> shareAppUrl(String title, String url) async {
    return ShareAppPlatform.instance.shareAppUrl(title, url);
  }
}
