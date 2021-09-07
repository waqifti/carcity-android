package com.nb.trackerapp.network

import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context

class LocationScheduler {
    companion object{
        fun scheduleJob(activity: Activity){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val jobScheduler = activity.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                val componentName = ComponentName(activity,LocationJobService::class.java)
                val jobInfo = JobInfo.Builder(12,componentName)
                    .setPeriodic(5000)
                    .build()
                jobScheduler.schedule(jobInfo)
            }
        }
    }
}