package test.gencidev.ui.xml

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import test.gencidev.databinding.ActivityMainBinding
import test.gencidev.extensions.goGone
import test.gencidev.extensions.goVisible
import test.gencidev.extensions.isLog
import test.gencidev.model.response.area.DayOffModel
import test.gencidev.module.BaseActivity
import test.gencidev.viewmodel.DayOffViewModel
import java.lang.Exception
import java.util.ArrayList
import java.util.Locale
import kotlin.text.clear

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel by viewModels<DayOffViewModel>()

    lateinit var binding: ActivityMainBinding

    var search = ""

    private var dayOffModel: MutableList<DayOffModel> = ArrayList()
    private var searchDayOffModel = ArrayList<DayOffModel>()

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        DayOffAdapter(::onClicked)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.dayOff()

        observe(viewModel.dayOffResponse) {
            if (it?.isNotEmpty() == true) {
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
            if (searchDayOffModel.isEmpty()){
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

    private fun onClicked(data: DayOffModel) {

    }

}