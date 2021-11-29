package carcity.app.customer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carcity.app.R;
import carcity.app.admin.activity.SettingsActivityAdmin;
import carcity.app.admin.adapters.SettingsAdapterAdmin;
import carcity.app.admin.modals.SettingsModal;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.serviceProvider.activity.ServiceProviderHome;
import carcity.app.serviceProvider.adapters.SettingsAdapterServiceProvider;

public class SettingsAdapterCustomer extends RecyclerView.Adapter<SettingsAdapterCustomer.Holder> {

    private final String TAG="AdapterCustomer";
    private Context context;
    private Activity activity;
    private List<SettingsModal> settingsList;
    private JSONArray jsonArray;
    private String selectedValue="", settingName="";
    public static SettingsModal settings;
    private KProgressHUD progressDialog = null;
    private int statusCode=0;

    public SettingsAdapterCustomer(Context context, Activity activity, ArrayList<SettingsModal> settingsList, JSONArray jsonArray){
        this.context = context;
        this.activity = activity;
        this.settingsList = settingsList;
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public SettingsAdapterCustomer.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_settings,parent,false);
        return new SettingsAdapterCustomer.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapterCustomer.Holder holder, int position) {
        this.settings = settingsList.get(position);
        Log.d(TAG, "size:     "+this.settings.getSelectableValues().size());

        holder.textViewSettingModalName.setText(settings.getSettingName());
        holder.textViewSettingModalName.setTextSize(15);
        if(settings.getSelectableValues().size() == 1){
            holder.editTextSettingsModal.setVisibility(View.VISIBLE);
            holder.editTextSettingsModal.setTextSize(15);
            holder.spinnerSettingsModal.setVisibility(View.GONE);
            if(this.settings.getSelectedValue() != null && !this.settings.getSelectedValue().equals("null")){
                Log.d(TAG,"selectedvalue: "+this.settings.getSelectedValue());
                holder.editTextSettingsModal.setText(this.settings.getSelectedValue());
            }
        } else if(settings.getSelectableValues().size() > 1){
            holder.editTextSettingsModal.setVisibility(View.GONE);
            holder.spinnerSettingsModal.setVisibility(View.VISIBLE);
            setSpinner(holder.spinnerSettingsModal, this.settings);
        }

        setButton(holder.buttonSettingsModal, holder.editTextSettingsModal, this.settings);

    }

    public void setButton(Button buttonSettingsModal, EditText editTextSettingsModal, SettingsModal settings){
        buttonSettingsModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingName = settings.getSettingName();
                if(editTextSettingsModal.getVisibility() == View.VISIBLE){
                    selectedValue = editTextSettingsModal.getText().toString();
                }
                updateSettings(settingName, selectedValue);
            }
        });
    }

    public void setSpinner(Spinner spinnerSettingsModal, SettingsModal settings){
        spinnerSettingsModal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(15);
                if(position == 0) {
                    selectedValue = "";
                } else {
                    Log.d(TAG, "position: "+position);
                    Log.d(TAG, "size:     "+settings.getSelectableValues().size());
                    selectedValue = settings.getSelectableValues().get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, R.layout.spinner_item, settings.getSelectableValues());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSettingsModal.setAdapter(arrayAdapter);
    }


    public class Holder extends RecyclerView.ViewHolder {
        TextView textViewSettingModalName;
        EditText editTextSettingsModal;
        Spinner spinnerSettingsModal;
        Button buttonSettingsModal;

        public Holder(@NonNull View view){
            super(view);
            textViewSettingModalName = view.findViewById(R.id.textViewSettingsModalName);
            editTextSettingsModal = view.findViewById(R.id.editTextSettingsModal);
            spinnerSettingsModal = view.findViewById(R.id.spinnerSettingsModal);
            buttonSettingsModal = view.findViewById(R.id.buttonSettingsModal);
        }
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    private void updateSettings(String settingName, String selectedValue){
        if(settingName.equals("")){
            Toast.makeText(activity, "Enter Setting Name", Toast.LENGTH_SHORT).show();
        } else if(selectedValue.equals("")){
            Toast.makeText(activity, "Enter or Select Setting", Toast.LENGTH_SHORT).show();
        } else {
            JSONObject settingObject = new JSONObject();
            JSONArray settingsArray = new JSONArray();
            JSONObject params = new JSONObject();
            try {
                settingObject.put("settingname", settingName);
                settingObject.put("selectedvalue", selectedValue);
                settingsArray.put(settingObject);
                params.put("settings", settingsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog = KProgressHUD.create(activity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Getting Settings")
                    .setCancellable(true)
                    .setAnimationSpeed(1)
                    .setDimAmount(0.5f)
                    .show();

            Log.d(TAG, "jsonObject: "+ params.toString());
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.POST, Constants.URL_PROFILE_UPDATE_CUSTOMER, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                progressDialog.dismiss();
                                Log.d(TAG, "onResponse: "+response.toString());
                                Toast.makeText(context, ""+response.getString("message"), Toast.LENGTH_SHORT).show();
                                activity.finish();
                                //activity.startActivity(new Intent(activity, SettingsActivityAdmin.class));
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
                                CommonMethods.logoutUser(ServiceProviderHome.activity);
                            }
                            Log.d(TAG, "onErrorResponse: "+error.toString());
                            Toast.makeText(context, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
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
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
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

}
