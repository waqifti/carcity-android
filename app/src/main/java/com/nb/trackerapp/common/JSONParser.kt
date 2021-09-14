package com.nb.trackerapp.common

import android.content.Context
import com.nb.trackerapp.R
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.models.Job
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