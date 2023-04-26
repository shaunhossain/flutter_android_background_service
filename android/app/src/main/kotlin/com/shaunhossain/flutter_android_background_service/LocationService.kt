package com.shaunhossain.flutter_android_background_service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class LocationService : Service() {

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("101", "foreground", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notifcationIntent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, notifcationIntent, 0)

        val notification =
            NotificationCompat.Builder(this, "101").setContentTitle("foreground service")
                .setContentText("This is content").setSmallIcon(R.drawable.launch_background)
                .setContentIntent(pi).build()

        startForeground(1, notification)

        return START_STICKY
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }
}