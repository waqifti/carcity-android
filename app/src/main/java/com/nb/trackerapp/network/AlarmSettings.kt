package com.nb.trackerapp.network

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nb.trackerapp.common.DateTime
import java.util.*

class AlarmSettings {
    companion object{
        @SuppressLint("ShortAlarm")
        fun setAlarm(context: Context){
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context,AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT)

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.SECOND,2)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),2000,
                pendingIntent)
            Log.d("response","alarm set at ${DateTime.getCurrentDateTime()}")
        }
    }
}