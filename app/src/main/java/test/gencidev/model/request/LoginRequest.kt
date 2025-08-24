package test.gencidev.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email_phone") val email_phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("fcm_token") val fcm_token: String
)