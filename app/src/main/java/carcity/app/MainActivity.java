package carcity.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.Utils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    EditText editTextPhone, editTextPassword, editTextUserTpe;
    Button buttonLogin;
    public static final String TAG = "myTag";
    String fcmToken;
    KProgressHUD progressDialog = null;
    Activity activity;
    String urlLogin = "http://18.118.199.175:9000/Login?cell=3334543256&fcmtoken=asdaskdaskdkasndkasd&password=333444555&ut=Customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        setViews();
        setListeners();
        getDeviceToken();
    }

    public void setViews(){
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUserTpe = findViewById(R.id.editTextUserTpe);
        buttonLogin = findViewById(R.id.buttonLogin);
    }

    public void setListeners(){
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber=editTextPhone.getText().toString();
                String password=editTextPassword.getText().toString();
                String userType=editTextUserTpe.getText().toString();

//                try {
//                    progressDialog = KProgressHUD.create(activity)
//                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                            .setLabel("Verifying User")
//                            .setCancellable(false)
//                            .setAnimationSpeed(1)
//                            .setDimAmount(0.5f)
//                            .show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                loginUser(phoneNumber, password, userType);
            }
        });
    }

    public void loginUser2(String phoneNumber, String password, String userType){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONObject postData = new JSONObject();
                try {
                    postData.put("cell", phoneNumber);
                    postData.put("password", password);
                    postData.put("ut", userType);
                    postData.put("fcmtoken", fcmToken);
                    OkHttpHandler okHttpHandler = new OkHttpHandler();
                    okHttpHandler.execute(urlLogin, postData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 100);
    }

    private void loginUser(String phoneNumber, String password, String userType) {
//        Map<String, String> params = new HashMap<>();
//        params.put("cell", phoneNumber);
//        params.put("password", password);
//        params.put("ut", userType);
//        Log.d(TAG, "params body: "+ params.toString());

//        JSONObject parameters = new JSONObject(params);

        JSONObject parameters = new JSONObject();
        try {
//            parameters.put("cell", 92306224);
//            parameters.put("password", password);
//            parameters.put("ut", "userType");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "JSONException: "+ e.toString());
        }


        JsonObjectRequest  jsonRequest = new JsonObjectRequest
                (Request.Method.POST, urlLogin, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: "+response.toString());
                        } catch (Exception e) {
                            Log.d(TAG, "Exception: "+e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: "+error.toString());
                    }
                });

        Volley.newRequestQueue(this).add(jsonRequest);

//        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlLogin,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try{
//                            Log.d(TAG, "onResponse: "+response.toString());
//                        }catch (Exception e) {
//                            Log.d(TAG, "Exception: "+e.toString());
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d(TAG, "onErrorResponse: "+error.toString());
//                    }
//                })
//        {
//            @Override
//            protected Map<String, String> getParams()throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("cell", phoneNumber);
//                params.put("password", password);
//                params.put("ut", userType);
//                params.put("fcmtoken", fcmToken);
//                Log.d(TAG, "params body: "+ params.toString());
//                return params;
//            }
//        };

//        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
//        requestQueue.add(stringRequest);
    }

    public void getDeviceToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }

                // Get new FCM registration token
                fcmToken = task.getResult();
                Log.d(TAG, "FCM Token: "+ fcmToken);
            }
        });
    }

    public class OkHttpHandler extends AsyncTask<String, String, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            Log.d(TAG,"============Going to onPreExecute");
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG,"============Going to doInBackground");
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, params[1]);
            Log.d(TAG,"============body::::" + body.toString());
            okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
            Log.d(TAG,"============builder::::" + builder.toString());
            Log.d(TAG,"============params[0]::::" + params[0].toString());
            Log.d(TAG,"============params[1]::::" + params[1].toString());
            builder.url(params[0]);
            builder.post(body);
            okhttp3.Request request = builder.build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                Log.d(TAG,"============response::::" + response.toString());
                ResponseBody responsebody = response.body();
                Log.d(TAG,"============responsebody::::" + responsebody.toString());
                String str = responsebody.string();
                Log.d(TAG,"str*******" + str);
                return str;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (null != s && !s.isEmpty()) {
                try {
                    JSONObject mainObj = new JSONObject(s);
                    if (mainObj != null) {

                    } else {
                        Toast.makeText(activity, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "Error in parsing vehicle Liecence details");
                }
            } else {
                Toast.makeText(activity, "Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}