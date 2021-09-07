package com.nb.trackerapp.network

import android.app.Activity
import android.util.Log
import com.nb.trackerapp.R
import com.nb.trackerapp.common.JSONParser
import com.nb.trackerapp.common.`interface`.OnApiResponseListener
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.views.dialogs.Dialog
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class NetworkCall {
    companion object{
        fun enqueueCall(activity: Activity, url: String, responseType:String, responseTag:String,
                        params: HashMap<String,Any>,apiResponseListener: OnApiResponseListener? = null,
                        isProgress:Boolean = true,dialogClickListener: OnDialogClickListener? = null){
            val request = getRequest(activity,responseType,url,params)
            sendNetworkCall(activity,responseTag, request, apiResponseListener, isProgress, dialogClickListener)
        }

        private fun sendNetworkCall(activity: Activity, responseTag:String,request: Request,
                                    apiResponseListener: OnApiResponseListener?,isProgress:Boolean,
                                    dialogClickListener: OnDialogClickListener?){
            if(Connectivity.isOnline(activity)){
                if(isProgress){ Dialog.showProgress(activity) }

                val client = OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .callTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Dialog.progressDialog?.dismiss()
                        e.message?.let { Dialog.showMessage(activity,activity.getString(R.string.alert),it) }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            Dialog.progressDialog?.dismiss()
                            val jsonObject = JSONObject()
                            jsonObject.put("response",response.body?.string())
                            response.close()
                            Log.d("response",jsonObject.toString())
                            if(response.code == 200){ apiResponseListener?.onApiResponse(responseTag,jsonObject) }
                            else{ JSONParser.parseErrorMessage(activity,jsonObject,responseTag,dialogClickListener) }
                        }
                    }
                })
            }else{ Dialog.showMessage(activity,ApiConstants.NO_INTERNET_MSG) }
        }

        private fun getRequest(activity: Activity,responseType: String,url: String,params: HashMap<String, Any>?)
           :Request{
            val requestBody = when(responseType){
                ApiConstants.RESPONSE_TYPE_EMPTY->{
                    FormBody.Builder().build()
                }
                else->{
                    val builder = FormBody.Builder()
                    params?.let {
                        for ((key, value) in it.entries) {
                            builder.add(key, value.toString())
                            //Log.d("response", "$key : $value")
                        }
                    }
                    builder.build()
                }
            }

            return Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
        }
    }
}