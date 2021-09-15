package com.nb.trackerapp.network

import android.content.Context
import android.util.Log
import com.nb.trackerapp.common.JSONParser
import com.nb.trackerapp.common.StringUtils
import com.nb.trackerapp.common.`interface`.OnApiResponseListener
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.views.dialogs.Dialog
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class NetworkCall {
    companion object{
        fun enqueueCall(context: Context, url: String, responseType:String, responseTag:String,
                        params: HashMap<String,Any>,apiResponseListener: OnApiResponseListener? = null,
                        isProgress:Boolean = true,dialogClickListener: OnDialogClickListener? = null){
            val request = getRequest(responseType,url,params)
            sendNetworkCall(context,responseTag, request, apiResponseListener, isProgress, dialogClickListener)
        }

        private fun sendNetworkCall(context: Context, responseTag:String,request: Request,
                                    apiResponseListener: OnApiResponseListener?,isProgress:Boolean,
                                    dialogClickListener: OnDialogClickListener?){
            if(Connectivity.isOnline(context)){
                if(isProgress){ Dialog.showProgress(context) }

                val connectionTime = if(responseTag == ApiConstants.UPDATE_LOCATION){ 2L } else{ 50L }
                val client = OkHttpClient.Builder()
                    .connectTimeout(connectionTime, TimeUnit.SECONDS)
                    .callTimeout(connectionTime, TimeUnit.SECONDS)
                    .writeTimeout(connectionTime, TimeUnit.SECONDS)
                    .readTimeout(connectionTime, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Dialog.progressDialog?.dismiss()
                        if(responseTag != ApiConstants.UPDATE_LOCATION){
                            e.message?.let { Dialog.showMessage(context,it) }
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            Dialog.progressDialog?.dismiss()
                            val jsonObject = JSONObject()
                            jsonObject.put("response",response.body?.string())
                            response.close()
                            Log.d("response",jsonObject.toString())
                            if(response.code == 200){
                                if(responseTag == ApiConstants.UPDATE_LOCATION){ Log.d("response","location updated") }
                                apiResponseListener?.onApiResponse(responseTag,jsonObject)
                            }
                            else{ JSONParser.parseErrorMessage(context,jsonObject,responseTag,dialogClickListener) }
                        }
                    }
                })
            }else{ if (responseTag != ApiConstants.UPDATE_LOCATION){ Dialog.showMessage(context,ApiConstants.NO_INTERNET_MSG) } }
        }

        private fun getRequest(responseType: String,url: String,params: HashMap<String, Any>?)
           :Request{
            Log.d("response","data : $params")
            var headerKey = ""
            var headerValue = ""
            val requestBody = when(responseType){
                ApiConstants.RESPONSE_TYPE_EMPTY->{
                    FormBody.Builder().build()
                }
                else->{
                    val builder = FormBody.Builder()
                    params?.let {
                        for ((key, value) in it.entries) {
                            if(key.contains(ApiConstants.HEADER)){
                                headerKey = key.substringAfter("_")
                                headerValue = value.toString()
                            }else{
                                builder.add(key, value.toString())
                            }
                        }
                    }
                    builder.build()
                }
            }

            return if(StringUtils.isNotEmpty(headerKey)){
                Request.Builder()
                    .header(headerKey,headerValue)
                    .url(url)
                    .post(requestBody)
                    .build()
            }else{
                Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()
            }
        }
    }
}