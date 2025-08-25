package test.gencidev.network.services

import retrofit2.http.GET
import retrofit2.http.Query
import test.gencidev.model.data.Response
import test.gencidev.model.response.area.DayOffModel

interface DayOffServices {

    @GET("/api")
    suspend fun dayOff(
        @Query("year") year: String
    ): List<DayOffModel>
}