package carcity.app.customer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import carcity.app.R;
import carcity.app.admin.activity.AdminHome;
import carcity.app.admin.activity.AllServiceProvidersActivity;
import carcity.app.admin.activity.SettingsActivityAdmin;
import carcity.app.common.activity.LoginActivity;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.customer.fragments.FragmentCreateJobCustomer;
import carcity.app.customer.fragments.FragmentJobDetailsCustomer;
import carcity.app.customer.fragments.FragmentJobDoneActivityCustomer;
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
    TextView textViewDrawerCancelAllJobsCustomer;
    TextView textViewDrawerLogout;
    private static final String TAG = "customer_home";
    KProgressHUD progressDialog = null;

    static CustomerHome instance;
    int statusCode=0;


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
        getJobDetails();
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
        textViewDrawerCancelAllJobsCustomer = findViewById(R.id.textViewDrawerCancelAllJobsCustomer);

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
                startActivity(new Intent(CustomerHome.this, CustomerHome.class));
            }
            if(view == textViewDrawerSettings){
                startActivity(new Intent(activity, SettingsActivityCustomer.class));
            }
            if(view == textViewDrawerCancelAllJobsCustomer){
                cancelAllJobs();
            }
            if(view == textViewDrawerLogout){
                CommonMethods.logoutUser(activity,context);
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

    private void cancelAllJobs(){
        progressDialog = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Cancelling All Jobs")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.URL_CANCEL_ALL_JOBS_CUSTOMER, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            String message = response.getString("message");
                            if(message.equals("Done")){
                                Toast.makeText(context, "All Jobs Cancelled", Toast.LENGTH_SHORT).show();
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
                        try {
                            Log.d(TAG, "onErrorResponse code "+code+": "+error.toString());
                            if(code==420 || code==401 || code==403 || code==404){
                                CommonMethods.logoutUser(ServiceProviderHome.activity,context);
                            } else if (code==412){
                                String data = new String(error.networkResponse.data, "UTF-8");
                                Log.d(TAG, "onErrorResponse data: "+data);
                                JSONObject jsonObject = new JSONObject(data);
                                String message = jsonObject.getString("message");
                                Log.d(TAG, "onErrorResponse message: "+message);
                                if(message.equals("Job not found (001).")){
                                    FragmentCreateJobCustomer fragmentCreateJobCustomer = new FragmentCreateJobCustomer(activity, context);
                                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_home_customer, fragmentCreateJobCustomer);
                                    transaction.commit();
//                                    Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                    Log.d(TAG, "parseNetworkResponse: "+response.toString());
                    Log.d(TAG, "statusCode: "+statusCode);
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(context).add(jsonRequest);
    }

    private void getJobDetails(){
        progressDialog = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Getting Job Details")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.URL_GET_JOB_DETAILS_CUSTOMER, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            Log.d(TAG, "onResponse: "+response.toString());
                            Bundle bundle = new Bundle();
                            bundle.putString("data",response.toString());

                            if(response.getString("state").equals("NEW_JOB_WANTS_SERVICE_NOW")){
                                FragmentJobDetailsCustomer fragmentJobDetailsCustomer = new FragmentJobDetailsCustomer(activity, context);
                                fragmentJobDetailsCustomer.setArguments(bundle);
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_home_customer, fragmentJobDetailsCustomer);
                                transaction.commit();
                            } else if(response.getString("state").equals("JOB_ASSIGNED_TO_SP")){
                                FragmentJobDoneActivityCustomer fragmentJobDoneActivityCustomer = new FragmentJobDoneActivityCustomer(activity, context);
                                fragmentJobDoneActivityCustomer.setArguments(bundle);
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_home_customer, fragmentJobDoneActivityCustomer);
                                transaction.commit();
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
                        try {
                            Log.d(TAG, "onErrorResponse code "+code+": "+error.toString());
                            if(code==420 || code==401 || code==403 || code==404){
                                CommonMethods.logoutUser(ServiceProviderHome.activity,context);
                            } else if (code==412){
                                String data = new String(error.networkResponse.data, "UTF-8");
                                Log.d(TAG, "onErrorResponse data: "+data);
                                JSONObject jsonObject = new JSONObject(data);
                                String message = jsonObject.getString("message");
                                Log.d(TAG, "onErrorResponse message: "+message);
                                if(message.equals("Job not found (001).")){
                                    FragmentCreateJobCustomer fragmentCreateJobCustomer = new FragmentCreateJobCustomer(activity, context);
                                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_home_customer, fragmentCreateJobCustomer);
                                    transaction.commit();
//                                    Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                    Log.d(TAG, "parseNetworkResponse: "+response.toString());
                    Log.d(TAG, "statusCode: "+statusCode);
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
}