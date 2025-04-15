package com.stories.orality.orality_platform.share_app

import android.content.ClipData
import android.content.ContentResolver
import android.content.Intent
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
import android.os.Build
import java.io.File
import java.util.HashMap
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import java.io.OutputStream

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
      shareAppAPK(appApkUri)
      result.success(true)
    } else if (call.method == "shareAppUrl") {
      val data: HashMap<String, String> = call.arguments as HashMap<String, String>
      val title = data.get("title") as? String ?: ""
      val url = data.get("url") as? String ?: ""
      if (url.isEmpty()) {
        result.error("ERROR", "The App url is missing.", null)
        return
      }
      shareAppUrl(title, url)
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
  private fun shareAppAPK(appApkUri: Uri) {
    val shareApp = Intent(Intent.ACTION_SEND)
    shareApp.setDataAndType(appApkUri, "*/*")
    shareApp.putExtra(Intent.EXTRA_STREAM, appApkUri)
    shareApp.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(
      Intent.createChooser(shareApp, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
  }

  /**
   * Shares the app with a title and an image preview.
   *
   * @param title The title of the content to be shared.
   * @param url The URL to be shared.
   */
  private fun shareAppUrl(title: String, url: String) {
    val iconUri: Uri = getAppIconUri()
    val iconClipData: ClipData = ClipData.newUri(context.contentResolver, null, iconUri)
    val intent: Intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TITLE, "$title\n$url")
      type = "text/plain"
      clipData = iconClipData
      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
      putExtra(Intent.EXTRA_TEXT, url)
    }
    context.startActivity(
      Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
  }

  /**
   * Get the app apk file
   */
  private fun getAppApk(): Uri? {
    val packageManager: PackageManager = context.getPackageManager()
    val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName!!, 0)
    val original = File(packageInfo.applicationInfo.publicSourceDir)
    if (apkFileName.isNullOrEmpty()) {
      return getUriFromFile(original)
    }

    context.openFileOutput(apkFileName, Context.MODE_PRIVATE).use {
      it.write(original.readBytes())
    }
    val final = File(context.filesDir, apkFileName)
    return getUriFromFile(final)
  }

  /**
   * Get the app icon and save it to the cached files
   */
  private fun getAppIconUri(): Uri {
    // Dynamically get the resource ID for ic_launcher
    val resourceId = context.resources.getIdentifier("ic_launcher", "mipmap", context.packageName)
    if (resourceId == 0) {
        throw IllegalStateException("Could not find launcher icon in the host app")
    }
    // Get the drawable resource
    val drawable = context.resources.getDrawable(resourceId, context.theme)
        ?: throw IllegalStateException("Could not load launcher icon")
    // Convert drawable to bitmap
    val bitmap = when (drawable) {
      is BitmapDrawable -> drawable.bitmap
      else -> {
        val bitmap = Bitmap.createBitmap(
          drawable.intrinsicWidth,
          drawable.intrinsicHeight,
          Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
      }
    }

    // Create a file in cache directory
    val file = File(context.cacheDir, "icon.png")
    file.outputStream().use { out: OutputStream ->
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    // Get content URI using FileProvider
    return getUriFromFile(file)
  }

  /**
   * Get the URI from a provided file
   *
   * @param file The file to retrieve the URI from
   *
   * @return The URI
   */
  private fun getUriFromFile(file: File): Uri {
    return if (Build.VERSION.SDK_INT >= 24) {
      FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    } else {
      Uri.fromFile(file)
    } ?: throw IllegalStateException("Failed to create URI from file: ${file.absolutePath}")
  }

}
