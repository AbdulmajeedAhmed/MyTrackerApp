package com.alyafei.mytracker.Others;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by DELL on 4/1/2017.
 */

public class TrackLocation implements LocationListener {

    public static boolean isRunning=false;
    public static Location location;

    public TrackLocation(){
        isRunning=true;
        location= new Location("not defined");
        location.setLatitude(0);
        location.setLongitude(0);
    }
    @Override
    public void onLocationChanged(Location location) {
        this.location=location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
