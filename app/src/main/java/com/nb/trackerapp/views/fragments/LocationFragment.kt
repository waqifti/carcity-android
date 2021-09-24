package com.nb.trackerapp.views.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.nb.trackerapp.R
import com.nb.trackerapp.base.AppConstants
import com.nb.trackerapp.base.AppSession
import com.nb.trackerapp.common.`interface`.OnApiResponseListener
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.common.`interface`.OnItemSelectedListener
import com.nb.trackerapp.data.LocationData
import com.nb.trackerapp.handler.LocationHandler
import com.nb.trackerapp.network.ApiConstants
import com.nb.trackerapp.network.LocationService
import com.nb.trackerapp.network.MyLocation
import com.nb.trackerapp.views.dialogs.Dialog
import org.json.JSONObject

class LocationFragment : Fragment(),OnApiResponseListener,OnDialogClickListener,OnItemSelectedListener,LocationListener {

    private lateinit var myLocation: MyLocation
    private lateinit var locationData: LocationData
    private var ctx:Context? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppConstants.FRAGMENT_MANAGER = activity?.supportFragmentManager

        // setting location object
        myLocation = MyLocation(ctx as Activity)

        // binding data
        val user = AppSession.getCurrentUser(ctx!!)
        locationData = LocationData(ctx!!,view,myLocation,user,this,this,
            this,this)
        locationData.bindData()

        // starting background service for location updates
        if(user?.type == ApiConstants.USER_TYPE_PROVIDER) {
            ctx!!.startService(Intent(ctx, LocationService::class.java))
        }

        // logout btn
        view.findViewById<AppCompatButton>(R.id.location_logoutBtn).setOnClickListener {
            locationData.moveToAuthenticationActivity()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onResume() {
        super.onResume()
        myLocation.getLocationManager().let {
            if (it.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                waitForLocation(it)
            }else{ getUserCurrentLocation() }
        }
    }

    override fun onApiResponse(responseTag: String, jsonObject: JSONObject) {
        LocationHandler.handleApiResponse(responseTag, jsonObject, locationData)
    }

    override fun onDialogClick(dialogTag: String?, data: Any?) {
        LocationHandler.handleDialogResponse(ctx as Activity,dialogTag,locationData)
    }

    override fun onItemSelected(tag: String, data: Any?) {
        LocationHandler.handleItemSelectedResponse(tag,data,locationData)
    }

    override fun onLocationChanged(location: Location) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //super.onStatusChanged(provider, status, extras)
    }

    override fun onProviderDisabled(provider: String) {
        //super.onProviderDisabled(provider)
    }

    override fun onProviderEnabled(provider: String) {
        //super.onProviderEnabled(provider)
    }

    private fun getUserCurrentLocation(){
        val locationData = myLocation.getCurrentLocation()
        val location = locationData["location"] as Location?
        val locationProvider = locationData["locationProvider"].toString()
        location?.let {
            MyLocation.updateLocation(ctx!!,it,locationProvider,this)
        } ?:run {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                Dialog.showMessage(ctx!!,getString(R.string.on_location),"Alert",
                    ApiConstants.GET_LOCATION, this)
            },200)
        }
    }

    @SuppressLint("MissingPermission")
    private fun waitForLocation(locationManager: LocationManager){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 1F, this)
        Dialog.showProgress(ctx!!,getString(R.string.wait_location))
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            getLocation(locationManager)
        },2000)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(locationManager: LocationManager){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 1F, this)
        val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0F, this)
        val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        val location = gpsLocation ?: networkLocation
        val locationProvider = MyLocation.getLocationProvider(gpsLocation)
        location?.let {
            Dialog.progressDialog?.dismiss()
            if(AppConstants.sendLocationForFirstTime) {
                AppConstants.sendLocationForFirstTime = false
                MyLocation.updateLocation(ctx!!, it, locationProvider, this)
            }
        } ?: run{
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                getLocation(locationManager)
            },1000)
        }
    }
}