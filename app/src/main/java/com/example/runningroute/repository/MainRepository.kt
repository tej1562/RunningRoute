package com.example.runningroute.repository

import com.example.runningroute.db.Run
import com.example.runningroute.db.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runDAO: RunDAO
) {
    suspend fun insertRun(run: Run) = runDAO.insertRun(run)

    suspend fun deleteRun(run: Run) = runDAO.deleteRun(run)

    fun getAllRunsSortedByDate() = runDAO.getAllRunsSortedByDate()

    fun getAllRunsSortedByTimeMillis() = runDAO.getAllRunsSortedByTimeMillis()

    fun getAllRunsSortedByCaloriesBurned() = runDAO.getAllRunsSortedByCaloriesBurned()

    fun getAllRunsSortedByAvgSpeed() = runDAO.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByDistanceM() = runDAO.getAllRunsSortedByDistanceM()

    fun getTotalTimeMillis() = runDAO.getTotalTimeMillis()

    fun getTotalCaloriesBurned() = runDAO.getTotalCaloriesBurned()

    fun getTotalDistanceMeters() = runDAO.getTotalDistanceMeters()

    fun getTotalAvgSpeed() = runDAO.getTotalAvgSpeed()
}