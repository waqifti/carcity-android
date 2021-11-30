package carcity.app.customer.fragments;

import static android.view.View.GONE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import org.json.JSONException;
import org.json.JSONObject;

import carcity.app.R;
import carcity.app.admin.activity.AllServiceProvidersActivity;
import carcity.app.common.utils.JobDetails;

public class FragmentJobDoneActivityCustomer extends Fragment implements OnMapReadyCallback {

    private final String TAG = "fragment3";
    View view;
    Activity activity;
    Context context;
    JSONObject jsonObject;
    JobDetails jobDetails;
    TextView textViewJobDoneActivity;

    public static GoogleMap map;
    public static MapView mapViewIncidents;
    private static final int REQUEST_CODE = 101;
    private static String API_KEY = "";
    Marker mMarker;

    public FragmentJobDoneActivityCustomer(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_home_job_done_activity, container, false);
        textViewJobDoneActivity = view.findViewById(R.id.textViewJobDoneActivity);
        try {
            jobDetails = new JobDetails();
            jsonObject = new JSONObject(getArguments().getString("data"));
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
                locationPermission();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
//            map.setMyLocationEnabled(true);
        }
    }

}
