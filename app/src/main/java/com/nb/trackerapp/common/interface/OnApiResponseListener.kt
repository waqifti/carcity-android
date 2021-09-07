package com.nb.trackerapp.common.`interface`

import org.json.JSONObject

interface OnApiResponseListener {
    fun onApiResponse(responseTag:String,jsonObject: JSONObject)
}