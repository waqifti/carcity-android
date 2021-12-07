package carcity.app.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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

import java.util.HashMap;
import java.util.Map;

import carcity.app.R;
import carcity.app.common.activity.LoginActivity;
import carcity.app.common.activity.SplashActivity;
import carcity.app.serviceProvider.activity.ServiceProviderHome;
import carcity.app.serviceProvider.service.LocationUpdatesService;

public class CommonMethods {

    public static void logoutUser(Activity activity, Context context){
        KProgressHUD progressDialog = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Logging Out")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.URL_LOGOUT, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            if(response.getString("message").equals("Done")){
                                SplashActivity.session.setSession("false");
                                SplashActivity.session.setCellNumber("");
                                SplashActivity.session.setPassword("");
                                SplashActivity.session.setUserType("");
                                SplashActivity.session.setSessionToken("");
                                Toast.makeText(activity, "Car City Session Logged Out", Toast.LENGTH_SHORT).show();
                                activity.stopService(new Intent(activity, LocationUpdatesService.class));
                                activity.startActivity(new Intent(activity, LoginActivity.class));
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(context).add(jsonRequest);
    }

    public static String getDeviceToken(){
        final String[] fcmToken = new String[1];
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                // Get new FCM registration token
                fcmToken[0] = task.getResult();
            }
        });
        return fcmToken[0];
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static void hideSystemUI(Activity activity) {
        Window window = activity.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = (int) dpToPx(-20);
        View view = new View(activity);
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.getLayoutParams().height = statusBarHeight;
        ((ViewGroup) window.getDecorView()).addView(view);
        view.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));

    }

    public static float dpToPx(float dp) {
        return (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
