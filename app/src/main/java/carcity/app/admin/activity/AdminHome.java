package carcity.app.admin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import carcity.app.R;

public class AdminHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}