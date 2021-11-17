package carcity.app.customer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import carcity.app.R;
import carcity.app.common.utils.CommonMethods;

public class CustomerHome extends AppCompatActivity {

    Activity activity;
    Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        activity = this;
        CommonMethods.hideSystemUI(activity);

        buttonLogout = findViewById(R.id.buttonLogoutCustomerHome);
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