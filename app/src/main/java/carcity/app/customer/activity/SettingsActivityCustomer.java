package carcity.app.customer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carcity.app.R;
import carcity.app.admin.adapters.SettingsAdapterAdmin;
import carcity.app.admin.modals.SettingsModal;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.customer.adapters.SettingsAdapterCustomer;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class SettingsActivityCustomer extends AppCompatActivity {

    private final String TAG = "my_tag";
    Context context;
    Activity activity;
    ImageView imageViewSettingsCustomerGoBack;
    RecyclerView recyclerViewSettingCustomer;
    KProgressHUD progressDialog = null;
    int statusCode=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_customer);
        activity = this;
        CommonMethods.hideSystemUI(activity);
        setViews();
        setListeners();
        getProfileInfo();
    }

    private void setViews() {
        context = getApplicationContext();
        imageViewSettingsCustomerGoBack = findViewById(R.id.imageViewSettingsCustomerGoBack);
        recyclerViewSettingCustomer = findViewById(R.id.recyclerViewSettingCustomer);
    }

    private void setListeners() {
        imageViewSettingsCustomerGoBack.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == imageViewSettingsCustomerGoBack){
                finish();
            }
        }
    };

    private void getProfileInfo(){
        progressDialog = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Getting Service Providers")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.POST, Constants.URL_PROFILE_INFO_CUSTOMER, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            progressDialog.dismiss();
                            ArrayList<SettingsModal> settingsModalList = new ArrayList<>();
                            String settingName;
                            ArrayList<String> selectableValues;
                            String selectedValue;
                            JSONArray selectableValuesJsonArray;

                            for(int i=0; i<response.length(); i++){
                                JSONObject jsonObject = response.getJSONObject(i);
                                settingName = jsonObject.getString("settingname");
                                selectedValue = jsonObject.getString("selectedvalue");
                                selectableValuesJsonArray = jsonObject.getJSONArray("selectablevalues");
                                selectableValues = new ArrayList<>();

                                selectableValues.add("Select from list");
                                for (int j = 0; j<selectableValuesJsonArray.length(); j++) {
                                    selectableValues.add(selectableValuesJsonArray.get(j).toString());
                                }
                                settingsModalList.add(new SettingsModal(settingName, selectableValues, selectedValue));
                            }
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                            recyclerViewSettingCustomer.setLayoutManager(mLayoutManager);
                            RecyclerView.Adapter adapter = new SettingsAdapterCustomer(context,activity,settingsModalList, response);
                            recyclerViewSettingCustomer.setAdapter(adapter);
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
}