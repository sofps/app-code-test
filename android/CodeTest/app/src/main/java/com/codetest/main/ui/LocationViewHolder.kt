package com.codetest.main.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.codetest.databinding.LocationBinding


class LocationViewHolder private constructor(private val binding: LocationBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup): LocationViewHolder {
            val binding = LocationBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return LocationViewHolder(binding)
        }
    }

    fun setup(location: LocationUI) {
        binding.card.setCardBackgroundColor(itemView.resources.getColor(location.status.color))
        binding.name.text = location.name
        binding.weatherInfo.text = location.weather
    }
}
