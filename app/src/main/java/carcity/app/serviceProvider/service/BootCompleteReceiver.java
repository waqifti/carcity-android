package carcity.app.serviceProvider.service;

import static android.content.Context.BATTERY_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;


public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Started", Toast.LENGTH_LONG).show();
//        Intent service = new Intent(context, LocationUpdatesService.class);
//        context.startService(service);
        //Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
        if ("android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
            //Power Off
            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//            OylaApplicationSingleton.getInstance().errorReport = "Battery Level = " + batLevel + ", Driver Status = " + OylaCaptainApplication.getInstance().getStatus() + ", SHUTDOWN/RESTARTED";
//            OylaApplicationSingleton.getInstance().callErrorAPi();
//            if (OylaCaptainApplication.getInstance().getStatus() == Constants.ONLINE) {
//                OylaApplicationSingleton.getInstance().statusChange(Constants.OFFLINE);
//            }
        }

    }

}