package com.nb.trackerapp.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
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
import com.nb.trackerapp.base.AppIntents
import com.nb.trackerapp.base.AppSession
import com.nb.trackerapp.common.DateTime
import com.nb.trackerapp.common.`interface`.OnApiResponseListener
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.models.Job
import com.nb.trackerapp.models.User
import com.nb.trackerapp.network.*
import com.nb.trackerapp.views.activities.AuthenticationActivity
import com.nb.trackerapp.views.adapters.JobsDropDownAdapter

class LocationData(private val context: Context,view: View,private val apiResponseListener: OnApiResponseListener,
                   private val dialogClickListener: OnDialogClickListener) {
    private val customerLayout = view.findViewById<LinearLayout>(R.id.customerLayout)
    private val jobIdEt = view.findViewById<AppCompatEditText>(R.id.job_idEt)
    private val descriptionEt = view.findViewById<AppCompatEditText>(R.id.descriptionEt)
    private val notesEt = view.findViewById<AppCompatEditText>(R.id.notesEt)
    private val timeEt = view.findViewById<AppCompatEditText>(R.id.timeEt)
    private val findBtn = view.findViewById<AppCompatButton>(R.id.find_btn)
    private val jobIddRecyclerView = view.findViewById<RecyclerView>(R.id.job_iddRecyclerView)
    private val versionTv = view.findViewById<AppCompatTextView>(R.id.main_versionTv)

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
    }

    fun bindJobTypes(jobList:ArrayList<Job>){
        (context as Activity).runOnUiThread {
            val adapter = JobsDropDownAdapter(context,jobList)
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
}