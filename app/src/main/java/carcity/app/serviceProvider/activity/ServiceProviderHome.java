package carcity.app.serviceProvider.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeUtils;

import java.util.Locale;

import carcity.app.R;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.serviceProvider.service.BackgroundLocationUpdateService;
import carcity.app.serviceProvider.service.LocationConstants;

public class ServiceProviderHome extends AppCompatActivity implements LocationListener {

    Activity activity;
    public static Context context;
    DrawerLayout drawerLayout;
    View leftDrawerMenu;
    ImageView ivNavMenu;
    TextView textViewDrawerUserName, textViewDrawerHome, textViewDrawerLogout, textViewLong, textViewLat;
    private static final String TAG = "myLocation";
    LocationManager locationManager;
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);
        activity = this;
        context = getApplicationContext();
        CommonMethods.hideSystemUI(activity);
        setViews();
        setListeners();
    }

    private void setViews() {
        context = getApplicationContext();
        leftDrawerMenu = findViewById(R.id.leftDrawerMenu);
        drawerLayout = findViewById(R.id.drawerLayout);
        ivNavMenu = findViewById(R.id.ivNavMenu);
        textViewDrawerUserName = findViewById(R.id.textViewDrawerUserName);
        textViewDrawerHome = findViewById(R.id.textViewDrawerHome);
        textViewDrawerLogout = findViewById(R.id.textViewDrawerLogout);
        textViewLong = findViewById(R.id.textViewLong);
        textViewLat = findViewById(R.id.textViewLat);

        textViewDrawerUserName.setText(SplashActivity.session.getCellNumber());

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    ServiceProviderHome.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
        } else {
            startLocationService();
        }

//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ServiceProviderHome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationService();
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isLocationServiceRunning(){
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for(ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationServices.class.getName().equals(service.service.getClassName())){
                    if(service.foreground){
                        return  true;
                    }
                }
            }
        }
        return false;
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), BackgroundLocationUpdateService.class);
            intent.setAction(LocationConstants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location Service Started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), BackgroundLocationUpdateService.class);
            intent.setAction(LocationConstants.ACTION_STOP_LOCATION_SERVICE);
            stopService(intent);
            Toast.makeText(this, "Location Service Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void setListeners() {
        textViewDrawerHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        textViewDrawerLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMethods.logoutUser(activity);
            }
        });

        ivNavMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLeftDrawer();
            }
        });

    }

    public void toggleLeftDrawer() {
        if (drawerLayout.isDrawerOpen(leftDrawerMenu)) {
            drawerLayout.closeDrawer(leftDrawerMenu);
        } else {
            drawerLayout.openDrawer(leftDrawerMenu);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        counter++;
//        Log.d(TAG, "Counter: "+counter);
//        Log.d(TAG, "Long: "+location.getLongitude());
//        Log.d(TAG, "Lat: "+location.getLatitude());
        textViewLong.setText("Longitude: "+location.getLongitude());
        textViewLat.setText("Latitude: "+location.getLatitude());

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d(TAG, "onProviderEnabled provider: "+provider);
    }
}