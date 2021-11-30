package carcity.app.customer.fragments;

import static android.view.View.GONE;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.json.JSONException;
import org.json.JSONObject;

import carcity.app.R;
import carcity.app.common.utils.JobDetails;

public class FragmentJobDetailsCustomer extends Fragment {

    private final String TAG = "fragment2";
    Activity activity;
    Context context;
    JSONObject jsonObject;
    JobDetails jobDetails;

    TextView textViewJobStatus;
    ProgressBar progressBarJobStatus;

    public FragmentJobDetailsCustomer(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_home_job_details, container, false);
        textViewJobStatus = view.findViewById(R.id.textViewJobDetailsStatus);
        progressBarJobStatus = view.findViewById(R.id.progressBarJobStatus);
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
                textViewJobStatus.setText("Job Created, Please Wait for job to be assigned");
            } else if(jobDetails.getState().equals("JOB_ASSIGNED_TO_SP")){
                JSONObject jsonObjectAssignedTo = jsonObject.getJSONObject("assignedtodetails");
                jobDetails.setAssignedToCell(jsonObjectAssignedTo.getString("cell"));
                jobDetails.setAssignedToCurrentLongitude(Double.parseDouble(jsonObjectAssignedTo.getString("currentlongi")));
                jobDetails.setAssignedToCurrentLatitude(Double.parseDouble(jsonObjectAssignedTo.getString("currentlati")));

                textViewJobStatus.setText("Job Assigned to Service Provider");
                view.findViewById(R.id.relatveLayoutJobProgress).setVisibility(GONE);
                view.findViewById(R.id.relativeLayoutJobStatus).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}