package com.codetest.main.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codetest.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class WeatherForecastActivity : AppCompatActivity() {

    private val viewModel: WeatherForecastViewModel by viewModels()

    private var adapter = ListAdapter()
    private var locations: List<LocationUI> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.codetest.R.layout.activity_main)

        adapter = ListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        viewModel.weatherForecastLiveData.observe(this) {
            when (it) {
                WeatherForecastState.Error -> showError()
                WeatherForecastState.Loading -> {
                    // TODO show progress
                }
                is WeatherForecastState.Success -> {
                    locations = it.locations
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun showError() {
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.error_title))
            .setMessage(resources.getString(R.string.error_title))
            .setPositiveButton(resources.getString(R.string.ok), { _, _ -> })
            .create()
            .show()
    }

    private inner class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int {
            return locations.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return LocationViewHolder.create(parent)
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
            (viewHolder as? LocationViewHolder)?.setup(locations[position])
        }
    }
}