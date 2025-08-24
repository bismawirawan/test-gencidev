package test.gencidev.module

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

abstract class BaseActivity : AppCompatActivity() {

    fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
        liveData.observe(this, Observer(body))
    }

    inline fun <T : Any, L : LiveData<T>> LifecycleOwner.observeNonNull(
        liveData: L,
        crossinline body: (T) -> Unit
    ) {
        liveData.observe(this, Observer { it?.let(body) })
    }

}