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
        locationData = LocationData(this,window.decorView,this,this)
        locationData.bindData()

        // setting location object
        myLocation = MyLocation(this)
        startService(Intent(this, LocationService::class.java))

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
        myLocation.getLocationManager().let {
            if (it.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                waitForLocation(it)
            }else{ getUserCurrentLocation() }
        }
    }

    override fun onPause() {
        super.onPause()
        Dialog.progressDialog?.dismiss()
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
            ApiConstants.SESSION_EXPIRED->{ locationData.moveToAuthenticationActivity() }
        }
    }

    override fun onLocationChanged(location: Location) {
        Dialog.progressDialog?.dismiss()
        //MyLocation.updateLocation(this,location,this)
        Log.d("response","updated location : ${location.latitude} :: ${location.longitude}")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //super.onStatusChanged(provider, status, extras)
        Log.d("response","onStatusChanged : $provider == status : $status")
    }

    override fun onProviderDisabled(provider: String) {
        //super.onProviderDisabled(provider)
        Log.d("response","onProviderDisabled : $provider")
    }

    override fun onProviderEnabled(provider: String) {
        //super.onProviderEnabled(provider)
        Log.d("response","onProviderEnabled : $provider")
    }

    private fun getUserCurrentLocation(){
        val location = myLocation.getCurrentLocation()
        location?.let {
            MyLocation.updateLocation(this,it,this)
            Log.d("response","current location : ${it.latitude} :: ${it.longitude}")
        } ?:run {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                Dialog.showMessage(this,getString(R.string.on_location),"Alert",
                    ApiConstants.GET_LOCATION, this)
            },200)
        }
    }

    @SuppressLint("MissingPermission")
    private fun waitForLocation(locationManager: LocationManager){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 1F, this)
        Dialog.showProgress(this,getString(R.string.wait_location))
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            getLocation(locationManager)
        },3000)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(locationManager: LocationManager){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 1F, this)
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1F, this)
        //val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        location?.let {
            Dialog.progressDialog?.dismiss()
            MyLocation.updateLocation(this,it,this)
            Log.d("response","location : ${it.latitude} :: ${it.longitude}")
        } ?: run{
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                getLocation(locationManager)
            },500)
        }
    }
}