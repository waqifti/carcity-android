package com.nb.trackerapp.common

import android.app.Activity
import android.content.Context
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
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
    }
}