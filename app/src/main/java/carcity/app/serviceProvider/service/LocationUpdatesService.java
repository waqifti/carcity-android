package carcity.app.serviceProvider.service;


import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import carcity.app.R;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class LocationUpdatesService extends Service implements LocationListener {
    private static final String TAG = "LocationUpdatesService";
    private static final String TAG_API = "api_response";
    private BroadcastReceiver mReceiver = null;
    Location mLoc;
    private static final String CHANNEL_ID = "channel_01";
    private final IBinder mBinder = new LocalBinder();
    private NotificationManager mNotificationManager;
    private static Location mLocation;
    private final long UPDATE_INTERVAL = 1000 * 60;
    private final long FASTEST_INTERVAL = 1000 * 30;
    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequestHighAccuracy;
    int statusCode=0;

    public LocationUpdatesService() {
    }

    public static Location getCurrentLocation() {
        return mLocation;
    }

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
        Log.d(TAG, "onCreate");
        startServiceInForeground();

        Log.d(TAG, "onCreate 2");
    }

    private void startServiceInForeground() {
        startForeground(98989, getNotification());
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("haseeb", "onLocationChanged() called");
        String msg = location + "Changed Location";
        Log.d(TAG, msg);
        Toast.makeText(ServiceProviderHome.context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    public void requestLocationUpdates() {
        Log.d(TAG, "Requesting location updates");
        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
    }


    private void locationUpdate(Location loc) {

            if (loc != null) {
                    mLoc = loc;
                    mLocation = loc;
            }

    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        mReceiver = new BootCompleteReceiver();
        registerReceiver(mReceiver, filter);
        Timer mTimer = new Timer();

        if (mLocationRequestHighAccuracy == null) {
            initializeLocationObject();
        }
        getLocation();

        return START_STICKY;

    }

    private void initializeLocationObject() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequestHighAccuracy = LocationRequest.create();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        Log.d(TAG, "initializeLocationObject  ");
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);
//        mLocationRequestHighAccuracy.setSmallestDisplacement(15);
//        mLocationRequestHighAccuracy.setMaxWaitTime(100);

    }

    public void startLocationRequest() {
        initializeLocationObject();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        Log.d(TAG, "getLocation  ----");
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
         Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, locationCallback, Looper.myLooper());
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Log.d("haseeb", "onLocationResult() called");
            Location location = locationResult.getLocations().get(0);
            Log.d(TAG, "getLocation  lat: " + location.getLatitude());
            Log.d(TAG, "getLocation  long: " + location.getLongitude());
            sendLocationToServer(location.getLongitude(), location.getLatitude());
            locationUpdate(location);
        }
    };

    private void sendLocationToServer(double longitude, double latitude){
        Log.d("haseeb", "sendLocationToServer");
        String APP_STATE="";
        if(foregrounded()){ APP_STATE = "FOREGROUND"; }
        else { APP_STATE = "BACKGROUND"; }

        String locationProvider = "GPS";

        Date currentTime = Calendar.getInstance().getTime();
        String time=""+ new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a").format(currentTime);
        String urlLocation = Constants.URL_Update_Location+"?appstate="+APP_STATE+"&lati="+latitude+"&locationprovider="+locationProvider+"&longi="+longitude+"&time="+time;
        Log.d(TAG_API, "URL: "+urlLocation);

        JSONObject parameters = new JSONObject();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, urlLocation, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG_API, "onResponse: "+response);
                        } catch (Exception e) {
                            Log.d(TAG_API, "Exception: "+e.toString());
                            Toast.makeText(getApplicationContext(), "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int code = error.networkResponse.statusCode;
                        Log.d(TAG_API, "onErrorResponse: "+error.toString());
                        Log.d(TAG_API, "onErrorResponse code: "+code);
                        if(code==420 || code==401 || code==403 || code==404){
                            CommonMethods.logoutUser(ServiceProviderHome.activity,getApplicationContext());
                        }
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("sessiontoken", SplashActivity.session.getSessionToken());
                params.put("Content-Type", "application/json");
                params.put("Accept", "*/*");

                return params;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response != null) {
                    statusCode = response.statusCode;
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "in onBind()");
        return mBinder;
    }


    @Override
    public void onDestroy() {
        Log.d("haseeb", "onDestroy called");
        try {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private Notification getNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 1006, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Listening Location...")
                .setOngoing(true)
                .setSilent(true)
                .setAutoCancel(false)
                .setContentIntent(contentIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher_round);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }

    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    public boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }
}
