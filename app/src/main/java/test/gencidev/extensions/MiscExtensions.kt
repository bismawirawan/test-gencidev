package test.gencidev.extensions

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.goGone() {
    if (visibility != View.GONE)
        visibility = View.GONE
}

fun View.goVisible() {
    if (visibility != View.VISIBLE)
        visibility = View.VISIBLE
}

fun isLog(msg: String) {
    Log.e("cek log:", msg)
}