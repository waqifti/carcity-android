package carcity.app.common.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import carcity.app.R;
import carcity.app.admin.activity.AdminHome;
import carcity.app.common.utils.CommonMethods;
import carcity.app.common.utils.Constants;
import carcity.app.customer.activity.CustomerHome;
import carcity.app.serviceProvider.activity.ServiceProviderHome;

public class LoginActivity extends AppCompatActivity {

    EditText editTextCellNumber, editTextPassword;
    Spinner spinnerUserType;
    Button buttonLogin;
    String cellNumber="", password="", userType="", fcmToken="";
    public static final String TAG = "myTag";
    KProgressHUD progressDialog = null;
    Activity activity;

    String[] userTypes = { "Select User Type", Constants.UserTypeCustomer, Constants.UserTypeServiceProvider, Constants.UserTypeAdmin};
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        setViews();
        setListeners();
    }

    public void setViews(){
        editTextCellNumber = findViewById(R.id.editTextLoginCellNumber);
        editTextPassword = findViewById(R.id.editTextLoginPassword);
        spinnerUserType = findViewById(R.id.spinnerLoginUserType);
        buttonLogin = findViewById(R.id.buttonLogin);

        arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, userTypes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(arrayAdapter);
    }

    public void setListeners(){
        //Spinner Listener
        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                if(position == 0){
                    userType = "";
                } else {
                    userType = userTypes[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Login Button Listener
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cellNumber = editTextCellNumber.getText().toString();
                password = editTextPassword.getText().toString();
                if(cellNumber.equals("")){
                    editTextCellNumber.setError("Enter Cell Number");
                    return;
                } else if(password.equals("")){
                    editTextPassword.setError("Enter Cell Number");
                    return;
                } else if(userType.equals("")){
                    Toast.makeText(LoginActivity.this, "Select User Type", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    try {
                        progressDialog = KProgressHUD.create(activity)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("Verifying User")
                                .setCancellable(true)
                                .setAnimationSpeed(1)
                                .setDimAmount(0.5f)
                                .show();
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    return;
                                }

                                // Get new FCM registration token
                                fcmToken = task.getResult();
                                loginUser(cellNumber, password, userType);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void loginUser(String cellNumber, String password, String userType) {
        Log.d("haseeb", "loginUser() called");
        Log.d(TAG, "fcmToken: "+fcmToken);
        String urlLogin = Constants.URL_LOGIN+"?cell="+cellNumber+"&fcmtoken="+fcmToken+"&password="+password+"&ut="+userType;
        JSONObject parameters = new JSONObject();
        JsonObjectRequest  jsonRequest = new JsonObjectRequest
                (Request.Method.POST, urlLogin, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: "+response.toString());
                        try {
                            SplashActivity.session.setSession("true");
                            SplashActivity.session.setCellNumber(cellNumber);
                            SplashActivity.session.setPassword(password);
                            SplashActivity.session.setUserType(userType);
                            SplashActivity.session.setSessionToken(response.getString("sessiontoken"));
                            progressDialog.dismiss();
                            if(userType.equals(Constants.UserTypeCustomer)){
                                //User is logged in as Customer
                                Intent intent = new Intent(LoginActivity.this, CustomerHome.class);
                                startActivity(intent);
                            } else if(userType.equals(Constants.UserTypeServiceProvider)){
                                //User is logged in as Service Provider
                                Intent intent = new Intent(LoginActivity.this, ServiceProviderHome.class);
                                startActivity(intent);
                            } else if(userType.equals(Constants.UserTypeAdmin)){
                                //User is logged in as Admin
                                Intent intent = new Intent(LoginActivity.this, AdminHome.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "Exception: "+e.toString());
                            Toast.makeText(getApplicationContext(), "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: "+error.toString());
                        Toast.makeText(getApplicationContext(), "User Not Found", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}