package carcity.app.admin.activity;

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
import carcity.app.serviceProvider.activity.ServiceProviderHome;
import carcity.app.serviceProvider.service.LocationUpdatesService;

public class AdminHome extends AppCompatActivity {

    public static Activity activity;
    public static Context context;
    DrawerLayout drawerLayout;
    View leftDrawerMenu;
    ImageView ivNavMenu;
    TextView textViewDrawerUserName;
    TextView textViewDrawerHome;
    TextView textViewDrawerAllServiceProviders;
    TextView textViewDrawerSettings;
    TextView textViewDrawerLogout;
    private static final String TAG = "myLocation";

    static ServiceProviderHome instance;


    public static ServiceProviderHome getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        activity = this;
        CommonMethods.hideSystemUI(activity);
        setViews();
        setListeners();
    }

    private void setViews() {
        context = getApplicationContext();
        leftDrawerMenu = findViewById(R.id.leftDrawerMenuAdmin);
        drawerLayout = findViewById(R.id.drawerLayoutAdmin);
        ivNavMenu = findViewById(R.id.ivNavMenuAdmin);
        textViewDrawerUserName = findViewById(R.id.textViewDrawerUserNameAdmin);
        textViewDrawerHome = findViewById(R.id.textViewDrawerHomeAdmin);
        textViewDrawerAllServiceProviders = findViewById(R.id.textViewDrawerAllServiceProvidersAdmin);
        textViewDrawerSettings = findViewById(R.id.textViewDrawerSettingsAdmin);
        textViewDrawerLogout = findViewById(R.id.textViewDrawerLogoutAdmin);

        textViewDrawerUserName.setText(SplashActivity.session.getCellNumber());
    }

    private void setListeners() {
        ivNavMenu.setOnClickListener(onClickListener);
        textViewDrawerHome.setOnClickListener(onClickListener);
        textViewDrawerAllServiceProviders.setOnClickListener(onClickListener);
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
            if(view == textViewDrawerAllServiceProviders){
                startActivity(new Intent(activity, AllServiceProvidersActivity.class));
            }
            if(view == textViewDrawerSettings){
                startActivity(new Intent(activity, SettingsActivityAdmin.class));
            }
            if(view == textViewDrawerLogout){
                CommonMethods.logoutUser(activity);
            }
        }
    };

    public void toggleLeftDrawer(){
        if (drawerLayout.isDrawerOpen(leftDrawerMenu)){
            drawerLayout.closeDrawer(leftDrawerMenu);
        } else{
            drawerLayout.openDrawer(leftDrawerMenu);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}