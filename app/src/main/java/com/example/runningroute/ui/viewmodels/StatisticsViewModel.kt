package com.example.runningroute.ui.viewmodels


import androidx.lifecycle.ViewModel
import com.example.runningroute.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
        val mainRepository: MainRepository
): ViewModel() {

        val totalTimeRun = mainRepository.getTotalTimeMillis()
        val totalDistanceRun = mainRepository.getTotalDistanceMeters()
        val totalCaloriesBurned = mainRepository.getTotalCaloriesBurned()
        val totalAverageSpeed = mainRepository.getTotalAvgSpeed()

        val runsSortedByDate = mainRepository.getAllRunsSortedByDate()



}