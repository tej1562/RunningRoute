package com.example.runningroute.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.runningroute.R
import com.example.runningroute.util.Constants.KEY_NAME
import com.example.runningroute.util.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsSharedPref()
        btnApplyChanges.setOnClickListener {
            val success = applyChangesSharedPref()

            if (success) {
                Snackbar.make(requireView(), "Changes Applied Successfully", Snackbar.LENGTH_LONG)
                    .show()
            } else {
                Snackbar.make(requireView(), "Please fill all the fields", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun loadFieldsSharedPref() {
        val name = sharedPreferences.getString(KEY_NAME, "")
        val weight = sharedPreferences.getFloat(KEY_WEIGHT, 75f)

        etName.setText(name)
        etWeight.setText(weight.toString())


    }

    private fun applyChangesSharedPref(): Boolean {
        val name = etName.text.toString()
        val weight = etWeight.text.toString()

        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }

        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .apply()

        return true
    }
}