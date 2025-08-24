package test.gencidev.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import test.gencidev.database.entity.DataEntity

@Dao
interface DaoData {
    @Query("SELECT * FROM DataEntity")
    fun getAll(): LiveData<List<DataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg data: DataEntity)

    @Query("DELETE FROM DataEntity")
    fun deleteAll()
}