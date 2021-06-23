package com.example.runningroute.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM run_table ORDER BY timeStamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY timeMillis DESC")
    fun getAllRunsSortedByTimeMillis(): LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY averageSpeedKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY distanceMeters DESC")
    fun getAllRunsSortedByDistanceM(): LiveData<List<Run>>

    @Query("SELECT SUM(timeMillis) FROM run_table")
    fun getTotalTimeMillis(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM run_table")
    fun getTotalCaloriesBurned(): LiveData<Int>

    @Query("SELECT SUM(distanceMeters) FROM run_table")
    fun getTotalDistanceMeters(): LiveData<Int>

    @Query("SELECT AVG(averageSpeedKMH) FROM run_table")
    fun getTotalAvgSpeed(): LiveData<Float>

}