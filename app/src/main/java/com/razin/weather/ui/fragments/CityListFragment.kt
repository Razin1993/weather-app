package com.razin.weather.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.razin.weather.HomeActivity
import com.razin.weather.R
import com.razin.weather.adapter.CitiesWeatherAdapter
import com.razin.weather.databinding.FragmentCityListBinding
import com.razin.weather.ui.uiUtils.BottomPaddingForRecycler
import com.razin.weather.ui.viewModel.WeatherViewModel

class CityListFragment : Fragment(),AddCityFragment.CitySelectedListener {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WeatherViewModel
    private lateinit var citiesWeatherAdapter: CitiesWeatherAdapter

    private var addedCity: List<String>? = null
    private lateinit var addCityDialog:AddCityFragment
    private lateinit var listener:AddCityFragment.CitySelectedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = this
        requireActivity().onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        viewModel = (activity as HomeActivity).viewModel
        setAdapter()
        handleObservers()
    }

    private fun setListeners(){
        binding.ivBack.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.addLocationIcon.setOnClickListener {
            handleAddCity()
        }

        val touchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val weatherInfo = citiesWeatherAdapter.diffUtil.currentList[position]
                viewModel.deleteWeather(weatherInfo)
                Snackbar.make(binding.root,"Deleted Successfully",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.saveWeather(weatherInfo)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(touchHelperCallBack).apply {
            attachToRecyclerView(binding.rvCityList)
        }
    }

    private fun setAdapter(){
        citiesWeatherAdapter = CitiesWeatherAdapter()
        binding.rvCityList.apply {
            adapter = citiesWeatherAdapter
            addItemDecoration(BottomPaddingForRecycler(20))
        }

    }

    private fun handleObservers(){

        viewModel.getAllWeather().observe(viewLifecycleOwner){ weatherList ->
            citiesWeatherAdapter.diffUtil.submitList(weatherList)
        }

        viewModel.getLocations().observe(viewLifecycleOwner) { locations ->
            addedCity = locations
        }
    }

    private fun handleAddCity(){
        addCityDialog = AddCityFragment(addedCity?: emptyList(),listener)
        addCityDialog.show(parentFragmentManager,"add_city")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCitySelected(selectedCity: String) {
        viewModel.getWeatherByCity(selectedCity)
        addCityDialog.dismiss()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            val navController = findNavController()
            navController.popBackStack()
            navController.navigate(R.id.homeFragment,null,null)
        }
    }

}