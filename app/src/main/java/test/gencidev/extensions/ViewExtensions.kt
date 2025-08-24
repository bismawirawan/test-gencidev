package test.gencidev.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

fun View.snackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun Int.drawable(resources: Resources) : Drawable? {
    return ResourcesCompat.getDrawable(resources, this, null)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Context.hideKeyboard(view: View) {
    view.hideKeyboard()
}

fun ComponentActivity.bindKeyboardStateEvents() {
    lifecycle.addObserver(ViewGroupHolder(findViewById(Window.ID_ANDROID_CONTENT)))
}

fun ViewGroup.isKeyboardOpen(visibleThreshold: Float = 100f): Boolean {
    val measureRect = Rect()
    getWindowVisibleDisplayFrame(measureRect)
    return rootView.height - measureRect.bottom > ceil((visibleThreshold * Resources.getSystem().displayMetrics.density))
}

val TextView.string: String
    get() = text.toString()

val TextView.int: Int
    get() = text.toString().toIntOrNull() ?: 0

val TextView.float: Float
    get() = text.toString().toFloatOrNull() ?: 0f

val TextView.double: Double
    get() = text.toString().toDoubleOrNull() ?: 0.0

enum class KeyboardState { OPEN, CLOSED }

object KeyboardStateLiveData {
    private val _state = MutableLiveData<KeyboardState>()
    val state: LiveData<KeyboardState> = _state

    fun post(state: KeyboardState) {
        _state.postValue(state)
    }
}

fun View.goInvisible() {
    if (visibility != View.INVISIBLE)
        visibility = View.INVISIBLE
}

fun View.goGone() {
    if (visibility != View.GONE)
        visibility = View.GONE
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.isGone(): Boolean {
    return visibility == View.GONE
}

fun View.goVisible() {
    if (visibility != View.VISIBLE)
        visibility = View.VISIBLE
}

private class ViewGroupHolder(private val root: ViewGroup) : LifecycleEventObserver {
    private val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
        private var previous: Boolean = root.isKeyboardOpen()

        override fun onGlobalLayout() {
            root.isKeyboardOpen().let {
                if (it != previous) {
                    KeyboardStateLiveData.post(if (it) KeyboardState.OPEN else KeyboardState.CLOSED)
                    previous = previous.not()
                }
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_PAUSE) {
            unregisterKeyboardListener()
        } else if (event == Lifecycle.Event.ON_RESUME) {
            registerKeyboardListener()
        }
    }

    private fun registerKeyboardListener() {
        root.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    private fun unregisterKeyboardListener() {
        root.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

}

fun View.setColor(@ColorRes colorRes: Int) {
    return this.setBackgroundColor(ContextCompat.getColor(context, colorRes))
}

fun TextView.setColor(@ColorRes colorRes: Int) {
    return this.setTextColor(ContextCompat.getColor(context, colorRes))
}

fun TextView.textCoret() {
    this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

fun getTimeNow():String{
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val now = Date()
    return sdf.format(now)
}

fun getDateNow():String{
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val now = Date()
    return sdf.format(now)
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}