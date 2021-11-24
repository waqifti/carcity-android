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
import carcity.app.admin.activity.AllServiceProvidersActivity;
import carcity.app.admin.activity.SettingsActivityAdmin;
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
    TextView textViewDrawerSettings;
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
        textViewDrawerSettings = findViewById(R.id.textViewDrawerSettingsServiceProvider);
        textViewDrawerLogout = findViewById(R.id.textViewDrawerLogout);
        textViewLong = findViewById(R.id.textViewLong);
        textViewLat = findViewById(R.id.textViewLat);

        textViewDrawerUserName.setText(SplashActivity.session.getCellNumber());
    }

    private void setListeners() {
        ivNavMenu.setOnClickListener(onClickListener);
        textViewDrawerHome.setOnClickListener(onClickListener);
        textViewDrawerSettings.setOnClickListener(onClickListener);
        textViewDrawerLogout.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            if(view == ivNavMenu){
                toggleLeftDrawer();
            }
            if(view == textViewDrawerHome){
                //
            }
            if(view == textViewDrawerSettings){
                startActivity(new Intent(activity, SettingsActivityServiceProvider.class));
            }
            if(view == textViewDrawerLogout){
                CommonMethods.logoutUser(activity);
            }
        }
    };

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