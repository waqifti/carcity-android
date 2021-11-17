package carcity.app.common.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import carcity.app.R;
import carcity.app.admin.activity.AdminHome;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.common.utils.Session;
import carcity.app.customer.activity.CustomerHome;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class SplashActivity extends AppCompatActivity {

    public static Session session;
    Activity activity;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activity = this;
        CommonMethods.hideSystemUI(activity);

        session = new Session(getApplicationContext());
        requestPermissions();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestPermissions() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    changeActivity();
                } else if(report.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(SplashActivity.this, "Permissions Denied, Go To Setting and allow the permissions", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check();
    }

    public void changeActivity(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void run() {
                if (session.getSession().equals("true")) {
                    if (session.getUserType().equals(Constants.UserTypeCustomer)) {
                        //User is logged in as Customer
                        Intent intent = new Intent(SplashActivity.this, CustomerHome.class);
                        startActivity(intent);
                    } else if (session.getUserType().equals(Constants.UserTypeServiceProvider)) {
                        //User is logged in as Service Provider
                        Intent intent = new Intent(SplashActivity.this, ServiceProviderHome.class);
                        startActivity(intent);
                    } else if (session.getUserType().equals(Constants.UserTypeAdmin)) {
                        //User is logged in as Admin
                        Intent intent = new Intent(SplashActivity.this, AdminHome.class);
                        startActivity(intent);
                    }
                } else if (!session.getSession().equals("true")) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }
        },3000);
    }
}