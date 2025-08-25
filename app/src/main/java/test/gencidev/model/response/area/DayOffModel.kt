package test.gencidev.model.response.area

data class DayOffModel(
    val id: Long = 0,
    val tanggal: String,
    val tanggal_display: String,
    val keterangan: String,
    val is_cuti: Boolean
)