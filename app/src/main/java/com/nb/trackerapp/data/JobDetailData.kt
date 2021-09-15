package com.nb.trackerapp.data

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.nb.trackerapp.R
import com.nb.trackerapp.base.AppSession
import com.nb.trackerapp.common.`interface`.OnApiResponseListener
import com.nb.trackerapp.models.Job
import com.nb.trackerapp.network.ApiConstants
import com.nb.trackerapp.network.ApiUrl
import com.nb.trackerapp.network.NetworkCall

class JobDetailData(private val context: Context, view: View, private val apiResponseListener: OnApiResponseListener) {
    private val jobDetailTv = view.findViewById<AppCompatTextView>(R.id.job_detailTv)

    fun bindJobData(job: Job){
        (context as Activity).runOnUiThread {
            jobDetailTv.text = "State : ${job.state}\nDescription :${job.description}\nNotes : ${job.notes}\n" +
                    "Assigned to : ${job.assignedto}"
        }
    }

    fun getJobDetails(jobId:String){
        AppSession.getCurrentUser(context)?.let {
            val params = HashMap<String,Any>()
            params["jobid"] = jobId
            params["${ApiConstants.HEADER}_sessiontoken"] = it.token

            NetworkCall.enqueueCall(context,ApiUrl.getJobDetailsUrl(it.type),ApiConstants.RESPONSE_TYPE_PARAMS,
            ApiConstants.JOB_DETAILS,params,apiResponseListener)
        }
    }
}