package test.gencidev.model.data

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
class Response<out T>(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String,
    @Json(name = "code") val code: Int,
    @Json(name = "data") val data: T,
    @Json(name = "meta") val meta: Meta?
)

@Keep
class ErrorResponse(
    @Json(name = "status") val status: String?,
    @Json(name = "message") val message: String?
)

@Keep
data class Meta(
    @Json(name = "current_page") val current_page: Int,
    @Json(name = "last_page") val last_page: Int,
    @Json(name = "per_page") val per_page: Int,
    @Json(name = "total") val total: Int
)

@Keep
class ResponseStatus(
    @Json(name = "status") val status: String,
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String
)

@Keep
class ResponseReferral(
    @Json(name = "status") val status: String,
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String,
    @Json(name = "data") val data: String
)

@Keep
class ResponseSimulasi(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String,
    @Json(name = "harga_kendaraan") val harga_kendaraan: String,
    @Json(name = "uang_muka") val uang_muka: Long,
    @Json(name = "pokok_pinjaman") val pokok_pinjaman: Long,
    @Json(name = "angsuran_perbulan") val angsuran_perbulan: Long,
    @Json(name = "total_bunga") val total_bunga: Long
)

@Keep
class ResponseLogout(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String
)