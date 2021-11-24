package carcity.app.customer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import carcity.app.R;
import carcity.app.admin.activity.AllServiceProvidersActivity;
import carcity.app.admin.activity.SettingsActivityAdmin;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class CustomerHome extends AppCompatActivity {

    public static Activity activity;
    public static Context context;
    DrawerLayout drawerLayout;
    View leftDrawerMenu;
    ImageView ivNavMenu;
    TextView textViewDrawerUserName;
    TextView textViewDrawerHome;
    TextView textViewDrawerSettings;
    TextView textViewDrawerLogout;
    private static final String TAG = "myLocation";

    static CustomerHome instance;


    public static CustomerHome getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        activity = this;
        CommonMethods.hideSystemUI(activity);
        setViews();
        setListeners();
    }

    private void setViews() {
        context = getApplicationContext();
        leftDrawerMenu = findViewById(R.id.leftDrawerMenuCustomer);
        drawerLayout = findViewById(R.id.drawerLayoutCustomer);
        ivNavMenu = findViewById(R.id.ivNavMenuCustomer);
        textViewDrawerUserName = findViewById(R.id.textViewDrawerUserNameCustomer);
        textViewDrawerHome = findViewById(R.id.textViewDrawerHomeCustomer);
        textViewDrawerSettings = findViewById(R.id.textViewDrawerSettingsCustomer);
        textViewDrawerLogout = findViewById(R.id.textViewDrawerLogoutCustomer);

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
                startActivity(new Intent(activity, SettingsActivityCustomer.class));
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