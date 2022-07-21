# Share App Flutter Plugin

A new Flutter plugin for sharing your app with others.  Currently only supports Android.

## Installation

Here are instructions for installing the plugin.  Remember to add this repository as a dependency in your pubspec.yaml file, and run `flutter pub get`.

### Android

Please note that if you want to share files, you need to setup a `FileProvider` which will give access to the files for sharing with other applications.

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
    <root-path name="root" path="." />`
</paths>
```

### iOS

Not supported currently.

## Usage

To use the plugin, simply import the library with:

```aidl
import 'package:share_app/share_app.dart';
```

Next, initialize and call the method you want to trigger.  The method is asynchronous so wrap in an async function:

```aidl
Future<void> _shareApp() async {
  final sharePlugin = ShareApp();
  await sharePlugin.shareAPK("Your App Name");
}
```

We also provide an optional parameter to set the name for the file being share.  Simply call the shareAPK method like this:

```aidl
await sharePlugin.shareAPK("Your App Name", apkFileName: "Your APK File Name");
```
