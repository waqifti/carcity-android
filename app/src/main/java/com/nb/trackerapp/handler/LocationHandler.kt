package com.nb.trackerapp.handler

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import com.nb.trackerapp.common.JSONParser
import com.nb.trackerapp.data.LocationData
import com.nb.trackerapp.network.ApiConstants
import org.json.JSONObject

class LocationHandler {
    companion object{
        fun handleDialogResponse(activity: Activity,dialogTag:String?,locationData:LocationData){
            when(dialogTag){
                ApiConstants.GET_LOCATION->{
                    activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                ApiConstants.SESSION_EXPIRED->{ locationData.moveToAuthenticationActivity() }
            }
        }

        fun handleApiResponse(responseTag:String,jsonObject:JSONObject,locationData: LocationData){
            when(responseTag){
                ApiConstants.JOB_TYPE_LIST->{
                    val jobList = JSONParser.parseJobTypeList(jsonObject)
                    jobList?.let { locationData.bindJobTypes(it) }
                }
                ApiConstants.CUSTOMER_JOB_REQUEST->{
                    val jobId = jsonObject.getString("response")
                    locationData.moveToJobDetailFragment(jobId)
                }
            }
        }
    }
}