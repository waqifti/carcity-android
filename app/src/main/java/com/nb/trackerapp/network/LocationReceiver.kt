package com.nb.trackerapp.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nb.trackerapp.common.DateTime

class LocationReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("response","signal received at ${DateTime.getCurrentDateTime()}")
        context.startService(Intent(context,LocationService::class.java))
    }
}