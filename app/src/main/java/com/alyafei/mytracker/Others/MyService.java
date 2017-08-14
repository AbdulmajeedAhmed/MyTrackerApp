package com.alyafei.mytracker.Others;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DELL on 4/1/2017.
 */

public class MyService extends IntentService {
    public static boolean isRunning=false;
    DatabaseReference mDatabaseReference;
   public MyService(){
       super("MyService");
       isRunning=true;
       mDatabaseReference= FirebaseDatabase.getInstance().getReference();
       Log.d("From"," MyService()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mDatabaseReference.child("Users").child(GlobalInfo.PhoneNumber).child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
               //set Latitude
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabaseReference.child("Users").child(GlobalInfo.PhoneNumber).child("Location")
                        .child("Latitude").setValue(TrackLocation.location.getLatitude());
                //set Longitude
                mDatabaseReference.child("Users").child(GlobalInfo.PhoneNumber).child("Location")
                        .child("Longitude").setValue(TrackLocation.location.getLongitude());

                //set real time and date..
                SimpleDateFormat df= new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
                Date date= new Date();
                mDatabaseReference.child("Users").child(GlobalInfo.PhoneNumber).child("Location")
                        .child("LastTimeAndDate").setValue(df.format(date).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
