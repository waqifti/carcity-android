package com.nb.trackerapp.network

import android.content.Context
import android.net.ConnectivityManager

class Connectivity {
    companion object{
        fun isOnline(context: Context):Boolean{
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                manager.activeNetwork
            } else {
                manager.activeNetworkInfo
            }

            return networkInfo != null
        }
    }
}