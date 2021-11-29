package carcity.app.customer.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.json.JSONException;
import org.json.JSONObject;

import carcity.app.R;

public class FragmentJobDetailsCustomer extends Fragment  {

    private final String TAG = "fragment2";
    Activity activity;
    Context context;
    JSONObject jsonObject;

    public FragmentJobDetailsCustomer(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_home_job_details, container, false);
        try {
            jsonObject = new JSONObject(getArguments().getString("data"));
            Log.d(TAG, "data: "+jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }


}
