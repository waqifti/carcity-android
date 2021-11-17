package carcity.app.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    //Constructor
    public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    //Session
    public void setSession(String session) {
        prefs.edit().putString("session", session).commit();
    }
    public String getSession() {
        return prefs.getString("session","");
    }

    //SessionToken
    public void setSessionToken(String sessionToken) {
        prefs.edit().putString("sessiontoken", sessionToken).commit();
    }
    public String getSessionToken() {
        return prefs.getString("sessiontoken","");
    }

    //FullName
    public void setFullName(String fullName) {
        prefs.edit().putString("fullname", fullName).commit();
    }
    public String getFullName() {
        return prefs.getString("fullname","");
    }

    //Cell Number
    public void setCellNumber(String phone) {
        prefs.edit().putString("cell", phone).commit();
    }
    public String getCellNumber() {
        return prefs.getString("cell","");
    }

    //Password
    public void setPassword(String password) { prefs.edit().putString("password", password).commit();}
    public String getPassword() {
        return prefs.getString("password","");
    }

    //User Type
    public void setUserType(String userType) { prefs.edit().putString("ut", userType).commit();}
    public String getUserType() {
        return prefs.getString("ut","");
    }

}
