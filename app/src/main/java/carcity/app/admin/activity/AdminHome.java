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
    TextView textViewAllServiceProviders;
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
        textViewAllServiceProviders = findViewById(R.id.textViewDrawerAllServiceProvidersAdmin);
        textViewDrawerLogout = findViewById(R.id.textViewDrawerLogoutAdmin);

        textViewDrawerUserName.setText(SplashActivity.session.getCellNumber());
    }

    private void setListeners() {
        textViewDrawerHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        textViewAllServiceProviders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, AllServiceProvidersActivity.class));
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
}