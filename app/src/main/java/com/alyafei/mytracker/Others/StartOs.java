package com.alyafei.mytracker.Others;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

/**
 * Created by DELL on 5/2/2017.
 */

public class StartOs extends BroadcastReceiver {

// this is a receiver to listen to the system when it is reboting...
    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {

            // load data
            GlobalInfo globalInfo = new GlobalInfo(context);
            globalInfo.loadData();

            // start services
            if(!TrackLocation.isRunning){
                TrackLocation trackLocation = new TrackLocation();
                LocationManager locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,trackLocation);
            }
            if(!MyService.isRunning){
                Intent intent1= new Intent(context,MyService.class);
                context.startService(intent1);
            }
        }
    }
}
