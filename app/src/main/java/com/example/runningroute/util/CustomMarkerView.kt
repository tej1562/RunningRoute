package com.example.runningroute.util

import android.content.Context
import com.example.runningroute.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    val runs: List<Run>,
    context: Context,
    layoutId: Int
): MarkerView(context,layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f,-height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e == null)
            return

        val currRunId = e.x.toInt()
        val run = runs[currRunId]


        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }

        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

        tvDate.text = dateFormat.format(calendar.time)

        val avgSpeed = "${run.averageSpeedKMH}km/h"
        tvAvgSpeed.text = avgSpeed

        val distanceKm = "${run.distanceMeters/1000f}km"
        tvDistance.text = distanceKm

        tvDuration.text = TrackingUtility.getFormattedStopWatchTime(run.timeMillis)

        val caloriesBurned = "${run.caloriesBurned}kcal"
        tvCaloriesBurned.text = caloriesBurned
    }
}