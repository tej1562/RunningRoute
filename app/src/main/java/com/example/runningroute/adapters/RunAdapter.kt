package com.example.runningroute.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runningroute.R
import com.example.runningroute.db.Run
import com.example.runningroute.util.TrackingUtility
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    val differCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return (oldItem.id == newItem.id)
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return (oldItem.hashCode() == newItem.hashCode())
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_run, parent, false)
        return RunViewHolder(view)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load(run.previewImg).into(ivRunImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timeStamp
            }

            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

            tvDate.text = dateFormat.format(calendar.time)

            val avgSpeed = "${run.averageSpeedKMH} km/h"
            tvAvgSpeed.text = avgSpeed

            val distanceKm = "${run.distanceMeters / 1000f} km"
            tvDistance.text = distanceKm

            tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeMillis)

            val caloriesBurned = "${run.caloriesBurned} kcal"
            tvCalories.text = caloriesBurned
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}