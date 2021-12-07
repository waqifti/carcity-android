package carcity.app.admin.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import carcity.app.R;

public class FragmentHomeAdmin extends Fragment {

    private static final String TAG = "All_Jobs_Admin";
    private Activity activity;
    private Context context;
    View view;


    public FragmentHomeAdmin(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        setViews();
        setListeners();
        return view;
    }

    private void setViews() {

    }

    private void setListeners() {

    }

}
