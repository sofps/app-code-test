package com.codetest.main.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codetest.R
import kotlinx.android.synthetic.main.location.view.*


class LocationViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup): LocationViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.location, parent, false)
            return LocationViewHolder(view)
        }
    }

    fun setup(location: LocationUI) {
        itemView.card.setCardBackgroundColor(itemView.resources.getColor(location.status.color))
        itemView.name.text = location.name
        itemView.weatherInfo.text = location.weather
    }
}
