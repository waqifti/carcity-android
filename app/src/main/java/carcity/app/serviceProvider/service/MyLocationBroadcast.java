package carcity.app.serviceProvider.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class MyLocationBroadcast extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE = "carcity.app.serviceProvider.service.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result != null){
                    Location location = result.getLastLocation();
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    String location_string = "Long: "+longitude+", Lat: "+latitude;
                    try {
                        ServiceProviderHome.getInstance().updateTextView(longitude, latitude);
                    } catch (Exception e){
                        Toast.makeText(context, location_string, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}