package com.nb.trackerapp.common

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nb.trackerapp.R
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.models.Job
import com.nb.trackerapp.models.JobType
import com.nb.trackerapp.network.ApiConstants
import com.nb.trackerapp.views.dialogs.Dialog
import org.json.JSONObject

class JSONParser {
    companion object{
        fun parseErrorMessage(context: Context,jsonObject: JSONObject,responseTag:String? = null,
                              dialogClickListener: OnDialogClickListener? = null){
            var dialogTag:String? = null
            val message = if(!jsonObject.isNull("response")){
                when (val responseMessage = jsonObject.getString("response")) {
                    ApiConstants.SESSION_EXPIRED_MSG -> {
                        dialogTag = ApiConstants.SESSION_EXPIRED
                        context.getString(R.string.session_expired)
                    }
                    else -> {
                        dialogTag = responseTag
                        responseMessage
                    }
                }
            }else{ "Something went wrong" }
            dialogClickListener?.let { Dialog.showMessage(context,message,"Alert",dialogTag,it) }
        }

        fun parseJobTypeList(jsonObject: JSONObject) : ArrayList<JobType>?{
            var jobTypeList:ArrayList<JobType>? = null
            if(!jsonObject.isNull("response")){
                jobTypeList = ArrayList()
                val jsonArray = jsonObject.getString("response").replace("[","")
                    .replace("]","").replace("\"","").split(",")
                jsonArray.forEach { jobTypeList.add(JobType(it)) }
            }

            return jobTypeList
        }

        fun parseJobObject(jsonObject: JSONObject) : Job?{
            var job:Job? = null

            try {
                if(!jsonObject.isNull("response")){
                    val obj = jsonObject.getString("response")
                    val jobObject = JSONObject(obj)

                    val gson = Gson()
                    job = gson.fromJson(jobObject.toString(),Job::class.java)
                }
            }catch(e: Exception){

                //show toast/snackabar/log here
            }

            return job
        }
    }
}