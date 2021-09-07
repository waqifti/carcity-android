package com.nb.trackerapp.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nb.trackerapp.common.DateTime

class AlarmReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("response","alarm received at ${DateTime.getCurrentDateTime()}")
    }
}