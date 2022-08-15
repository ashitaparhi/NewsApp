package com.ashita.news.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.ashita.news.NewsApplication

class NetworkUtils {

    companion object {
        fun getNetworkStatus(): Boolean {
            var isConnected = false

            val connectivityManager =
                NewsApplication.appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    isConnected = true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    isConnected = true
                }
            }
            return isConnected
        }
    }
}
