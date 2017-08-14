package com.alyafei.mytracker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.alyafei.mytracker.Others.GlobalInfo;
import com.alyafei.mytracker.Others.MyService;
import com.alyafei.mytracker.Others.TrackLocation;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Login extends AppCompatActivity {
    public static boolean loginFirstTime;
    EditText EDTNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_login);
        EDTNumber= (EditText)findViewById(R.id.EDTNumber);

       /* if(!loginFirstTime){
            startServices();
            GlobalInfo globalInfo= new GlobalInfo(this);
            globalInfo.loadData();
           *//* Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);*//*

        }else{ //first time
            CheckUserPermsions();
        }*/


    }


    public void BuNext(View view) {
        CheckUserPermsions();

       /* GlobalInfo.PhoneNumber=GlobalInfo.FormatPhoneNumber(EDTNumber.getText().toString());
        GlobalInfo.UpdatesInfo(GlobalInfo.PhoneNumber);
        loginFirstTime=true;
        finish();
        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);*/
    }

    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.READ_CONTACTS},

                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        login();

    }

    private void login() {
        GlobalInfo.PhoneNumber=GlobalInfo.FormatPhoneNumber(EDTNumber.getText().toString());
        GlobalInfo.UpdatesInfo(GlobalInfo.PhoneNumber);
        loginFirstTime=true;
        startServices();
        finish();
        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);

    }

    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;// you can put any thing ..



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED  &&grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)  {
                    login();
                } else {
                    // Permission Denied
                    Toast.makeText( this,"ERROR! Permession is denied" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void startServices(){
        if(!TrackLocation.isRunning){
            TrackLocation trackLocation = new TrackLocation();
            LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,trackLocation);
        }
        if(!MyService.isRunning){
            Intent intent= new Intent(this,MyService.class);
            startService(intent);
        }
    }






}
