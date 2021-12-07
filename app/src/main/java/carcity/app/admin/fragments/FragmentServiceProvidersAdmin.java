package carcity.app.admin.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import carcity.app.R;
import carcity.app.admin.activity.AllServiceProvidersActivity;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class FragmentServiceProvidersAdmin extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "All_Jobs_Admin";
    private Activity activity;
    private Context context;
    View view;

    ImageView imageViewRefresh;
    Spinner spinnerAllServiceProviders;
    TextView textViewDateStart, textViewDateEnd;
    KProgressHUD progressDialog = null;
    int statusCode=0;
    ArrayList<String> cellNumbers;
    ArrayAdapter arrayAdapter;

    public static GoogleMap map;
    public static MapView mapViewIncidents;
    private static final int REQUEST_CODE = 101;
    private static String API_KEY = "";
    private ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();

    String cellNumber="", timeStart = "", timeEnd = "";

    public FragmentServiceProvidersAdmin(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_service_providers, container, false);
        setViews();
        setListeners();
        getAllServiceProviders();
        locationPermission();
        return view;
    }

    private void setViews() {
        imageViewRefresh = view.findViewById(R.id.imageViewRefresh);
        spinnerAllServiceProviders = view.findViewById(R.id.spinnerAllServiceProviders);
        textViewDateStart = view.findViewById(R.id.textViewDateStart);
        textViewDateEnd = view.findViewById(R.id.textViewDateEnd);

        cellNumbers = new ArrayList<>();
        cellNumbers.add("Select Service Provider");
    }

    private void setListeners() {
        imageViewRefresh.setOnClickListener(onClickListener);
        textViewDateStart.setOnClickListener(onClickListener);
        textViewDateEnd.setOnClickListener(onClickListener);
        spinnerAllServiceProviders.setOnItemSelectedListener(onItemSelectedListener);
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            map();
        }
    }

    private void map() {
        MapsInitializer.initialize(context);
        mapViewIncidents = (MapView) view.findViewById(R.id.mapAllServiceProviders);
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
            if(view == textViewDateStart){
                selectDateTime(1);
            }
            if(view == textViewDateEnd){
                selectDateTime(2);
            }
            if(view == imageViewRefresh){
                getServiceProviderLocationData();
            }
        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTextSize(17);
            if(position == 0){
                cellNumber = "";
            } else {
                cellNumber = cellNumbers.get(position);
                getServiceProviderLocationData();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void getAllServiceProviders(){
        progressDialog = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Getting Service Providers")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, Constants.URL_ALL_SERVICE_PROVIDERS, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String cell;
                        try {
                            progressDialog.dismiss();
                            for(int i=0; i<response.length(); i++){
                                JSONObject jsonObject = response.getJSONObject(i);
                                cell = jsonObject.getString("cell");
                                cellNumbers.add(cell);
                                Log.d(TAG, "onResponse: cell, "+cell);
                            }
                            arrayAdapter = new ArrayAdapter(activity, R.layout.spinner_item, cellNumbers);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerAllServiceProviders.setAdapter(arrayAdapter);
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
                        if(code==420 || code==401 || code==403 || code==404){
                            CommonMethods.logoutUser(ServiceProviderHome.activity,context);
                        }
                        Log.d(TAG, "onErrorResponse: "+error.toString());
                        Toast.makeText(context, "User Not Found", Toast.LENGTH_SHORT).show();
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
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
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

        Volley.newRequestQueue(context).add(jsonRequest);
    }

    private void selectDateTime(int pos) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, month);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
                        if(pos == 1){
                            timeStart = simpleDateFormat.format(calendar.getTime());
                            textViewDateStart.setText(timeStart);
                            getServiceProviderLocationData();
                        } else if(pos == 2){
                            timeEnd = simpleDateFormat.format(calendar.getTime());
                            textViewDateEnd.setText(timeEnd);
                            getServiceProviderLocationData();
                        }

                    }
                };

                new TimePickerDialog(activity, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(activity, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void getServiceProviderLocationData(){
        if(cellNumber.equals("")){
            Toast.makeText(context, "Select Service Provider", Toast.LENGTH_SHORT).show();
        } else if(timeStart.equals("")){
            Toast.makeText(context, "Select Start Date", Toast.LENGTH_SHORT).show();
        } else if(timeEnd.equals("")){
            Toast.makeText(context, "Select End Date", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = KProgressHUD.create(activity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Fetching Locations")
                    .setCancellable(true)
                    .setAnimationSpeed(1)
                    .setDimAmount(0.5f)
                    .show();

            String url = Constants.URL_SERVICE_PROVIDERS_RECORDED_LOCATIONS+"?cell="+cellNumber+"&endtime="+timeEnd+"&starttime="+timeStart;
            JsonArrayRequest jsonRequest = new JsonArrayRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                progressDialog.dismiss();
                                double longi = 0.0;
                                double lati = 0.0;
                                String recordedat = "";
                                String dbentryat = "";
                                LatLng latLng;
                                String title = "";
                                Log.d(TAG, "onResponse: "+response.toString());
                                map.clear();
                                mMarkerArray.clear();
                                if(response.length()==0){
                                    Toast.makeText(context, "No Record Found", Toast.LENGTH_LONG).show();
                                } else {
                                    for(int i=0; i<response.length(); i++){
                                        JSONObject jsonObject = response.getJSONObject(i);
                                        longi = Double.parseDouble(jsonObject.getString("longi"));
                                        lati = Double.parseDouble(jsonObject.getString("lati"));
                                        recordedat = jsonObject.getString("recordedat");
                                        dbentryat = jsonObject.getString("dbentryat");
                                        latLng = new LatLng(lati, longi);
                                        title = "Recorder at: "+recordedat;

                                        Marker marker = map.addMarker(new MarkerOptions().position(latLng)
                                                .title(title));
                                        mMarkerArray.add(marker);
                                        moveCamera();
                                    }
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
                            if(code==420 || code==401 || code==403 || code==404){
                                CommonMethods.logoutUser(ServiceProviderHome.activity,context);
                            }
                            Log.d(TAG, "onErrorResponse: "+error.toString());
                            Toast.makeText(context, "User Not Found", Toast.LENGTH_SHORT).show();
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
                protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
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

            Volley.newRequestQueue(context).add(jsonRequest);
        }
    }

    private void moveCamera() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMarkerArray.get(mMarkerArray.size()-1).getPosition().latitude, mMarkerArray.get(mMarkerArray.size()-1).getPosition().longitude), 14.5f));

//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //the include method will calculate the min and max bound.
//        for(int i=0; i<mMarkerArray.size(); i++) {
//            builder.include(mMarkerArray.get(i).getPosition());
//            builder.include(mMarkerArray.get(i).getPosition());
//            builder.include(mMarkerArray.get(i).getPosition());
//            builder.include(mMarkerArray.get(i).getPosition());
//        }
//        builder.include(mMarkerArray.get(mMarkerArray.size()-1).getPosition());
//        LatLngBounds bounds = builder.build();
//
//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = getResources().getDisplayMetrics().heightPixels;
//        int padding = (int) (width * 0.01); // offset from edges of the map 1% of screen
//
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
//
//        map.animateCamera(cu);
    }

}
