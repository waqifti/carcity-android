package com.nb.trackerapp.network

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.nb.trackerapp.common.DateTime

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class LocationJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("response","job scheduled at : ${DateTime.getCurrentDateTime()}")
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}