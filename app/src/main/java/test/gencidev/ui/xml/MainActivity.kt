package test.gencidev.ui.xml

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import test.gencidev.common.states.UiState
import test.gencidev.databinding.ActivityMainBinding
import test.gencidev.extensions.goGone
import test.gencidev.extensions.goVisible
import test.gencidev.extensions.isLog
import test.gencidev.extensions.toast
import test.gencidev.model.response.area.DayOffModel
import test.gencidev.module.BaseActivity
import test.gencidev.network.connection.NetworkConnectionLiveData
import test.gencidev.viewmodel.DayOffViewModel
import java.lang.Exception
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel by viewModels<DayOffViewModel>()

    lateinit var binding: ActivityMainBinding

    var search = ""

    private var dayOffModel: MutableList<DayOffModel> = ArrayList()
    private var searchDayOffModel = ArrayList<DayOffModel>()

    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    private var isConnect = false

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        DayOffAdapter(::onClicked)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val networkconnection = NetworkConnectionLiveData(this)
        observeNonNull(networkconnection) { isConnected ->
            try {
                if (isConnected) {
                    isConnect = true
                    isLog("network connected")
                } else {
                    isConnect = false
                    isLog("network disconnect")
                    viewModel.dataState.removeObservers(this)
                    observeNonNull(viewModel.dataState) { state ->
                        when (state) {
                            UiState.Loading -> {
                                binding.btnAmbil.goGone()
                                binding.loading.goVisible()
                                binding.recycler.goGone()
                            }

                            UiState.Success -> {
                                binding.btnAmbil.goGone()
                                binding.loading.goGone()
                                binding.recycler.goVisible()
                            }

                            is UiState.Error -> {
                                binding.btnAmbil.goGone()
                                binding.loading.goGone()
                                binding.recycler.goVisible()
                                toast(state.message)
                            }
                        }
                    }

                    viewModel.checkDatabaseAndLoadData()
                }
            } catch (error: kotlin.Exception) {
                isLog("error: ${error.message}")
            }

        }

        observe(viewModel.dayOffResponse) {
            if (it?.isNotEmpty() == true) {
                binding.tvDataKosong.goGone()
                dayOffModel.clear()
                dayOffModel.addAll(it)
                adapter.clear()
                adapter.insertAll(dayOffModel)
            }
        }

        binding.recycler.let {
            it.adapter = adapter
            it.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                search = s.toString()
                searching()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        binding.layoutFilter.setOnClickListener {
            if (isConnect)
                showYearPicker()
            else
                toast("No internet")
        }

        binding.btnAmbil.setOnClickListener {
            if (isConnect) {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
                checkNetworkAndGetData(currentYear)
            } else
                toast("No internet")
        }
    }

    private fun checkNetworkAndGetData(year: String) {
        val networkconnection = NetworkConnectionLiveData(this)
        networkconnection.observe(this) { isConnected ->
            if (isConnected) {
                getData(year)
            } else {
                viewModel.checkDatabaseAndLoadData()
            }
        }
    }

    private fun getData(data: String) {
        viewModel.dataState.removeObservers(this)
        viewModel.dayOff(data)
        observeNonNull(viewModel.dataState) {
            when (it) {
                UiState.Loading -> {
                    binding.tvDataKosong.goGone()
                    binding.btnAmbil.goGone()
                    binding.loading.goVisible()
                    binding.recycler.goGone()
                }

                UiState.Success -> {
                    binding.btnAmbil.goGone()
                    binding.loading.goGone()
                    binding.recycler.goVisible()
                }

                is UiState.Error -> {
                    binding.btnAmbil.goGone()
                    binding.loading.goGone()
                    binding.recycler.goVisible()

                    viewModel.checkDatabaseAndLoadData()
                }
            }
        }
    }

    private fun searching() {
        val text = search.lowercase(Locale.getDefault())

        searchDayOffModel = dayOffModel.filter {
            it.keterangan.lowercase(Locale.getDefault()).contains(text)
        } as ArrayList<DayOffModel>

        if (search.isNotEmpty()) {
            try {
                adapter.clear()
                adapter.insertAll(searchDayOffModel)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (searchDayOffModel.isEmpty()) {
                binding.layoutKosong.goVisible()
                binding.recycler.goGone()
            } else {
                binding.layoutKosong.goGone()
                binding.recycler.goVisible()
            }
        } else {
            try {
                adapter.clear()
                adapter.insertAll(dayOffModel)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            checkEmptyData()
        }
    }

    private fun checkEmptyData() {
        if (dayOffModel.isEmpty()) {
            binding.layoutKosong.goVisible()
            binding.recycler.goGone()
        } else {
            binding.layoutKosong.goGone()
            binding.recycler.goVisible()
        }
    }

    private fun showYearPicker() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = mutableListOf<String>()

        val startYear = 2020
        val endYear = currentYear + 2

        for (year in startYear..endYear) {
            years.add(year.toString())
        }

        AlertDialog.Builder(this)
            .setTitle("Pilih Tahun")
            .setSingleChoiceItems(
                years.toTypedArray(),
                years.indexOf(selectedYear.toString())
            ) { dialog, which ->
                selectedYear = years[which].toInt()
                dialog.dismiss()
                getData(selectedYear.toString())
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun onClicked(data: DayOffModel) {
        val dialog = DialogDayOff(this, data)
        dialog.show()
    }

}