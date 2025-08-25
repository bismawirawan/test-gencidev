package test.gencidev.model.data

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
class ErrorResponse(
    @Json(name = "status") val status: String?,
    @Json(name = "message") val message: String?
)