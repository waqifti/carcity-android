package carcity.app.serviceProvider.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import carcity.app.R;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.serviceProvider.service.MyLocationBroadcast;
import carcity.app.serviceProvider.service.MyLocationService;

public class ServiceProviderHome extends AppCompatActivity {

    Activity activity;
    public static Context context;
    DrawerLayout drawerLayout;
    View leftDrawerMenu;
    ImageView ivNavMenu;
    TextView textViewDrawerUserName, textViewDrawerHome, textViewDrawerLogout, textViewLong, textViewLat;
    private static final String TAG = "myLocation";

    static ServiceProviderHome instance;


    public static ServiceProviderHome getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);
        activity = this;
        context = getApplicationContext();
        CommonMethods.hideSystemUI(activity);
        setViews();
        setListeners();
//        updateLocation();
        startService(new Intent(this, MyLocationService.class));
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
    }

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

    private void askPermission() {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        updateLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                });
    }

    private void updateLocation() {
        buildLocationRequest();

//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationBroadcast.class);
        intent.setAction(MyLocationBroadcast.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void buildLocationRequest() {
//        locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(2000);
//        locationRequest.setFastestInterval(0);

    }

    public void updateTextView(double longitude, double latitude){
        ServiceProviderHome.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewLong.setText(""+longitude);
                textViewLat.setText(""+latitude);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}