package com.razin.weather.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.razin.weather.R
import com.razin.weather.adapter.CitiesNameAdapter
import com.razin.weather.databinding.FragmentAddCityBinding

class AddCityFragment(
    private val selectedCityList: List<String>,
    listener: CitySelectedListener
) : BottomSheetDialogFragment() {

    private var _binding: FragmentAddCityBinding? = null
    private val binding get() = _binding!!

    private var bottomSheet: FrameLayout? = null
    private var listener: CitySelectedListener? = null

    init {
        this.listener = listener
    }

    fun interface CitySelectedListener {
        fun onCitySelected(selectedCity: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setDimAmount(0.4f)

            setOnShowListener {
                bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                addContentToView()

            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as CitySelectedListener
        } catch (e: ClassCastException) {
            Log.e("AddCityFragment", "onAttach: ClassCastException: ${e.message}")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCityBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun addContentToView() {
        val topCitiesList = listOf("Bengaluru", "Chennai", "Kolkata", "Mumbai", "New Delhi")
        val topCitiesInWorld = listOf("New York", "Paris", "Dubai", "London", "Sydney")

        val citiesNameAdapter = CitiesNameAdapter(topCitiesList, selectedCityList)
        binding.rvTopCity.adapter = citiesNameAdapter

        citiesNameAdapter.setOnCitySelectedListener { selectedCity ->
            listener?.onCitySelected(selectedCity)
        }

        val citiesNameWorldAdapter = CitiesNameAdapter(topCitiesInWorld, selectedCityList)
        binding.rvTopCityWorld.adapter = citiesNameWorldAdapter

        citiesNameWorldAdapter.setOnCitySelectedListener { selectedCity ->
            listener?.onCitySelected(selectedCity)
        }

        binding.txtCancelCity.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}