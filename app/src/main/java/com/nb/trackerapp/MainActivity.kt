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
import com.nb.trackerapp.base.AppIntents
import com.nb.trackerapp.base.AppSession
import com.nb.trackerapp.common.DateTime
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.network.AlarmSettings
import com.nb.trackerapp.network.ApiConstants
import com.nb.trackerapp.network.LocationScheduler
import com.nb.trackerapp.network.MyLocation
import com.nb.trackerapp.views.activities.AuthenticationActivity
import com.nb.trackerapp.views.dialogs.Dialog

class MainActivity : AppCompatActivity(),OnDialogClickListener,LocationListener {

    private lateinit var myLocation: MyLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setting location object
        myLocation = MyLocation(this)
        Log.d("response","user ${AppSession.getCurrentUser(this)}")

        // setting alarm
        //LocationScheduler.scheduleJob(this)
        AlarmSettings.setAlarm(this)

        // logout btn
        findViewById<AppCompatButton>(R.id.logout_btn).setOnClickListener {
            moveToAuthenticationActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        if(ApiConstants.IS_LOCATION_ENABLED) {
            myLocation.getLocationManager().let {
                if (it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    ApiConstants.IS_LOCATION_ENABLED = false
                    waitForLocation(it)
                }else{ getUserCurrentLocation() }
            }
        }else{ getUserCurrentLocation() }
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
        Log.d("response","updated location : ${location.latitude} :: ${location.longitude}")
    }

    private fun getUserCurrentLocation(){
        val location = myLocation.getCurrentLocation()
        location?.let {
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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5F, this)
        Dialog.showProgress(this,getString(R.string.wait_location))
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            Dialog.progressDialog?.dismiss()
            getLocation(locationManager)
        },2000)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(locationManager: LocationManager){
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5F, this)
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        location?.let {
            Log.d("response","location : ${it.latitude} :: ${it.longitude}")
        } ?: run{
            Dialog.showMessage(this,getString(R.string.no_location),"Alert",
                ApiConstants.GET_LOCATION,this)
        }
    }

    private fun moveToAuthenticationActivity(){
        AppSession.setLoginStatus(this,false)
        AppIntents.moveToActivity(this,true, Intent(this,AuthenticationActivity::class.java))
    }
}