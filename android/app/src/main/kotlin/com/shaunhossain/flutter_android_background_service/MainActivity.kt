package com.shaunhossain.flutter_android_background_service

import android.util.Log
import android.widget.Toast
import com.barikoi.barikoitrace.BarikoiTrace
import com.barikoi.barikoitrace.TraceMode
import com.barikoi.barikoitrace.callback.BarikoiTraceUserCallback
import com.barikoi.barikoitrace.models.BarikoiTraceError
import com.barikoi.barikoitrace.models.BarikoiTraceUser
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
            BarikoiTrace.initialize(this@MainActivity, resources.getString(R.string.api_key))
            if (call.method == "start") {
                val userName = call.argument("name") ?: "Not Found"
                val userEmail = call.argument("email") ?: "Not Found"
                val userPhone = call.argument("phone") ?: "Not Found"
                BarikoiTrace.setOrCreateUser(
                    userName,
                    userEmail,
                    userPhone,
                    object : BarikoiTraceUserCallback {
                        override fun onFailure(barikoiError: BarikoiTraceError) {
                            Log.d("Login trace error", barikoiError.message)
                        }

                        override fun onSuccess(traceUser: BarikoiTraceUser) {
                            Log.d("Login", traceUser.userId)
                            startTracking()
                        }
                    })
            }

            if (call.method == "stop") {
                BarikoiTrace.stopTracking()
            }
            result.success(1)
        }
    }

    private fun startTracking() {
        //to start the location tracking
        if (BarikoiTrace.isBatteryOptimizationEnabled()) {
            Log.d("Login", "is batteryOptimized: " + BarikoiTrace.isBatteryOptimizationEnabled())
            BarikoiTrace.requestDisableBatteryOptimization(applicationContext)
        }
        if (BarikoiTrace.isLocationTracking()) {
            Log.d("Login", "is tracking 3: " + BarikoiTrace.isLocationTracking())
            Toast.makeText(
                applicationContext,
                "Service already running!! no need to start again",
                Toast.LENGTH_SHORT
            ).show()
        } else if (!BarikoiTrace.isLocationPermissionsGranted()) {
            Log.d("Login", "is Location: " + BarikoiTrace.isLocationPermissionsGranted())
            BarikoiTrace.requestLocationPermissions(this@MainActivity)
        } else if (!BarikoiTrace.isLocationSettingsOn()) {
            Log.d("Login", "is location settings on: " + BarikoiTrace.isLocationSettingsOn())
            BarikoiTrace.requestLocationServices(this@MainActivity)
        } else {
            //start tracking using preferable tracking mode with updateInterval in seconds and distanceFilter in meters
            Log.d("Login", "is tracking 2 user: " + BarikoiTrace.getUserId())
            BarikoiTrace.startTracking(TraceMode.Builder().setUpdateInterval(30).build())
            Log.d("Login", "is tracking 2: " + BarikoiTrace.isLocationTracking())
            if (BarikoiTrace.isLocationTracking()) {
                Toast.makeText(applicationContext, "Service started!!", Toast.LENGTH_SHORT).show()
                Log.d("Login", "is tracking")
            }
        }
    }
}
