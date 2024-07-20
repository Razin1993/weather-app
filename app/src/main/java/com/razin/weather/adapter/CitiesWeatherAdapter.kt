package com.razin.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.razin.weather.data.db.WeatherInfo
import com.razin.weather.databinding.ItemCityWeatherBinding
import com.razin.weather.util.FunctionUtil
import kotlin.math.ceil

class CitiesWeatherAdapter : RecyclerView.Adapter<CitiesWeatherAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: ItemCityWeatherBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val txtDate = itemView.txtDate
        val txtLocationName = itemView.txtLocationName
        val ivWeather = itemView.ivWeather
        val txtTemp = itemView.txtTemp
        val txtInfo = itemView.txtInfo
        val txtMin = itemView.txtMin
        val txtMax = itemView.txtMax
    }

    private val diffUtilCallBack = object : DiffUtil.ItemCallback<WeatherInfo>() {

        override fun areItemsTheSame(oldItem: WeatherInfo, newItem: WeatherInfo): Boolean {
            return oldItem.location == newItem.location
        }

        override fun areContentsTheSame(oldItem: WeatherInfo, newItem: WeatherInfo): Boolean {
            return oldItem == newItem
        }

    }

    val diffUtil = AsyncListDiffer(this, diffUtilCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemCityWeatherBinding =
            ItemCityWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemCityWeatherBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherInfo = diffUtil.currentList[position]
        with(weatherInfo) {
            holder.txtDate.text = FunctionUtil.convertMillisToDateTime(date * 1000)
            holder.txtLocationName.text = location
            holder.txtTemp.text = buildString {
                append(ceil(temperature).toInt())
                append("°")
            }

            holder.txtInfo.text = buildString {
                append(main)
                append("(")
                append(description)
                append(")")
            }

            holder.txtMin.text = buildString {
                append("Min : ")
                append(ceil(tempMin).toInt())
                append("°")
            }

            holder.txtMax.text = buildString {
                append("Max : ")
                append(ceil(tempMax).toInt())
                append("°")
            }

            val imageUrl = "https://openweathermap.org/img/wn/$icon.png"
            holder.ivWeather.load(imageUrl)

        }
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }
}