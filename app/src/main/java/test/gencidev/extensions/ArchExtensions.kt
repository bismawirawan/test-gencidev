package test.gencidev.extensions

import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.Executors
import java.util.regex.Pattern

fun <T : Any, L : LiveData<T>> Fragment.observe(liveData: L, body: (T?) -> Unit) {
    liveData.observe(viewLifecycleOwner, Observer(body))
}

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
    liveData.observe(this, Observer(body))
}

fun <T : Any, L : LiveData<T>> observeOnce(liveData: L, body: (T) -> Unit) {
    liveData.observeForever(object : Observer<T> {
        override fun onChanged(value: T) {
            value.let {
                body(it)
                liveData.removeObserver(this)
            }
        }
    })
}

//===== VISIBILITY =====//

const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L

/** Combination of all flags required to put activity into immersive mode */
const val FLAGS_FULLSCREEN =
    View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

fun ImageButton.simulateClick(delay: Long = ANIMATION_FAST_MILLIS) {
    performClick()
    isPressed = true
    invalidate()
    postDelayed({
        invalidate()
        isPressed = false
    }, delay)
}

fun String.isValidEmail(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun hasLength(data: CharSequence): Boolean {
    return data.toString().length >= 8
}

fun hasSymbol(data: CharSequence): Boolean {
//    val password = data.toString()
//    val pattern = Regex("[*0-9]")
//    return !password.matches(pattern)
    val regex = "(.)*(\\d)(.)*"
    val pattern: Pattern = Pattern.compile(regex)
    return pattern.matcher(data.toString()).matches()
}

fun hasUpperCase(data: CharSequence): Boolean {
    val password = data.toString()
    return password != password.lowercase()
}

fun hasLowerCase(data: CharSequence): Boolean {
    val password = data.toString()
    return password != password.uppercase()
}

fun Int?.toIntWithSafety(): Int {
    return if (this != null) {
        try {
            this.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            0
        }
    } else {
        0
    }
}

fun String?.toIntWithSafety(): Int {
    return if (this != null) {
        if (this.isEmpty()) {
            0
        } else {
            try {
                this.toInt()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                0
            }
        }
    } else {
        0
    }
}

fun Double?.toIntWithSafety(): Int {
    return if (this != null) {
        try {
            this.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            0
        }
    } else {
        0
    }
}

fun Float?.toIntWithSafety(): Int {
    return if (this != null) {
        try {
            this.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            0
        }
    } else {
        0
    }
}

fun Long?.toIntWithSafety(): Int {
    return if (this != null) {
        try {
            this.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            0
        }
    } else {
        0
    }
}

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}