package com.nb.trackerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ProcessLifecycleOwner
import com.nb.trackerapp.base.AppLifeCycleObserver
import com.nb.trackerapp.common.JSONParser
import com.nb.trackerapp.common.`interface`.OnApiResponseListener
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.data.LocationData
import com.nb.trackerapp.network.ApiConstants
import com.nb.trackerapp.network.LocationService
import com.nb.trackerapp.network.MyLocation
import com.nb.trackerapp.views.dialogs.Dialog
import org.json.JSONObject

class MainActivity : AppCompatActivity(),OnDialogClickListener,OnApiResponseListener,LocationListener {

    private lateinit var myLocation: MyLocation
    private lateinit var locationData: LocationData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // binding data
        locationData = LocationData(this,window.decorView,this)
        locationData.bindData()


        // setting location object
        myLocation = MyLocation(this)

        // logout btn
        findViewById<AppCompatButton>(R.id.logout_btn).setOnClickListener {
            locationData.moveToAuthenticationActivity()
        }

        // initializing app state
        val appLifeCycleObserver = AppLifeCycleObserver()
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifeCycleObserver)
    }

    override fun onResume() {
        super.onResume()
        if(ApiConstants.IS_LOCATION_ENABLED) {
            myLocation.getLocationManager().let {
                if (it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    ApiConstants.IS_LOCATION_ENABLED = false
                    waitForLocation(it)
                    locationData.startLocationService()
                }else{ getUserCurrentLocation() }
            }
        }else{ getUserCurrentLocation() }
    }

    override fun onApiResponse(responseTag: String, jsonObject: JSONObject) {
        when(responseTag){
            ApiConstants.JOB_TYPE_LIST->{
                val jobList = JSONParser.parseJobTypeList(jsonObject)
                jobList?.let { locationData.bindJobTypes(it) }
            }
        }
    }

    override fun onDialogClick(dialogTag: String?, data: Any?) {
        when(dialogTag){
            ApiConstants.GET_LOCATION->{
                ApiConstants.IS_LOCATION_ENABLED = true
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        locationData.bindLocation(location)
        MyLocation.updateLocation(this,location)
        Log.d("response","updated location : ${location.latitude} :: ${location.longitude}")
    }

    private fun getUserCurrentLocation(){
        val location = myLocation.getCurrentLocation()
        location?.let {
            locationData.bindLocation(it)
            MyLocation.updateLocation(this,it)
            startService(Intent(this,LocationService::class.java))
            Log.d("response","current location : ${it.latitude} :: ${it.longitude}")
        } ?:run {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                Dialog.showMessage(this,getString(R.string.on_location),"Alert",
                ApiConstants.GET_LOCATION,this)
            },200)
        }
    }

    @SuppressLint("MissingPermission")
    private fun waitForLocation(locationManager: LocationManager){
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0F, this)
        Dialog.showProgress(this,getString(R.string.wait_location))
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            Dialog.progressDialog?.dismiss()
            getLocation(locationManager)
        },2000)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(locationManager: LocationManager){
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0F, this)
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        location?.let {
            locationData.bindLocation(it)
            MyLocation.updateLocation(this,it)
            Log.d("response","location : ${it.latitude} :: ${it.longitude}")
        } ?: run{
            Dialog.showMessage(this,getString(R.string.no_location),"Alert",
                ApiConstants.GET_LOCATION,this)
        }
    }
}