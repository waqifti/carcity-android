package com.nb.trackerapp.common

import android.app.Activity
import android.content.Context
import android.util.Log
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.models.Job
import com.nb.trackerapp.network.ApiConstants
import com.nb.trackerapp.views.dialogs.Dialog
import org.json.JSONObject

class JSONParser {
    companion object{
        fun parseErrorMessage(context: Context,jsonObject: JSONObject,responseTag:String? = null,
                              dialogClickListener: OnDialogClickListener? = null){
            if(responseTag != ApiConstants.UPDATE_LOCATION){
                val message = if(!jsonObject.isNull("response")){ jsonObject.getString("response") }
                else{ "Something went wrong" }
                Dialog.showMessage(context,message,"Alert",responseTag,dialogClickListener)
            }
        }

        fun parseJobTypeList(jsonObject: JSONObject) : ArrayList<Job>?{
            var jobList:ArrayList<Job>? = null
            if(!jsonObject.isNull("response")){
                jobList = ArrayList()
                val jsonArray = jsonObject.getString("response").replace("[","")
                    .replace("]","").replace("\"","").split(",")
                jsonArray.forEach { jobList.add(Job(it)) }
            }

            return jobList
        }
    }
}