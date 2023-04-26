package com.shaunhossain.flutter_android_background_service.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.shaunhossain.flutter_android_background_service.MainActivity
import com.shaunhossain.flutter_android_background_service.R
import com.shaunhossain.traceservice.service.DefaultLocationClient
import com.shaunhossain.traceservice.service.LocationClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient


    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        var USER = "Unknown"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        starts()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> starts()
            ACTION_STOP -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(): Int {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "track_location")
            .setContentTitle("Tracking location ....")
            .setContentText("$USER Location: null")
            .setSmallIcon(R.drawable.icon_notifications)
            .setOngoing(true)
        locationClient.getLocationUpdates(100L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString()
                val lon = location.longitude.toString()
                val updateNotification = notification.setContentText("$USER Location: ($lat,$lon)")
                notificationManager.notify(1, updateNotification.build())
            }
            .launchIn(serviceScope)

        return START_STICKY
    }

    private fun starts(): Int {
        var notificationManager: NotificationManager? = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("101", "foreground", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification =
            NotificationCompat.Builder(this, "101").setContentTitle("foreground service")
                .setContentText("This is $USER location").setSmallIcon(R.drawable.icon_notifications)
                .setContentIntent(pi)

        locationClient.getLocationUpdates(100L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString()
                val lon = location.longitude.toString()
                val updateNotification = notification.setContentText("$USER Location: ($lat,$lon)")
                notificationManager?.notify(1, updateNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())

        return START_STICKY
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("serviceWarning", "service removed")

    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}