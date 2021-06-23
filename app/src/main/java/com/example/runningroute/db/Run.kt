package com.example.runningroute.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_table")
data class Run(
    var previewImg: Bitmap? = null,
    var timeStamp: Long = 0L,
    var averageSpeedKMH: Float = 0F,
    var distanceMeters: Int = 0,
    var timeMillis: Long = 0L,
    var caloriesBurned: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}