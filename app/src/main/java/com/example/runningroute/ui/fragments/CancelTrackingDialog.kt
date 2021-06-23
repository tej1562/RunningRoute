package com.example.runningroute.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.runningroute.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelTrackingDialog: DialogFragment() {

    private var yesListener: (() -> Unit)? = null

    fun setYesListener(listener: () -> Unit){
        yesListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
         return MaterialAlertDialogBuilder(requireContext())
             .setTitle("Cancel Run")
             .setMessage("Are you sure to cancel the current run?")
             .setIcon(R.drawable.ic_delete)
             .setPositiveButton("Yes") { _, _ ->
                 yesListener?.let {yes->
                     yes()
                 }
             }
             .setNegativeButton("No") { dialogInterface, _ ->
                 dialogInterface.cancel()
             }
             .create()
    }
}