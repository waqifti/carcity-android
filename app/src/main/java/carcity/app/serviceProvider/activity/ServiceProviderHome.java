package carcity.app.serviceProvider.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.badge.BadgeUtils;

import carcity.app.R;
import carcity.app.common.utils.CommonMethods;

public class ServiceProviderHome extends AppCompatActivity {

    Activity activity;
    Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);
        activity = this;
        CommonMethods.hideSystemUI(activity);

        buttonLogout = findViewById(R.id.buttonLogoutServiceProvider);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMethods.logoutUser(activity);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}