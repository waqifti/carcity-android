package carcity.app.serviceProvider.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import carcity.app.R;
import carcity.app.admin.activity.AllServiceProvidersActivity;
import carcity.app.admin.activity.SettingsActivityAdmin;
import carcity.app.admin.modals.SettingsModal;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.customer.fragments.FragmentJobDoneActivityCustomer;
import carcity.app.serviceProvider.adapters.SettingsAdapterServiceProvider;
import carcity.app.serviceProvider.fragments.FragmentHomeSP;
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
    TextView textViewDrawerActivateJobSearch;
    TextView textViewDrawerDeactivateJobSearch;
    TextView textViewDrawerLogout;
    static TextView textViewLong;
    static TextView textViewLat;
    private static final String TAG = "SP_HOME_123";
    static ServiceProviderHome instance;
    KProgressHUD progressDialog = null;
    int statusCode=0;


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
        goHome();
    }

    private void setViews() {
        context = getApplicationContext();
        leftDrawerMenu = findViewById(R.id.leftDrawerMenu);
        drawerLayout = findViewById(R.id.drawerLayout);
        ivNavMenu = findViewById(R.id.ivNavMenu);
        textViewDrawerUserName = findViewById(R.id.textViewDrawerUserName);
        textViewDrawerHome = findViewById(R.id.textViewDrawerHome);
        textViewDrawerSettings = findViewById(R.id.textViewDrawerSettingsServiceProvider);
        textViewDrawerActivateJobSearch = findViewById(R.id.textViewDrawerActivateJobSearch);
        textViewDrawerDeactivateJobSearch = findViewById(R.id.textViewDrawerDeactivateJobSearch);
        textViewDrawerLogout = findViewById(R.id.textViewDrawerLogout);

        textViewDrawerUserName.setText(SplashActivity.session.getCellNumber());
    }

    private void setListeners() {
        ivNavMenu.setOnClickListener(onClickListener);
        textViewDrawerHome.setOnClickListener(onClickListener);
        textViewDrawerSettings.setOnClickListener(onClickListener);
        textViewDrawerActivateJobSearch.setOnClickListener(onClickListener);
        textViewDrawerDeactivateJobSearch.setOnClickListener(onClickListener);
        textViewDrawerLogout.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            if(view == ivNavMenu){
                toggleLeftDrawer();
            }
            if(view == textViewDrawerHome){
                goHome();
                toggleLeftDrawer();
            }
            if(view == textViewDrawerSettings){
                startActivity(new Intent(activity, SettingsActivityServiceProvider.class));
            }
            if(view == textViewDrawerActivateJobSearch){
                activateJobSearch();
            }
            if(view == textViewDrawerDeactivateJobSearch){
                deactivateJobSearch();
            }
            if(view == textViewDrawerLogout){
                CommonMethods.logoutUser(activity, context);
            }
        }
    };

    private void goHome() {
        FragmentHomeSP fragmentHomeSP = new FragmentHomeSP(activity, context);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_home_sp, fragmentHomeSP);
        transaction.commit();
    }

    public void toggleLeftDrawer() {
        if (drawerLayout.isDrawerOpen(leftDrawerMenu)) {
            drawerLayout.closeDrawer(leftDrawerMenu);
        } else {
            drawerLayout.openDrawer(leftDrawerMenu);
        }
    }

    private void activateJobSearch() {
        progressDialog = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Activating Job Search")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.URL_ACTIVATE_JOB_SEARCH, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            Log.d(TAG, "onResponse: "+response.toString());
                            if(response.getString("message").equals("Done")) {
                                Toast.makeText(context, "Activated", Toast.LENGTH_SHORT).show();
                                toggleLeftDrawer();
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "Exception: "+e.toString());
                            Toast.makeText(getApplicationContext(), "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int code = error.networkResponse.statusCode;
                        if(code==420 || code==401 || code==403 || code==404){
                            CommonMethods.logoutUser(ServiceProviderHome.activity, context);
                        }
                        Log.d(TAG, "onErrorResponse: "+error.toString());
                        Toast.makeText(getApplicationContext(), "User Not Found", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("sessiontoken", SplashActivity.session.getSessionToken());
                params.put("Content-Type", "application/json");
                params.put("Accept", "*/*");
                return params;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response != null) {
                    statusCode = response.statusCode;
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void deactivateJobSearch() {
        progressDialog = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Deactivating Job Search")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.URL_DEACTIVATE_JOB_SEARCH, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            Log.d(TAG, "onResponse: "+response.toString());
                            if(response.getString("message").equals("Done")) {
                                Toast.makeText(context, "Deactivated", Toast.LENGTH_SHORT).show();
                                toggleLeftDrawer();
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "Exception: "+e.toString());
                            Toast.makeText(getApplicationContext(), "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int code = error.networkResponse.statusCode;
                        if(code==420 || code==401 || code==403 || code==404){
                            CommonMethods.logoutUser(ServiceProviderHome.activity,context);
                        }
                        Log.d(TAG, "onErrorResponse: "+error.toString());
                        Toast.makeText(getApplicationContext(), "User Not Found", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("sessiontoken", SplashActivity.session.getSessionToken());
                params.put("Content-Type", "application/json");
                params.put("Accept", "*/*");
                return params;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response != null) {
                    statusCode = response.statusCode;
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}