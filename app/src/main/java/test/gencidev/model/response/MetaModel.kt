package test.gencidev.model.response

import com.google.gson.annotations.SerializedName

data class MetaModel(
    val current_page: Int,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)