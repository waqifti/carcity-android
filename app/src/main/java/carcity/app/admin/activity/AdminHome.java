package carcity.app.admin.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import carcity.app.R;
import carcity.app.admin.fragments.FragmentAllJobsAdmin;
import carcity.app.admin.fragments.FragmentHomeAdmin;
import carcity.app.admin.fragments.FragmentServiceProvidersAdmin;
import carcity.app.admin.fragments.FragmentSettingsAdmin;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class AdminHome extends AppCompatActivity {

    public static Activity activity;
    public static Context context;
    DrawerLayout drawerLayout;
    View leftDrawerMenu;
    ImageView ivNavMenu;
    TextView textViewDrawerUserName;
    TextView textViewDrawerHome;
    TextView textViewDrawerAllServiceProviders;
    TextView textViewDrawerAllJobsAdmin;
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
        textViewDrawerAllJobsAdmin = findViewById(R.id.textViewDrawerAllJobsAdmin);
        textViewDrawerSettings = findViewById(R.id.textViewDrawerSettingsAdmin);
        textViewDrawerLogout = findViewById(R.id.textViewDrawerLogoutAdmin);

        textViewDrawerUserName.setText(SplashActivity.session.getCellNumber());
    }

    private void setListeners() {
        ivNavMenu.setOnClickListener(onClickListener);
        textViewDrawerHome.setOnClickListener(onClickListener);
        textViewDrawerAllServiceProviders.setOnClickListener(onClickListener);
        textViewDrawerAllJobsAdmin.setOnClickListener(onClickListener);
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
                FragmentHomeAdmin fragmentHomeAdmin = new FragmentHomeAdmin(activity, context);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_home_admin, fragmentHomeAdmin);
                transaction.commit();
                toggleLeftDrawer();
            }
            if(view == textViewDrawerAllServiceProviders){
                FragmentServiceProvidersAdmin fragmentServiceProvidersAdmin = new FragmentServiceProvidersAdmin(activity, context);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_home_admin, fragmentServiceProvidersAdmin);
                transaction.commit();
                toggleLeftDrawer();
            }
            if(view == textViewDrawerAllJobsAdmin){
                FragmentAllJobsAdmin fragmentAllJobsAdmin = new FragmentAllJobsAdmin(activity, context);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_home_admin, fragmentAllJobsAdmin);
                transaction.commit();
                toggleLeftDrawer();
            }
            if(view == textViewDrawerSettings){
                FragmentSettingsAdmin fragmentSettingsAdmin = new FragmentSettingsAdmin(activity, context);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_home_admin, fragmentSettingsAdmin);
                transaction.commit();
                toggleLeftDrawer();
            }
            if(view == textViewDrawerLogout){
                CommonMethods.logoutUser(activity, context);
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