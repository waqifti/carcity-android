package carcity.app.serviceProvider.fragments;

import static android.view.View.GONE;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.MapView;
import com.kaopiz.kprogresshud.KProgressHUD;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import carcity.app.R;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class FragmentHomeSP extends Fragment {

    private final String TAG = "fragmentHomeSP";
    View view;
    Activity activity;
    Context context;
    int statusCode=0;
    KProgressHUD progressDialog = null;

    TextView textViewCustomerDetailsSPHome;
    ImageView imageViewRefreshCustomerDetailsSPHome;
    MapView mapCustomerDetailsSPHome;

    public FragmentHomeSP(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sp_home, container, false);
        setViews();
        setListeners();
        getJobDetails();
        return view;
    }

    private void setViews() {
        textViewCustomerDetailsSPHome =  view.findViewById(R.id.textViewCustomerDetailsSPHome);
        imageViewRefreshCustomerDetailsSPHome =  view.findViewById(R.id.imageViewRefreshCustomerDetailsSPHome);
        mapCustomerDetailsSPHome =  view.findViewById(R.id.mapCustomerDetailsSPHome);
    }

    private void setListeners() {

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
                (Request.Method.POST, Constants.URL_GET_JOB_DETAILS_SP, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //progressDialog.dismiss();
                            Log.d(TAG, "onResponse: "+response.toString());
                            if(response.getString("state").equals("JOB_ASSIGNED_TO_SP")){
                                //setJob(response);
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
                            if(code==401 || code==403 || code==404){
                                CommonMethods.logoutUser(ServiceProviderHome.activity,context);
                            } else if (code==420){
                                String data = new String(error.networkResponse.data, "UTF-8");
                                Log.d(TAG, "onErrorResponse data: "+data);
                                JSONObject jsonObject = new JSONObject(data);
                                String message = jsonObject.getString("message");
                                Log.d(TAG, "onErrorResponse message: "+message);
                                if(message.equals("Job not found.")){
                                    textViewCustomerDetailsSPHome.setText("No Job Assigned");
                                    imageViewRefreshCustomerDetailsSPHome.setVisibility(GONE);
                                    mapCustomerDetailsSPHome.setVisibility(GONE);
                                    Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                                } else if(message.equals("Wrong sessiontoken")){
                                    CommonMethods.logoutUser(ServiceProviderHome.activity,context);
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
