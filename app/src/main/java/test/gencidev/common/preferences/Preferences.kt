package test.gencidev.common.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(
    private val preferences: SharedPreferences
) {

    fun clear() {
        preferences.edit().clear().apply()
    }

}