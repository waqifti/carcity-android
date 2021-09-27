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
    private val jobDetailTv2 = view.findViewById<AppCompatTextView>(R.id.job_detailTv2)

    fun bindJobData(job: Job){
        (context as Activity).runOnUiThread {
            if(job.assignedto.isNullOrBlank()){
                jobDetailTv.text = "Waiting for Service Provider to be assigned."
            } else {
                if(job.assignedtodetails.currentlongi.isNaN()){
                    jobDetailTv.text = "State : ${job.state}\nAssigned to : ${job.assignedto}\n" +
                            "Current Location : Location Not Available."
                } else {

                    //http://maps.google.com/maps?q=loc:31.47727302275598,74.39065941609442

                    jobDetailTv.text = "State : ${job.state}\nAssigned to : ${job.assignedto}\n\n Click below to track the service provider assigned to you"
                    jobDetailTv2.text = "http://maps.google.com/maps?q=loc:${job.assignedtodetails.currentlati},${job.assignedtodetails.currentlongi}"
                }

            }

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