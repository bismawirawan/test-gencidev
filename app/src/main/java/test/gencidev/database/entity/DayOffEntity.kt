package test.gencidev.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_off_table")
data class DayOffEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keterangan: String = "",
    val tanggal: String = "",
    val tanggal_display: String = "",
    val is_cuti: Boolean = false
)