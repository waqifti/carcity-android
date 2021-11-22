package carcity.app.admin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.MapView;
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
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class AllServiceProvidersActivity extends AppCompatActivity {

    private final String TAG = "my_tag";
    Context context;
    Activity activity;
    ImageView imageViewAllServiceProvidersGoBack;
    Spinner spinnerAllServiceProviders;
    Button buttonDateStart, buttonDateEnd;
    MapView mapView;
    KProgressHUD progressDialog = null;
    int statusCode=0;

    ArrayList<String> cellNumbers;
    ArrayAdapter arrayAdapter;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_service_providers);
        activity = this;
        CommonMethods.hideSystemUI(activity);
        setViews();
        setListeners();
        getAllServiceProviders();
    }

    private void setViews() {
        imageViewAllServiceProvidersGoBack = findViewById(R.id.imageViewAllServiceProvidersGoBack);
        spinnerAllServiceProviders = findViewById(R.id.spinnerAllServiceProviders);
        buttonDateStart = findViewById(R.id.buttonDateStart);
        buttonDateEnd    = findViewById(R.id.buttonDateEnd);

         cellNumbers = new ArrayList<>();
         cellNumbers.add("Select Service Provider");
    }

    private void setListeners() {
        imageViewAllServiceProvidersGoBack.setOnClickListener(onClickListener);
        buttonDateStart.setOnClickListener(onClickListener);
        buttonDateEnd.setOnClickListener(onClickListener);
        spinnerAllServiceProviders.setOnItemSelectedListener(onItemSelectedListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == imageViewAllServiceProvidersGoBack){
                finish();
            }
            if(view == buttonDateStart){
                selectDateStart();
            }
            if(view == buttonDateEnd){
                selectDateEnd();
            }
        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            if(position == 0){
                //userType = "";
            } else {
                //userType = userTypes[position];
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void getAllServiceProviders(){
        progressDialog = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Verifying User")
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
                            Toast.makeText(getApplicationContext(), "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int code = error.networkResponse.statusCode;
                        if(code==420 || code==401 || code==403 || code==404){
                            CommonMethods.logoutUser(ServiceProviderHome.activity);
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

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void selectDateStart() {

    }

    private void selectDateEnd() {

    }
}