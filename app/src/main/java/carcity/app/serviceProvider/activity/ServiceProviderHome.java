package carcity.app.serviceProvider.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import carcity.app.R;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.serviceProvider.service.LocationUpdatesService;

public class ServiceProviderHome extends AppCompatActivity {

    public static Activity activity;
    public static Context context;
    DrawerLayout drawerLayout;
    View leftDrawerMenu;
    ImageView ivNavMenu;
    TextView textViewDrawerUserName;
    TextView textViewDrawerHome;
    TextView textViewDrawerLogout;
    static TextView textViewLong;
    static TextView textViewLat;
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
        CommonMethods.hideSystemUI(activity);
        setViews();
        setListeners();
        startService(new Intent(activity, LocationUpdatesService.class));
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

    public static void updateTextView(double longitude, double latitude){
        textViewLong.setText("Longitude: "+longitude);
        textViewLat.setText("Latitude: "+latitude);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}