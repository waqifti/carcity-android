package carcity.app.admin.modals;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SettingsModal {
    private String settingName;
    private ArrayList<String> selectableValues;
    private String selectedValue;

    public SettingsModal(String settingName, ArrayList<String> selectableValues, String selectedValue){
        this.settingName = settingName;
        this.selectableValues = selectableValues;
        this.selectedValue = selectedValue;
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public ArrayList<String> getSelectableValues() {
        return selectableValues;
    }

    public void setSelectableValues(ArrayList<String> selectableValues) {
        this.selectableValues = selectableValues;
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }
}
