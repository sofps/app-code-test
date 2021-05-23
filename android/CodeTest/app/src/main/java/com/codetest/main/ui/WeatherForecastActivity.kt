package com.codetest.main.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codetest.R
import com.codetest.main.data.repository.LocationRepository
import com.codetest.main.domain.LocationSuccess
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class WeatherForecastActivity : AppCompatActivity() {

    @Inject lateinit var locationRepository: LocationRepository

    private var adapter = ListAdapter()
    private var locations: List<LocationUI> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.codetest.R.layout.activity_main)

        adapter = ListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        fetchLocations()
    }

    private fun fetchLocations() {
        locationRepository.getLocations()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { result ->
                    if (result is LocationSuccess) {
                        locations = result.locations.map { it.toLocationUI() }
                        adapter.notifyDataSetChanged()
                    } else {
                        showError()
                    }
                },
                onError = {
                    showError()
                }
            )
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