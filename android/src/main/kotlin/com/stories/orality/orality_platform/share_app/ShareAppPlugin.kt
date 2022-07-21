package com.stories.orality.orality_platform.share_app

import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import android.net.Uri
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.HashMap




/** ShareAppPlugin */
class ShareAppPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  private lateinit var context: Context
  private lateinit var channel : MethodChannel
  private var packageName : String? = ""
  private var apkFileName : String? = ""

  // Register our plugin to receive messages with Flutter
  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "orality_platform.share_app/share")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "shareAPK") {
      val data: HashMap<String, String> = call.arguments as HashMap<String, String>
      packageName = data.get("packageName")
      apkFileName = data.get("apkFileName")
      if (packageName.isNullOrEmpty()) {
        result.error("ERROR", "Package name needs to be valid.", null)
        return
      }
      val appApkUri: Uri? = getAppApk()
      if (appApkUri == null) {
        result.error("ERROR", "APK URI Missing", null)
        return
      }
      shareApplication(appApkUri)
      result.success(true)
    } else {
      result.notImplemented()
    }
  }

  // Unregister our plugin
  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  // This implementation is based on the instructions in https://developer.android.com/training/secure-file-sharing
  // with additional references to the source of https://pub.dev/packages/apk_admin to understand
  // how to find and share the app apk file itself. It seems to work without any errors or warnings,
  // but I don't fully understand how. In particular, it seems like sharing files outside of the app's own
  // directory is not explicitly supported, as I couldn't find any official documentation of the `root-path`
  // path type in the FileProvider configuration.
  //
  // However, that's what the apk_admin library uses, and it seems like the only way to achieve the
  // desired app sharing.
  private fun shareApplication(appApkUri: Uri) {
    val shareApp = Intent(Intent.ACTION_SEND)
    shareApp.setDataAndType(appApkUri, "*/*")
    shareApp.putExtra(Intent.EXTRA_STREAM, appApkUri)
    shareApp.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(
      Intent.createChooser(shareApp, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
  }

  private fun getAppApk(): Uri? {
    val packageManager: PackageManager = context.getPackageManager()
    val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName!!, 0)
    val original = File(packageInfo.applicationInfo.publicSourceDir)
    if (apkFileName.isNullOrEmpty()) {
      return getUriFromFile(original, context)
    }

    context.openFileOutput(apkFileName, Context.MODE_PRIVATE).use {
      it.write(original.readBytes())
    }
    val final = File(context.filesDir, apkFileName)
    return getUriFromFile(final, context)
  }

  private fun getUriFromFile(file: File, context: Context): Uri? {
    val fileUri: Uri
    fileUri = if (Build.VERSION.SDK_INT >= 24) {
      // The authority string must match a FileProvider definition in the app's manifest.
      // See https://developer.android.com/reference/androidx/core/content/FileProvider for
      // more details.
      FileProvider.getUriForFile(context, "$packageName.provider", file)
    } else {
      Uri.fromFile(file)
    }
    return fileUri
  }

}
