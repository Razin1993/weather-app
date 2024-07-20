package com.razin.weather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.razin.weather.databinding.ItemCitiesNameBinding

class CitiesNameAdapter(
    private val cityList: List<String>,
    private var selectedCityList: List<String>,
) : RecyclerView.Adapter<CitiesNameAdapter.ViewHolder>() {

    private val checkedCities = BooleanArray(cityList.size)
    private var listener: onCitySelectedListener? = null

    fun interface onCitySelectedListener {
        fun onCitySelected(selectedCity: String)
    }

    fun setOnCitySelectedListener(listener: onCitySelectedListener) {
        this.listener = listener
    }

    init {
        for (i in cityList.indices) {
            for (j in selectedCityList.indices) {
                if (cityList[i] == selectedCityList[j]) {
                    checkedCities[i] = true
                }
            }
        }
    }

    class ViewHolder(itemView: ItemCitiesNameBinding) : RecyclerView.ViewHolder(itemView.root) {

        val cityName: AppCompatCheckBox = itemView.cityName

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemCitiesNameBinding =
            ItemCitiesNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemCitiesNameBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cityName.text = cityList[position]
        holder.cityName.isChecked = checkedCities[position]
        holder.cityName.isEnabled = !checkedCities[position]
        holder.cityName.setOnClickListener {
            listener?.onCitySelected(cityList[position])
        }
    }

    override fun getItemCount() = cityList.size

}