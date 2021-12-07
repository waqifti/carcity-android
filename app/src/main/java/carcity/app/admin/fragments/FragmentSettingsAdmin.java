package carcity.app.admin.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Map;

import carcity.app.R;
import carcity.app.admin.adapters.SettingsAdapterAdmin;
import carcity.app.admin.modals.SettingsModal;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class FragmentSettingsAdmin extends Fragment {

    private static final String TAG = "All_Jobs_Admin";
    private Activity activity;
    private Context context;
    View view;
    RecyclerView recyclerViewSettingAdmin;
    Button buttonUpdateSettingsAdmin;
    KProgressHUD progressDialog = null;
    int statusCode=0;

    public FragmentSettingsAdmin(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_settings, container, false);
        setViews();
        setListeners();
        getProfileInfo();
        return view;
    }

    private void setViews() {
        recyclerViewSettingAdmin = view.findViewById(R.id.recyclerViewSettingAdmin);
        buttonUpdateSettingsAdmin = view.findViewById(R.id.buttonUpdateSettingsAdmin);
    }

    private void setListeners() {
        buttonUpdateSettingsAdmin.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == buttonUpdateSettingsAdmin){
//                updateSettings();
            }
        }
    };

    private void getProfileInfo(){
        progressDialog = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Getting Settings")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.POST, Constants.URL_PROFILE_INFO_ADMIN, null, new Response.Listener<JSONArray>() {
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
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
                            recyclerViewSettingAdmin.setLayoutManager(mLayoutManager);
                            RecyclerView.Adapter adapter = new SettingsAdapterAdmin(context,activity,settingsModalList, response);
                            recyclerViewSettingAdmin.setAdapter(adapter);
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
