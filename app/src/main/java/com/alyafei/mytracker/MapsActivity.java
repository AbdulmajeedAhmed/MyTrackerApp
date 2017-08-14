package com.alyafei.mytracker;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.alyafei.mytracker.Others.GlobalInfo;
import com.alyafei.mytracker.Others.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();

        Bundle bundle= getIntent().getExtras();
        LoadLocation(bundle.getString("PhoneNumber"));

    }

    private void LoadLocation(String PhoneNumber) {
        mDatabaseReference.child("Users").child(PhoneNumber).child("Location")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (td==null)return;

                        double lat = Double.parseDouble(td.get("Latitude").toString());
                        double lag = Double.parseDouble(td.get("Longitude").toString());
                        /** Make sure that the map has been initialised **/
                        sydney = new LatLng(lat, lag); // user location..
                        LastDateOnline= td.get("LastTimeAndDate").toString();

                        loadMap(); // default function.
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    LatLng sydney ;
    String LastDateOnline;

    private void loadMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(sydney).title("last online:"+ LastDateOnline));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
    }
}
