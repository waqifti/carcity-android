package com.nb.trackerapp.network

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
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
        val userType = AppSession.getCurrentUser(this)?.type
        getLocation(userType)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onLocationChanged(location: Location) {
        userLocation = location
    }

    private fun updateUserLocation(userType: String?){
        MyLocation.updateLocation(this,userLocation)
        // setting delay time according to user type
        val delayTime = if(userType == ApiConstants.USER_TYPE_CUSTOMER){ 180000L }
        else{ 3000L }
        Thread(Runnable {
            try {
                Thread.sleep(delayTime)
            }catch (e:Exception){}
            finally {
                sendSignalToReceiver()
                stopSelf()
            }
        }).start()
    }

    private fun sendSignalToReceiver(){
        sendBroadcast(Intent(this,LocationReceiver::class.java))
    }

    private fun getLocation(userType:String?){
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0F, this)
        userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        // setting delay time according to user type
        val delayTime = if(userType == ApiConstants.USER_TYPE_CUSTOMER){ 120000L }
                        else{ 2000L }

        Thread(Runnable {
            try{ Thread.sleep(delayTime) }
            catch (e:Exception){}
            finally { updateUserLocation(userType) }
        }).start()
    }
}