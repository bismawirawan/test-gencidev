package test.gencidev.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import test.gencidev.database.entity.DayOffEntity

@Dao
interface DaoData {
    @Query("SELECT * FROM day_off_table")
    fun getAll(): LiveData<List<DayOffEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg data: DayOffEntity)

    @Query("DELETE FROM day_off_table")
    fun deleteAll()

    @Query("SELECT * FROM day_off_table")
    fun getAllDayOffs(): LiveData<List<DayOffEntity>>

    @Query("SELECT * FROM day_off_table")
    suspend fun getAllDayOffsSync(): List<DayOffEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDayOffs(dayOffs: List<DayOffEntity>)

    @Query("DELETE FROM day_off_table")
    suspend fun deleteAllDayOffs()
}