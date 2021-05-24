package com.codetest.main.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codetest.R
import com.codetest.main.util.afterTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_add_location.*

@AndroidEntryPoint
class WeatherForecastActivity : AppCompatActivity() {

    private val viewModel: WeatherForecastViewModel by viewModels()

    private var adapter = ListAdapter()
    private var locations = mutableListOf<LocationUI>()

    private var addLocationDialog: AddLocationDialog? = null
    private val dialogListener = object : DialogListener {

        override fun onAddLocationButtonClicked(
            name: String,
            status: String,
            temperature: String
        ) {
            viewModel.addLocationLiveData.observe(this@WeatherForecastActivity) { state ->
                when (state) {
                    AddLocationState.Error -> {
                        addLocationDialog?.showError()
                    }
                    AddLocationState.Loading -> {
                        // TODO
                    }
                    AddLocationState.Success -> {
                        addLocationDialog?.setButtonDialogListener(null)
                        addLocationDialog?.dismiss()
                    }
                }
            }
            viewModel.addLocation(name, status, temperature)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = ListAdapter().also {
            recyclerView.adapter = it
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val location = locations[position]
                viewModel.removeLocation(position, location)
                locations.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }).attachToRecyclerView(recyclerView)

        viewModel.weatherForecastLiveData.observe(this) {
            when (it) {
                WeatherForecastState.Error -> showError()
                WeatherForecastState.Loading -> {
                    // TODO show progress
                }
                is WeatherForecastState.Success -> {
                    if (it.clearAll) locations.clear()
                    locations.addAll(it.locations)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        viewModel.deleteLocationLiveData.observe(this@WeatherForecastActivity) { state ->
            if (state is DeleteLocationState.Error) {
                val position = state.position
                val location = state.location

                Snackbar.make(
                    recyclerView,
                    resources.getString(R.string.location_deletion_error, location.name),
                    Snackbar.LENGTH_LONG
                ).show()

                locations.add(position, location)
                adapter.notifyItemInserted(position)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_add_location -> {
                if (addLocationDialog == null) addLocationDialog = AddLocationDialog(this)
                addLocationDialog?.setButtonDialogListener(dialogListener)
                addLocationDialog?.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    private inner class AddLocationDialog(context: Context) : AppCompatDialog(context) {

        private var dialogListener: DialogListener? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_add_location)

            val status = mutableListOf(resources.getString(R.string.status_hint))
            status.addAll(StatusUI.values().map { it.toString() })

            status_spinner.apply {
                adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, status)

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        validateFieldsAndEnableButton()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Code to perform some action when nothing is selected
                    }
                }
            }

            city_name.afterTextChanged { validateFieldsAndEnableButton() }
            temperature.afterTextChanged { validateFieldsAndEnableButton() }

            add_location_button.setOnClickListener {
                error_message.visibility = View.GONE
                dialogListener?.onAddLocationButtonClicked(
                    city_name.text.toString(),
                    status_spinner.selectedItem.toString(),
                    temperature.text.toString()
                )
            }
        }

        fun setButtonDialogListener(listener: DialogListener?) {
            dialogListener = listener
        }

        fun showError() {
            error_message.visibility = View.VISIBLE
        }

        private fun validateFieldsAndEnableButton() {
            add_location_button.isEnabled = city_name.text.isNotEmpty() &&
                    status_spinner.selectedItemPosition != 0 &&
                    temperature.text.isNotEmpty()
        }
    }
}

interface DialogListener {
    fun onAddLocationButtonClicked(name: String, status: String, temperature: String)
}
