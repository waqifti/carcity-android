package com.nb.trackerapp.network

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.nb.trackerapp.base.AppConstants
import com.nb.trackerapp.base.AppSession
import com.nb.trackerapp.common.DateTime

class MyLocation(private val activity: Activity) {
    fun getCurrentLocation(): Location?{
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Grant permission
            return null
        }
        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }

    fun getLocationManager(): LocationManager {
        return activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    companion object{
        fun updateLocation(context: Context,location:Location?){
            location?.let {
                AppSession.getCurrentUser(context)?.let { user ->
                    val params = HashMap<String,Any>()
                    params["appstate"] = AppConstants.APP_STATE
                    params["lati"] = it.latitude
                    params["longi"] = it.longitude
                    params["${ApiConstants.TOKEN}_sessiontoken"] = user.token
                    params["time"] = DateTime.getCurrentDateTime()

                    NetworkCall.enqueueCall(context,ApiUrl.getUpdateLocationUrl(user.type),ApiConstants.RESPONSE_TYPE_PARAMS,
                        ApiConstants.UPDATE_LOCATION,params,null,false)
                }
            }
        }
    }
}