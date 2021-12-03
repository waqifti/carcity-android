package carcity.app.customer.fragments;

import static android.view.View.GONE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import carcity.app.R;
import carcity.app.admin.activity.AllServiceProvidersActivity;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.common.utils.JobDetails;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class FragmentJobDoneActivityCustomer extends Fragment implements OnMapReadyCallback {

    private final String TAG = "fragment3";
    View view;
    Activity activity;
    Context context;
    JobDetails jobDetails;
    TextView textViewJobDoneActivity;
    ImageView imageViewRefreshJobDoneActivity;
    int statusCode=0;
    KProgressHUD progressDialog = null;

    public static GoogleMap map;
    public static MapView mapViewIncidents;
    private static final int REQUEST_CODE = 101;
    private static String API_KEY = "";
    Handler handler;
    private Runnable runnable;

    public FragmentJobDoneActivityCustomer(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_home_job_done_activity, container, false);
        textViewJobDoneActivity = view.findViewById(R.id.textViewJobDoneActivity);
        imageViewRefreshJobDoneActivity = view.findViewById(R.id.imageViewRefreshJobDoneActivity);
        imageViewRefreshJobDoneActivity.setOnClickListener(onClickListener);
        locationPermission();
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

    public void locationPermission(){
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            map();
        }
    }

    private void map() {
        MapsInitializer.initialize(context);
        mapViewIncidents = (MapView) view.findViewById(R.id.mapJobDoneActivity);
        if(mapViewIncidents != null){
            mapViewIncidents.onCreate(null);
            mapViewIncidents.onResume();
            mapViewIncidents.getMapAsync(this);
        }

        API_KEY = getResources().getString(R.string.google_map_api);
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        MapsInitializer.initialize(context);

        map = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE);
        }
        else{
//            map.setMyLocationEnabled(true);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == imageViewRefreshJobDoneActivity){
                getJobDetails();
            }
        }
    };

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

                            if(response.getString("state").equals("JOB_ASSIGNED_TO_SP")){
                                setJob(response);
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
                                    handler.removeCallbacks(runnable);
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

    private void setJob(JSONObject jsonObject) {
        try {
            jobDetails = new JobDetails();
            Log.d(TAG, "data: "+jsonObject.toString());
            jobDetails.setId(Integer.parseInt(jsonObject.getString("id")));
            jobDetails.setDbEntryAt(jsonObject.getString("dbentryat"));
            jobDetails.setLongitudeCustomer(Double.parseDouble(jsonObject.getString("longi")));
            jobDetails.setLatitudeCustomer(Double.parseDouble(jsonObject.getString("lati")));
            jobDetails.setState(jsonObject.getString("state"));
            jobDetails.setDescription(jsonObject.getString("description"));
            jobDetails.setNotes(jsonObject.getString("notes"));
            jobDetails.setCreatedBy(jsonObject.getString("createdby"));
            jobDetails.setAssignedTo(jsonObject.getString("assignedto"));
            jobDetails.setManagedBy(jsonObject.getString("managedby"));

            if(jobDetails.getState().equals("NEW_JOB_WANTS_SERVICE_NOW")){
            } else if(jobDetails.getState().equals("JOB_ASSIGNED_TO_SP")){
                JSONObject jsonObjectAssignedTo = jsonObject.getJSONObject("assignedtodetails");
                jobDetails.setAssignedToCell(jsonObjectAssignedTo.getString("cell"));
                jobDetails.setAssignedToCurrentLongitude(Double.parseDouble(jsonObjectAssignedTo.getString("currentlongi")));
                jobDetails.setAssignedToCurrentLatitude(Double.parseDouble(jsonObjectAssignedTo.getString("currentlati")));

                textViewJobDoneActivity.setText(jobDetails.getAssignedTo());

                LatLng latLng = new LatLng(jobDetails.getAssignedToCurrentLatitude(), jobDetails.getAssignedToCurrentLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                map.clear();
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                markerOptions.getPosition();
                map.addMarker(markerOptions);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                map.animateCamera(cameraUpdate);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
