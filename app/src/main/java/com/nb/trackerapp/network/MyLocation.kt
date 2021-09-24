package com.nb.trackerapp.network

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.nb.trackerapp.base.AppConstants
import com.nb.trackerapp.base.AppSession
import com.nb.trackerapp.common.DateTime
import com.nb.trackerapp.common.`interface`.OnDialogClickListener

class MyLocation(private val activity: Activity) {
    fun getCurrentLocation(): HashMap<String,Any?>{
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Grant permission
            return HashMap()
        }

        val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val locationData = HashMap<String,Any?>()
        locationData["location"] = gpsLocation ?: networkLocation
        locationData["locationProvider"] = getLocationProvider(gpsLocation)
        return locationData
    }

    fun getLocationManager(): LocationManager {
        return activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun getGpsLocation(context: Context,locationListener: LocationListener) : Location?{
        val locationManager = getLocationManager()
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 1F, locationListener)
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

    companion object{
        fun updateLocation(context: Context,location:Location?,locationProvider:String,
                           dialogClickListener: OnDialogClickListener? = null){
            location?.let {
                AppSession.getCurrentUser(context)?.let { user ->
                    val params = HashMap<String,Any>()
                    params["appstate"] = AppConstants.APP_STATE
                    params["lati"] = it.latitude
                    params["longi"] = it.longitude
                    params["locationprovider"] = locationProvider
                    params["${ApiConstants.HEADER}_sessiontoken"] = user.token
                    params["time"] = DateTime.getCurrentDateTime()

                    NetworkCall.enqueueCall(context,ApiUrl.getUpdateLocationUrl(user.type),ApiConstants.RESPONSE_TYPE_PARAMS,
                        ApiConstants.UPDATE_LOCATION,params,null,false,dialogClickListener)
                }
            }
        }

        fun getLocationProvider(gpsLocation:Location?):String{
            return gpsLocation?.let { ApiConstants.PROVIDER_GPS } ?: run { ApiConstants.PROVIDER_NETWORK }
        }
    }
}