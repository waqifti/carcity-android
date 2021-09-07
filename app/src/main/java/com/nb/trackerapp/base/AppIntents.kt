package com.nb.trackerapp.base

import android.app.Activity
import android.content.Intent

class AppIntents {
    companion object{
        fun moveToActivity(activity: Activity, isFinish:Boolean, intent: Intent){
            activity.runOnUiThread {
                if (isFinish) {activity.finish()}
                activity.startActivity(intent)
            }
        }
    }
}