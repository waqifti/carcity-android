package carcity.app.serviceProvider.fragments;

import static android.view.View.GONE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.kaopiz.kprogresshud.KProgressHUD;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import carcity.app.R;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class FragmentHomeSP extends Fragment implements OnMapReadyCallback {

    private final String TAG = "fragmentHomeSP";
    View view;
    Activity activity;
    Context context;
    int statusCode=0;
    KProgressHUD progressDialog = null;

    TextView textViewCustomerDetailsSPHome, textViewCounterSPHome;

    public static GoogleMap map;
    MapView mapCustomerDetailsSPHome;
    private static final int REQUEST_CODE = 101;
    private static String API_KEY = "";
    public static Handler handler;
    public static Runnable runnable;
    int counter = 5;

    public static double currentLongitude=0.0, currentLatitude=0.0;
    Location myLocation, customerLocation, prevLocation;
    public static LocationListener locationListener;
    public static LocationManager locationManager;
    LatLng latLng;
    public static MarkerOptions markerOptions;
    Marker marker;

    public FragmentHomeSP(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sp_home, container, false);
        textViewCustomerDetailsSPHome =  view.findViewById(R.id.textViewCustomerDetailsSPHome);
        textViewCounterSPHome =  view.findViewById(R.id.textViewCounterSPHome);
        mapCustomerDetailsSPHome =  view.findViewById(R.id.mapCustomerDetailsSPHome);
        myLocation = new Location("");
        customerLocation = new Location("");
        locationPermission();
        startLocationListener();
        getJobDetails();
        handler = new Handler();
        runTask();
        return view;
    }

    public void runTask(){
        runnable = new Runnable() {
            @Override
            public void run() {
                if(counter == 0){
                    counter = 5;
                    getJobDetails();
                } else {
                    textViewCounterSPHome.setText(""+counter);
                    counter--;
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @SuppressLint("MissingPermission")
    public void startLocationListener(){
        locationListener = new LocationListener(){
            @Override
            public void onLocationChanged(Location location){
                Log.d("haseeb1", "onLocationChanged() called");
                if(marker!= null){
                    marker.remove();
                }

                Log.d(TAG+"ggg", "onLocationChanged: "+location.toString());
                myLocation = new Location("");
                myLocation.setLatitude(location.getLatitude());
                myLocation.setLongitude(location.getLongitude());

                latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Me")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                marker = map.addMarker(markerOptions);
                marker.showInfoWindow();
                if(prevLocation == null || (prevLocation.distanceTo(myLocation))/1000 > 1){
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f));
                    prevLocation = myLocation;
                    drawPath();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle){

            }

            @Override
            public void onProviderEnabled(String s){

            }

            @Override
            public void onProviderDisabled(String s){
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
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
        mapCustomerDetailsSPHome = (MapView) view.findViewById(R.id.mapCustomerDetailsSPHome);
        if(mapCustomerDetailsSPHome != null){
            mapCustomerDetailsSPHome.onCreate(null);
            mapCustomerDetailsSPHome.onResume();
            mapCustomerDetailsSPHome.getMapAsync(this);
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

    private void getJobDetails(){
        Log.d("haseeb", "getJobDetails() called");
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
                            Log.d(TAG+"response", "onResponse: "+response.toString());
                            if(response.getString("state").equals("JOB_ASSIGNED_TO_SP")){
                                handler.removeCallbacks(runnable);
                                textViewCustomerDetailsSPHome.setText(response.getString("createdby"));
                                customerLocation.setLatitude(Double.parseDouble(response.getString("lati")));
                                customerLocation.setLongitude(Double.parseDouble(response.getString("longi")));
                                startLocationListener();
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
                                handler.removeCallbacks(runnable);
                                CommonMethods.logoutUser(ServiceProviderHome.activity,context);
                            } else if (code==420){
                                String data = new String(error.networkResponse.data, "UTF-8");
                                Log.d(TAG, "onErrorResponse data: "+data);
                                JSONObject jsonObject = new JSONObject(data);
                                String message = jsonObject.getString("message");
                                Log.d(TAG, "onErrorResponse message: "+message);
                                if(message.equals("Job not found.")){
                                    textViewCustomerDetailsSPHome.setText("No Job Assigned");
                                    textViewCounterSPHome.setVisibility(GONE);
                                    mapCustomerDetailsSPHome.setVisibility(GONE);
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

    public void drawPath(){
        Log.d(TAG+"yes", "drawPath() called");
        map.clear();
        setDestinationMarker();

        String origin = myLocation.getLatitude()+","+myLocation.getLongitude();
        String destination = customerLocation.getLatitude()+","+customerLocation.getLongitude();
        List<LatLng> path = new ArrayList();
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(getResources().getString(R.string.google_map_api))
                .build();
        DirectionsApiRequest req = DirectionsApi.getDirections(geoApiContext, origin, destination);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e("TAG", ex.getLocalizedMessage());
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            map.addPolyline(opts);
        }

        map.getUiSettings().setZoomControlsEnabled(true);
    }

    public void setDestinationMarker(){
        if(customerLocation != null) {
            LatLng latLng = new LatLng(customerLocation.getLatitude(), customerLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Customer");

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            Marker marker = map.addMarker(markerOptions);
            marker.showInfoWindow();
        }
    }
}
