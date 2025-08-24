package test.gencidev.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import test.gencidev.common.states.ActionLiveData
import test.gencidev.common.states.UiState
import test.gencidev.model.response.area.DayOffModel
import test.gencidev.network.services.DayOffServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayOffViewModel @Inject constructor(
    private val services: DayOffServices
) : ViewModel() {

    //dayOff
    val dataState = ActionLiveData<UiState>()
    var dayOffResponse = MutableLiveData<List<DayOffModel>>()

    fun dayOff() {
        dataState.sendAction(UiState.Loading)
        viewModelScope.launch {
            try {
                val response = services.dayOff()
                dayOffResponse.postValue(response)
            } catch (error: Exception) {
                error.printStackTrace()
            }
        }
    }
}