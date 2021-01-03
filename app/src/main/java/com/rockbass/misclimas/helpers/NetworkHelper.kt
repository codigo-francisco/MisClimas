package com.rockbass.misclimas.helpers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

object NetworkHelper {
    fun hasNetwork(context: Context): Boolean {
        var isConnected = false // Initial Value
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }else{
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
        }

        return isConnected
    }
}