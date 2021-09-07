package com.nb.trackerapp.common

import android.app.Activity
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.views.dialogs.Dialog
import org.json.JSONObject

class JSONParser {
    companion object{
        fun parseErrorMessage(activity: Activity,jsonObject: JSONObject,responseTag:String? = null,
                              dialogClickListener: OnDialogClickListener? = null){
            val message = if(!jsonObject.isNull("response")){ jsonObject.getString("response") }
            else{ "Something went wrong" }
            Dialog.showMessage(activity,message,"Alert",responseTag,dialogClickListener)
        }
    }
}