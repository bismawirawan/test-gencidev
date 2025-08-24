package test.gencidev.common.preferences

import android.content.SharedPreferences
import com.layanacomputindo.bukopin_customer.common.preferences.PreferenceData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(
    private val preferences: SharedPreferences
) {

    //account
    var token: String by PreferenceData(preferences, "token", "")
    var token_fcm: String by PreferenceData(preferences, "token_fcm", "")
    var device_id: String by PreferenceData(preferences, "device_id", "")
    var ref_code: String by PreferenceData(preferences, "ref_code", "")

    fun clear() {
        preferences.edit().clear().apply()
    }

}