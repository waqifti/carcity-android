package com.nb.trackerapp.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.nb.trackerapp.R
import com.nb.trackerapp.base.AppConstants
import com.nb.trackerapp.common.JSONParser
import com.nb.trackerapp.common.`interface`.OnApiResponseListener
import com.nb.trackerapp.data.JobDetailData
import com.nb.trackerapp.network.ApiConstants
import org.json.JSONObject

class JobDetailFragment(private val jobId:String) : Fragment(),OnApiResponseListener{

    private var ctx:Context? = null
    private lateinit var jobDetailData: JobDetailData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_job_details,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppConstants.FRAGMENT_MANAGER = activity?.supportFragmentManager

        // binding object
        jobDetailData = JobDetailData(ctx!!,view,this)

        // view job detail btn
        view.findViewById<AppCompatButton>(R.id.job_detailBtn).setOnClickListener {
            jobDetailData.getJobDetails(jobId)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onApiResponse(responseTag: String, jsonObject: JSONObject) {
        when(responseTag){
            ApiConstants.JOB_DETAILS->{
                val job = JSONParser.parseJobObject(jsonObject)
                job?.let { jobDetailData.bindJobData(it) }
            }
        }
    }
}