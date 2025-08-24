package test.gencidev.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DataEntity(
    @PrimaryKey(autoGenerate = false)
    var nomor_telepon: String = "",
    var nama: String = "",
    var tanggal_lahir: String = "",
    var alamat: String = ""
)