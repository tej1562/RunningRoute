package com.example.runningroute.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningroute.R
import com.example.runningroute.ui.viewmodels.StatisticsViewModel
import com.example.runningroute.util.CustomMarkerView
import com.example.runningroute.util.TrackingUtility
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setupBarChart()
    }

    private fun subscribeToObservers(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner,{
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                tvTotalTime.text = totalTimeRun
            }
        })
        viewModel.totalDistanceRun.observe(viewLifecycleOwner,{
            it?.let {
                val km = it/1000f
                val totalDistance = round(km * 10f)/10f
                val totalDistanceString = "${totalDistance} km"
                tvTotalDistance.text = totalDistanceString
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner,{
            it?.let {
                val totalCalories = "${it} kcal"
                tvTotalCalories.text = totalCalories
            }
        })
        viewModel.totalAverageSpeed.observe(viewLifecycleOwner,{
            it?.let {
                val avgSpeed = round(it * 10f)/10f
                val avgSpeedString = "${avgSpeed} km/h"
                tvAverageSpeed.text = avgSpeedString
            }
        })

        viewModel.runsSortedByDate.observe(viewLifecycleOwner,{
            it?.let{
                val allAvgSpeeds = it.indices.map{i ->
                   BarEntry(i.toFloat(),it[i].averageSpeedKMH)
                }

                val barDataSet = BarDataSet(allAvgSpeeds,"Avg Speed v/s Time").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(),R.color.colorAccent)
                }

                barChart.data = BarData(barDataSet)
                barChart.marker = CustomMarkerView(it.reversed(),requireContext(),R.layout.marker_view)
                barChart.invalidate()
            }
        })
    }

    private fun setupBarChart(){
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        barChart.apply {
            description.text = "Average Speed v/s Time"
            legend.isEnabled = false
        }
    }
    
}