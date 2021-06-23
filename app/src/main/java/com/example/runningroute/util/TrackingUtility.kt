package com.example.runningroute.util

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.example.runningroute.services.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit
import kotlin.math.min

object TrackingUtility {

    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        }

    fun calculatePolylineLength(polyline: Polyline): Float {
        var distance = 0F
        for (i in 0..polyline.size - 2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]

            val results = FloatArray(1)
            Location.distanceBetween(pos1.latitude, pos1.longitude, pos2.latitude, pos2.longitude,results)

            distance += results[0]
        }

        return distance
    }

    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {
        var millis = ms
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        millis -= TimeUnit.SECONDS.toMillis(seconds)

        if (!includeMillis) {
            return "${if (hours < 10) "0" else ""}$hours : " +
                    "${if (minutes < 10) "0" else ""}$minutes : " +
                    "${if (seconds < 10) "0" else ""}$seconds"
        }

        millis /= 10

        return "${if (hours < 10) "0" else ""}$hours : " +
                "${if (minutes < 10) "0" else ""}$minutes : " +
                "${if (seconds < 10) "0" else ""}$seconds : " +
                "${if (millis < 10) "0" else ""}$millis"
    }
}