package test.gencidev.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build

object NetworkConnectionStatus {

    fun onAvailable(context: Context): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val connectionManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connectionManager.activeNetworkInfo
            return networkInfo?.isConnected == true
        } else {
            return false
        }

    }

}