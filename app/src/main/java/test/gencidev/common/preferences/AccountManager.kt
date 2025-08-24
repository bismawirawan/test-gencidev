package test.gencidev.common.preferences

import test.gencidev.common.preferences.Preferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManager @Inject constructor(
    private val preferences: Preferences
) {

    fun logOut() {
        preferences.clear()
    }

}