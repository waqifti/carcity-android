package com.nb.trackerapp.network

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.nb.trackerapp.base.AppSession
import java.lang.Exception
import java.lang.UnsupportedOperationException

class LocationService : Service(),LocationListener {

    private var userLocation:Location? = null
    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // setting delay time according to user type
        val userType = AppSession.getCurrentUser(this)?.type
        val delayTime = if(userType == ApiConstants.USER_TYPE_CUSTOMER){ 300000L }
        else{ 60000L }
        getLocation(userType,delayTime,5000)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onLocationChanged(location: Location) {
        userLocation = location
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //super.onStatusChanged(provider, status, extras)
        Log.d("response","service onStatusChanged : $provider == status : $status")
    }

    override fun onProviderDisabled(provider: String) {
        //super.onProviderDisabled(provider)
        Log.d("response","service onProviderDisabled : $provider")
    }

    override fun onProviderEnabled(provider: String) {
        //super.onProviderEnabled(provider)
        Log.d("response","service onProviderEnabled : $provider")
    }

    private fun updateUserLocation(){
        Log.d("response","update location called from service : $userLocation")
        MyLocation.updateLocation(this,userLocation)
        sendSignalToReceiver()
        stopSelf()
    }

    private fun sendSignalToReceiver(){
        sendBroadcast(Intent(this,LocationReceiver::class.java))
    }

    private fun getLocation(userType:String?,totalDelayTime:Long,delayInterval:Long){
        requestLocation()
        Thread(Runnable {
            try{ Thread.sleep(5000) }
            catch (e:Exception){}
            finally {
                if(delayInterval == totalDelayTime){ updateUserLocation() }
                else{ getLocation(userType, totalDelayTime, delayInterval+5000) }
            }
        }).start()
    }

    private fun requestLocation(){
        Handler(Looper.getMainLooper()).post(Runnable {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 1F, this)
            val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 2F, this)
            //val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            Log.d("response","gpsLocation : $gpsLocation")
            userLocation = gpsLocation
        })
    }
}