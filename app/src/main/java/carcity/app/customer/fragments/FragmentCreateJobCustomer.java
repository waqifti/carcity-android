package carcity.app.customer.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import carcity.app.R;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;

public class FragmentCreateJobCustomer extends Fragment {

    private final String TAG = "fragment1";
    Activity activity;
    Context context;
    Spinner spinnerJobTypeCustomerHome;
    Button buttonCreateJobCustomerHome;
    int statusCode=0;
    KProgressHUD progressDialog = null;
    ArrayList<String> arrayListJobTypes;
    String jobTypeSelected="";
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("MissingPermission")
    public FragmentCreateJobCustomer(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_home_create_job, container, false);
        spinnerJobTypeCustomerHome = view.findViewById(R.id.spinnerJobTypeCustomerHome);
        buttonCreateJobCustomerHome = view.findViewById(R.id.buttonCreateJobCustomerHome);

        spinnerJobTypeCustomerHome.setOnItemSelectedListener(onItemSelectedListener);
        buttonCreateJobCustomerHome.setOnClickListener(onClickListener);
        getJobTypes();
        return view;
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTextSize(17);
            if(position == 0){
                jobTypeSelected = "";
            } else {
                jobTypeSelected = arrayListJobTypes.get(position);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == buttonCreateJobCustomerHome){
                createJob();
            }
        }
    };

    private void getJobTypes(){
        progressDialog = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Getting Job Details")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.POST, Constants.URL_GET_JOB_TYPES, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            progressDialog.dismiss();
                            Log.d(TAG, "onResponse: "+response.toString());
                            arrayListJobTypes = new ArrayList<>();
                            arrayListJobTypes.add("Select Job Type");
                            for(int i=0; i<response.length(); i++){
                                arrayListJobTypes.add(response.get(i).toString());
                            }
                            ArrayAdapter arrayAdapter = new ArrayAdapter(activity, R.layout.spinner_item, arrayListJobTypes);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerJobTypeCustomerHome.setAdapter(arrayAdapter);
                        } catch (Exception e) {
                            Log.d(TAG, "Exception: "+e.toString());
                            Toast.makeText(context, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int code = error.networkResponse.statusCode;
                        try {
                            Log.d(TAG, "onErrorResponse code "+code+": "+error.toString());
                            String data = new String(error.networkResponse.data, "UTF-8");
                            Log.d(TAG, "onErrorResponse data: "+data);
                            if(code==420 || code==401 || code==403 || code==404){
                                CommonMethods.logoutUser(activity);
                            } else if (code==412){
                                JSONObject jsonObject = new JSONObject(data);
                                String message = jsonObject.getString("message");
                                Log.d(TAG, "onErrorResponse message: "+message);
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
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
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

    public void createJob(){
        if(jobTypeSelected.equals("")){
            Toast.makeText(context, "Select Job Type", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = KProgressHUD.create(activity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Getting Job Details")
                    .setCancellable(true)
                    .setAnimationSpeed(1)
                    .setDimAmount(0.5f)
                    .show();
            String url = Constants.URL_CREATE_JOB_CUSTOMER+"?lati="+latitude+"&longi="+longitude+"&jobtypes="+jobTypeSelected;
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                progressDialog.dismiss();
                                Log.d(TAG, "onResponse: "+response.toString());
                                String message = response.getString("message");
                                if(message.equals("Done")){
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "Exception: "+e.toString());
                                Toast.makeText(context, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            int code = error.networkResponse.statusCode;
                            try {
                                Log.d(TAG, "onErrorResponse code "+code+": "+error.toString());
                                String data = new String(error.networkResponse.data, "UTF-8");
                                Log.d(TAG, "onErrorResponse data: "+data);
                                if(code==420 || code==401 || code==403 || code==404){
                                    CommonMethods.logoutUser(activity);
                                } else if (code==412){
                                    JSONObject jsonObject = new JSONObject(data);
                                    String message = jsonObject.getString("message");
                                    Log.d(TAG, "onErrorResponse message: "+message);
                                    if (message.equals("You already have a job created or assigned to sp.")){
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
    }
}
