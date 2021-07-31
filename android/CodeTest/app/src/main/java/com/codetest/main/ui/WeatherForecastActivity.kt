package com.codetest.main.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codetest.R
import com.codetest.databinding.ActivityMainBinding
import com.codetest.databinding.DialogAddLocationBinding
import com.codetest.main.util.afterTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherForecastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = ListAdapter().also {
            binding.recyclerView.adapter = it
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
        }).attachToRecyclerView(binding.recyclerView)

        viewModel.weatherForecastLiveData.observe(this) {
            when (it) {
                WeatherForecastState.Error -> showError()
                WeatherForecastState.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is WeatherForecastState.Success -> {
                    binding.progress.visibility = View.GONE
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
                    binding.recyclerView,
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
        binding.progress.visibility = View.GONE
        Snackbar.make(
            binding.recyclerView,
            resources.getString(R.string.error_message),
            Snackbar.LENGTH_LONG
        ).setAction(R.string.retry) { viewModel.loadLocations() }
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

        private lateinit var binding: DialogAddLocationBinding

        private var dialogListener: DialogListener? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = DialogAddLocationBinding.inflate(LayoutInflater.from(context))
            setContentView(binding.root)

            val status = mutableListOf(resources.getString(R.string.status_hint))
            status.addAll(StatusUI.values().map { it.toString() })

            binding.statusSpinner.apply {
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

            binding.cityName.afterTextChanged { validateFieldsAndEnableButton() }
            binding.temperature.afterTextChanged { validateFieldsAndEnableButton() }

            binding.addLocationButton.setOnClickListener {
                binding.errorMessage.visibility = View.GONE
                dialogListener?.onAddLocationButtonClicked(
                    binding.cityName.text.toString(),
                    binding.statusSpinner.selectedItem.toString(),
                    binding.temperature.text.toString()
                )
            }
        }

        fun setButtonDialogListener(listener: DialogListener?) {
            dialogListener = listener
        }

        fun showError() {
            binding.errorMessage.visibility = View.VISIBLE
        }

        private fun validateFieldsAndEnableButton() {
            binding.addLocationButton.isEnabled = binding.cityName.text.isNotEmpty() &&
                    binding.statusSpinner.selectedItemPosition != 0 &&
                    binding.temperature.text.isNotEmpty()
        }
    }
}

interface DialogListener {
    fun onAddLocationButtonClicked(name: String, status: String, temperature: String)
}
