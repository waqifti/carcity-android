package carcity.app.serviceProvider.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import carcity.app.R;
import carcity.app.admin.adapters.SettingsAdapterAdmin;
import carcity.app.admin.modals.SettingsModal;

public class SettingsAdapterServiceProvider extends RecyclerView.Adapter<SettingsAdapterServiceProvider.Holder> {

    private Context context;
    private Activity activity;
    private List<SettingsModal> settingsList;
    private JSONArray jsonArray;

    public SettingsAdapterServiceProvider(Context context, Activity activity, ArrayList<SettingsModal> settingsList, JSONArray jsonArray){
        this.context = context;
        this.activity = activity;
        this.settingsList = settingsList;
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public SettingsAdapterServiceProvider.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_settings,parent,false);
        return new SettingsAdapterServiceProvider.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapterServiceProvider.Holder holder, int position) {
        final SettingsModal settings = settingsList.get(position);

        holder.textViewSettingModalName.setText(settings.getSettingName());
        holder.textViewSettingModalName.setTextSize(15);
        if(settings.getSelectableValues().size() == 0){
            holder.editTextSettingsModal.setVisibility(View.VISIBLE);
            holder.editTextSettingsModal.setTextSize(15);
            holder.spinnerSettingsModal.setVisibility(View.GONE);
        } else {
            holder.editTextSettingsModal.setVisibility(View.GONE);
            holder.spinnerSettingsModal.setVisibility(View.VISIBLE);
            ArrayAdapter arrayAdapter = new ArrayAdapter(activity, R.layout.spinner_item, settings.getSelectableValues());
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinnerSettingsModal.setAdapter(arrayAdapter);
            holder.spinnerSettingsModal.setOnItemSelectedListener(onItemSelectedListener);
        }
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTextSize(15);
            if(position == 0){
            } else {
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public class Holder extends RecyclerView.ViewHolder {
        TextView textViewSettingModalName;
        EditText editTextSettingsModal;
        Spinner spinnerSettingsModal;

        public Holder(@NonNull View view){
            super(view);
            textViewSettingModalName = view.findViewById(R.id.textViewSettingsModalName);
            editTextSettingsModal = view.findViewById(R.id.editTextSettingsModal);
            spinnerSettingsModal = view.findViewById(R.id.spinnerSettingsModal);
        }
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

}
