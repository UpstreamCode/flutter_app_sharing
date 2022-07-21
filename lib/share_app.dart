import 'share_app_platform_interface.dart';

class ShareApp {
  Future<bool> shareAPK(String packageName, {String apkFileName = ''}) async {
    return ShareAppPlatform.instance.shareAPK(packageName, apkFileName: apkFileName);
  }
}
