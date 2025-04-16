# Share App Flutter Plugin

A new Flutter plugin for sharing your app with others.  Currently only supports Android.

## Installation

Here are instructions for installing the plugin.  Remember to add this repository as a dependency in your pubspec.yaml file, and run `flutter pub get`.

### Android

In order to use this plugin, you need to setup a `FileProvider` which will give access to the files for sharing with other applications.

First, add this to AndroidManifest.xml:

```aidl
<application>
...
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths"/>
</provider>
</application>
...
```

Then add the following to `res/xml/file_paths.xml`:

```aidl
<paths>
    <root-path name="root" path="." />
    <cache-path name="cache" path="." />
    <files-path name="files" path="." />
</paths>
```

For the shareAppUrl() method, it uses the file **android/app/src/main/res/mipmap-*/ic_launcher.png** for the image in the sharesheet. Make sure the Flutter app is using the correct image in all the mipmap sizes.

### iOS

Not supported currently.

## Usage: Sharing the APK

To use the plugin, simply import the library with:

```aidl
import 'package:share_app/share_app.dart';
```

Next, initialize and call the method you want to trigger.  The method is asynchronous so wrap in an async function:

```aidl
Future<void> _shareAPK() async {
  final sharePlugin = ShareApp();
  await sharePlugin.shareAPK("Your App Name");
}
```

We also provide an optional parameter to set the name for the file being share.  Simply call the shareAPK method like this:

```aidl
await sharePlugin.shareAPK("Your App Name", apkFileName: "Your APK File Name");
```

## Usage: Sharing the App URL

We also provide a customized way to share an App URL. It will use ic_launcher.png with a title and url in the sharesheet. In order to support iOS, we recommend installing the [Share +](https://pub.dev/packages/share_plus) package. Then you can use the following code to handle sharing the App URL in both iOS and Android:

```aidl
import 'dart:io';
import 'package:share_app/share_app.dart';
import 'package:share_plus/share_plus.dart';
...
Future<void> shareAppUrl(String appName, String url) async {
  if (!Platform.isAndroid) {
    final uri = Uri.parse(url);
    await Share.shareUri(uri);
    return;
  }
  try {
    /// Try to share the URL with a preview. share_plus does not support this currently.
    final sharePlugin = ShareApp();
    sharePlugin.shareAppUrl(appName, url);
  } catch (e) {
    /// Fallback to the normal share without preview.
    final uri = Uri.parse(url);
    Share.shareUri(uri);
  }
}
```
