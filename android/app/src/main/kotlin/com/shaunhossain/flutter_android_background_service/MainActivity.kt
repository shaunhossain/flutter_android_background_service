package com.shaunhossain.flutter_android_background_service


import android.content.Context
import android.content.Intent
import androidx.core.content.ContentProviderCompat.requireContext
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private val tag = "MainActivity"
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            "backgroundservice"
        ).setMethodCallHandler { call: MethodCall, result: MethodChannel.Result ->
            if (call.method == "start") {
                Intent(this, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    context.startService(this)
                }
            }

            if (call.method == "stop") {
                Intent(this, LocationService::class.java).apply {
                    action = LocationService.ACTION_STOP
                    context.stopService(this)
                }
            }
            result.success(1)
        }
    }
}
