package com.example.runningroute.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.runningroute.R
import com.example.runningroute.db.Run
import com.example.runningroute.services.Polyline
import com.example.runningroute.services.TrackingService
import com.example.runningroute.ui.viewmodels.MainViewModel
import com.example.runningroute.util.Constants.ACTION_PAUSE_SERVICE
import com.example.runningroute.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningroute.util.Constants.ACTION_STOP_SERVICE
import com.example.runningroute.util.Constants.MAP_ZOOM
import com.example.runningroute.util.Constants.POLYLINE_COLOR
import com.example.runningroute.util.Constants.POLYLINE_WIDTH
import com.example.runningroute.util.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import timber.log.Timber
import java.lang.ClassCastException
import java.util.*
import javax.inject.Inject
import kotlin.math.round

const val CANCEL_TRACKING_DIALOG_TAG = "CancelDialog"

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var map: GoogleMap? = null

    private var menu: Menu? = null

    @set:Inject
    var weight = 75f

    private var curTimeMillis = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        mapView.onCreate(savedInstanceState)

        btnToggleRun.setOnClickListener {
            toggleRun()
        }

        if (savedInstanceState != null) {
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG_TAG
            ) as CancelTrackingDialog?

            cancelTrackingDialog?.setYesListener { stopRun() }
        }

        if (curTimeMillis > 0L) {
            btnCancel.visibility = View.VISIBLE
        }

        btnCancel.setOnClickListener {
            showCancelDialog()
        }

        btnFinishRun.setOnClickListener {
            zoomShowTrackRoute()
            endRunAndSaveToDb()
        }

        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        subscribeToObservers()
    }


    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunMillis.observe(viewLifecycleOwner, {
            if(isTracking)
            {
                curTimeMillis = it
                val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeMillis)
                tvTimer.text = formattedTime
            }
        })
    }

    private fun toggleRun() {
        if (isTracking) {
            btnCancel.visibility = View.VISIBLE
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }


    private fun showCancelDialog() {
        CancelTrackingDialog().apply {
            setYesListener { stopRun() }
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)
    }

    private fun stopRun() {
        tvTimer.text = "00 : 00 : 00"
        timerAnimation.cancelAnimation()
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking

        if (!isTracking && curTimeMillis > 0L) {
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
            timerAnimation.pauseAnimation()
        } else if (isTracking) {
            btnToggleRun.text = "Stop"
            btnCancel.visibility = View.VISIBLE
            btnFinishRun.visibility = View.GONE
            timerAnimation.resumeAnimation()
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun zoomShowTrackRoute() {
        val bounds = LatLngBounds.Builder()

        var counter = 0

        for (polyline in pathPoints) {
            if (polyline.isNotEmpty()) {
                for (position in polyline) {
                    bounds.include(position)
                    counter++
                }
            }
        }

        if (counter > 0) {
            map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(),
                    mapView.width,
                    mapView.height,
                    (mapView.height * 0.05).toInt()
                )
            )
        }

    }

    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceMeters = 0
            for (polyline in pathPoints) {
                distanceMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }

            /* Calculate avg spped in km/h and round up to 1 decimal place */
            val avgSpeed =
                round((distanceMeters / 1000f) / (curTimeMillis / 1000f / 3600) * 10) / 10f

            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceMeters / 1000f) * weight).toInt()
            val run = Run(
                bmp,
                dateTimestamp,
                avgSpeed,
                distanceMeters,
                curTimeMillis,
                caloriesBurned
            )

            viewModel.insertRun(run)

            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run Saved Successfully",
                Snackbar.LENGTH_LONG
            ).show()

            stopRun()

        }
    }

    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()

            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

}