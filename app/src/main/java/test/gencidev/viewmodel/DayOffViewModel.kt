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
import test.gencidev.database.dao.DaoData
import test.gencidev.database.entity.DayOffEntity
import javax.inject.Inject

@HiltViewModel
class DayOffViewModel @Inject constructor(
    private val services: DayOffServices,
    private val dayOffDao: DaoData
) : ViewModel() {

    val dataState = ActionLiveData<UiState>()
    var dayOffResponse = MutableLiveData<List<DayOffModel>>()
    val databaseCheckResult = MutableLiveData<DatabaseCheckResult>()

    fun dayOff(data: String) {
        dataState.sendAction(UiState.Loading)
        viewModelScope.launch {
            try {
                val response = services.dayOff(data)
                if (response.isNotEmpty()) {
                    saveDayOffsToDatabase(response)

                    dayOffResponse.postValue(response)
                    dataState.sendAction(UiState.Success)
                } else {
                    loadFromDatabase()
                }
            } catch (error: Exception) {
                error.printStackTrace()
                loadFromDatabase()
            }
        }
    }

    fun checkDatabaseAndLoadData() {
        dataState.sendAction(UiState.Loading)
        viewModelScope.launch {
            try {
                val entities = getDayOffsFromDatabase()

                if (entities.isNotEmpty()) {
                    val models = entities.map { entity ->
                        DayOffModel(
                            tanggal = entity.tanggal,
                            tanggal_display = entity.tanggal_display,
                            keterangan = entity.keterangan,
                            is_cuti = entity.is_cuti
                        )
                    }
                    dayOffResponse.postValue(models)
                    dataState.sendAction(UiState.Success)
                    databaseCheckResult.postValue(DatabaseCheckResult.HasData)
                } else {
                    dataState.sendAction(UiState.Error("No internet."))
                    databaseCheckResult.postValue(DatabaseCheckResult.NoData)
                }
            } catch (e: Exception) {
                dataState.sendAction(UiState.Error("Error loading cached data: ${e.message}"))
            }
        }
    }

    private suspend fun getDayOffsFromDatabase(): List<DayOffEntity> {
        return try {
            dayOffDao.getAllDayOffsSync()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun saveDayOffsToDatabase(dayOffs: List<DayOffModel>) {
        try {

            val entities = dayOffs.mapIndexed { index, dayOff ->
                val entity = DayOffEntity(
                    id = 0,
                    keterangan = dayOff.keterangan,
                    tanggal = dayOff.tanggal,
                    tanggal_display = dayOff.tanggal_display,
                    is_cuti = dayOff.is_cuti
                )
                entity
            }

            dayOffDao.deleteAllDayOffs()

            dayOffDao.insertDayOffs(entities)

            val savedItems = dayOffDao.getAllDayOffsSync()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadFromDatabase() {
        try {
            val entities = getDayOffsFromDatabase()
            if (entities.isNotEmpty()) {
                val models = entities.map { entity ->
                    DayOffModel(
                        tanggal = entity.tanggal,
                        tanggal_display = entity.tanggal_display,
                        keterangan = entity.keterangan,
                        is_cuti = entity.is_cuti
                    )
                }
                dayOffResponse.postValue(models)
                dataState.sendAction(UiState.Success)
            } else {
                dataState.sendAction(UiState.Error("No data."))
            }
        } catch (e: Exception) {
            dataState.sendAction(UiState.Error("Error loading offline data: ${e.message}"))
        }
    }

    enum class DatabaseCheckResult {
        HasData,
        NoData
    }
}