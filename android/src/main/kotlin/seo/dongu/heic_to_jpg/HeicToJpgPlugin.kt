package seo.dongu.heic_to_jpg

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

/** HeicToJpgPlugin */
class HeicToJpgPlugin : FlutterPlugin, MethodCallHandler {

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        applicationContext = flutterPluginBinding.applicationContext
        val channel =
            MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "heic_to_jpg")
        channel.setMethodCallHandler(HeicToJpgPlugin());
    }

    companion object {
        var applicationContext: Context? = null
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "convert") {
            if (call.hasArgument("heicPath") && !call.argument<String>("heicPath")
                    .isNullOrEmpty()
            ) {
                val handler = Handler(Looper.getMainLooper())
                Thread {
                    var jpgPath = call.argument<String>("jpgPath")
                    if (jpgPath.isNullOrEmpty()) {
                        jpgPath =
                            "${applicationContext?.cacheDir}/${System.currentTimeMillis()}.jpg"
                    }
                    val output = convertHeicToJpeg(call.argument<String>("heicPath")!!, jpgPath)
                    handler.post {
                        if (output != null) {
                            result.success(output)
                        } else {
                            result.error("error", "output path is null", null)
                        }
                    }
                }.start()
                return
            }
            result.error("illegalArgument", "heicPath is null or Empty.", null)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    }
}
