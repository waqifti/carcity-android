package com.nb.trackerapp.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nb.trackerapp.R
import com.nb.trackerapp.base.AppConstants
import com.nb.trackerapp.base.AppIntents
import com.nb.trackerapp.base.AppSession
import com.nb.trackerapp.common.DateTime
import com.nb.trackerapp.common.`interface`.OnApiResponseListener
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.common.showErrorMessage
import com.nb.trackerapp.models.JobType
import com.nb.trackerapp.models.User
import com.nb.trackerapp.network.*
import com.nb.trackerapp.views.activities.AuthenticationActivity
import com.nb.trackerapp.views.adapters.JobsDropDownAdapter
import com.nb.trackerapp.views.fragments.JobDetailFragment

class LocationData(private val context: Context,private val view: View,private val apiResponseListener: OnApiResponseListener,
                   private val dialogClickListener: OnDialogClickListener) {
    private val customerLayout = view.findViewById<LinearLayout>(R.id.location_customerLayout)
    private val jobIdEt = view.findViewById<AppCompatEditText>(R.id.location_job_idEt)
    private val descriptionEt = view.findViewById<AppCompatEditText>(R.id.location_descriptionEt)
    private val notesEt = view.findViewById<AppCompatEditText>(R.id.location_notesEt)
    private val timeEt = view.findViewById<AppCompatEditText>(R.id.location_timeEt)
    private val findBtn = view.findViewById<AppCompatButton>(R.id.location_findBtn)
    private val jobIddRecyclerView = view.findViewById<RecyclerView>(R.id.location_job_iddRecyclerView)
    private val versionTv = view.findViewById<AppCompatTextView>(R.id.location_versionTv)

    fun bindData(){
        val user = AppSession.getCurrentUser(context)
        Log.d("response","user $user")

        // setting views
        jobIdEt.isClickable = true
        jobIdEt.isFocusable = false
        jobIdEt.inputType = InputType.TYPE_NULL
        versionTv.text = "Version : ${Build.VERSION.RELEASE}"
        timeEt.setText(DateTime.getCurrentDateTime())
        if(user?.type == ApiConstants.USER_TYPE_CUSTOMER){
            customerLayout.visibility = View.VISIBLE
            findBtn.text = context.getString(R.string.find_service_provider)

            // get job types
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                getJobTypes(user)
            },200)
        } else{
            customerLayout.visibility = View.GONE
            findBtn.text = context.getString(R.string.activate_job_search)
        }

        // job id click
        jobIdEt.setOnClickListener {
            showOrHideDropDown()
        }

        // find btn click
        findBtn.setOnClickListener {
            if(user?.type == ApiConstants.USER_TYPE_CUSTOMER){
                if(validateCustomerData()){ createCustomerJobRequest(user) }
            }
        }
    }

    fun bindJobTypes(jobTypeList:ArrayList<JobType>){
        (context as Activity).runOnUiThread {
            val adapter = JobsDropDownAdapter(context,jobTypeList)
            val layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.VERTICAL

            jobIddRecyclerView.adapter = adapter
            jobIddRecyclerView.layoutManager = layoutManager
            jobIddRecyclerView.itemAnimator = DefaultItemAnimator()
        }
    }

    fun moveToAuthenticationActivity(){
        AppSession.setLoginStatus(context,false)
        AppIntents.moveToActivity(context as Activity,true, Intent(context, AuthenticationActivity::class.java))
    }

    fun moveToJobDetailFragment(jobId:String){
        AppIntents.moveToForwardFragment(view,context,R.id.main_frame,JobDetailFragment(jobId),
            AppConstants.FRAGMENT_MANAGER!!)
    }

    private fun getJobTypes(user: User?){
        user?.let {
            val params = HashMap<String,Any>()
            params["${ApiConstants.HEADER}_sessiontoken"] = it.token

            NetworkCall.enqueueCall(context,ApiUrl.getJobTypesUrl(),ApiConstants.RESPONSE_TYPE_PARAMS,
            ApiConstants.JOB_TYPE_LIST,params,apiResponseListener,false,dialogClickListener)
        }
    }

    private fun showOrHideDropDown(){
        jobIddRecyclerView.visibility = if(jobIddRecyclerView.visibility == View.VISIBLE){ View.GONE }
        else{ View.VISIBLE }
    }

    private fun validateCustomerData() : Boolean{
        return when{
            descriptionEt.text.isNullOrEmpty()->{
                showErrorMessage(context,"Please enter description")
                false
            }
            notesEt.text.isNullOrEmpty()->{
                showErrorMessage(context,"Please enter notes")
                false
            }
            (JobType.selectedJobList.isNullOrEmpty() || JobType.selectedJobList.size == 0)->{
                showErrorMessage(context,"Please select job types")
                false
            }
            else->{ true }
        }
    }

    private fun createCustomerJobRequest(user: User?){
        user?.let {
            val scheduledAt = if(timeEt.text.isNullOrEmpty()){ DateTime.getCurrentDateTime() }else{ timeEt.text.toString() }
            val params = HashMap<String,Any>()
            params["description"] = descriptionEt.text.toString().trim()
            params["jobtypes"] = JobType.selectedJobList
            params["notes"] = notesEt.text.toString().trim()
            params["scheduledAt"] = scheduledAt
            params["${ApiConstants.HEADER}_sessiontoken"] = it.token

            NetworkCall.enqueueCall(context,ApiUrl.getJobRequestUrl(it.type),ApiConstants.RESPONSE_TYPE_PARAMS,
            ApiConstants.CUSTOMER_JOB_REQUEST,params,apiResponseListener,true,dialogClickListener)
        }
    }
}