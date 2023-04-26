package com.shaunhossain.traceservice.utils

import android.content.Context
import androidx.core.content.ContextCompat
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import java.text.SimpleDateFormat
import java.util.*


fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}


@SuppressLint("SimpleDateFormat")
fun formatDate(timestampInMillis: Long): String? {
    val sdf = SimpleDateFormat("MM/dd/yyyy")
    return sdf.format(Date(timestampInMillis))
}