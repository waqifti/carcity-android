package carcity.app.customer.fragments;

import static android.view.View.GONE;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import carcity.app.R;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.common.utils.JobDetails;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class FragmentJobDetailsCustomer extends Fragment {

    private final String TAG = "fragment2";
    Activity activity;
    Context context;
    int statusCode=0;
    TextView textViewJobStatus;
    ProgressBar progressBarJobStatus;
    private Handler handler;
    private Runnable runnable;

    public FragmentJobDetailsCustomer(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_home_job_details, container, false);
        textViewJobStatus = view.findViewById(R.id.textViewJobDetailsStatus);
        progressBarJobStatus = view.findViewById(R.id.progressBarJobStatus);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                getJobDetails();
                handler.postDelayed(this, Constants.TIME_INTERVAL);
            }
        };
        handler.postDelayed(runnable, Constants.TIME_INTERVAL);

        return view;
    }

    private void getJobDetails(){
//        progressDialog = KProgressHUD.create(activity)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Getting Job Details")
//                .setCancellable(true)
//                .setAnimationSpeed(1)
//                .setDimAmount(0.5f)
//                .show();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.URL_GET_JOB_DETAILS_CUSTOMER, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //progressDialog.dismiss();
                            Log.d(TAG, "onResponse: "+response.toString());
                            Bundle bundle = new Bundle();
                            bundle.putString("data",response.toString());

                            if(response.getString("state").equals("NEW_JOB_WANTS_SERVICE_NOW")){
                                textViewJobStatus.setText("Job Created, Please Wait for job to be assigned");
                            } else if(response.getString("state").equals("JOB_ASSIGNED_TO_SP")){
                                handler.removeCallbacks(runnable);
                                FragmentJobDoneActivityCustomer fragmentJobDoneActivityCustomer = new FragmentJobDoneActivityCustomer(activity, context);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_home_customer, fragmentJobDoneActivityCustomer);
                                transaction.commit();
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "Exception: "+e.toString());
                            Toast.makeText(context, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            //progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int code = error.networkResponse.statusCode;
                        try {
                            Log.d(TAG, "onErrorResponse code "+code+": "+error.toString());
                            if(code==420 || code==401 || code==403 || code==404){
                                CommonMethods.logoutUser(ServiceProviderHome.activity);
                            } else if (code==412){
                                String data = new String(error.networkResponse.data, "UTF-8");
                                Log.d(TAG, "onErrorResponse data: "+data);
                                JSONObject jsonObject = new JSONObject(data);
                                String message = jsonObject.getString("message");
                                Log.d(TAG, "onErrorResponse message: "+message);
                                if(message.equals("Job not found (001).")){
                                    FragmentCreateJobCustomer fragmentCreateJobCustomer = new FragmentCreateJobCustomer(activity, context);
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_home_customer, fragmentCreateJobCustomer);
                                    transaction.commit();
//                                    Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                                }
                            }
                            //progressDialog.dismiss();
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