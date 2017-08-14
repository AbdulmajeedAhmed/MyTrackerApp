package com.alyafei.mytracker.Others;

import android.content.Context;
import android.content.Intent;

import com.alyafei.mytracker.Login;
import com.alyafei.mytracker.MainActivity;
import com.alyafei.mytracker.MyTrackers;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by DELL on 3/30/2017.
 */

public class GlobalInfo {
    public static String PhoneNumber="";
    public static Map<String,String> myTrackers= new HashMap<>();
    public static Context context;
    public static Realm realm;


    public GlobalInfo(Context context) {
        this.context=context;
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .build();
        Realm.setDefaultConfiguration(config);

        realm = Realm.getDefaultInstance();
    }

    public static void UpdatesInfo(String UserPhone){
        SimpleDateFormat df= new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
        Date date= new Date();
        DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("Users").child(UserPhone).child("Updates").setValue(df.format(date).toString());
        //Realm
    }
    //format phone number
    public static String FormatPhoneNumber(String Oldnmber){
        try{
            String numberOnly= Oldnmber.replaceAll("[^0-9]", "");
            if(Oldnmber.charAt(0)=='+') numberOnly="+" +numberOnly ;
            if (numberOnly.length()>=10)
                numberOnly=numberOnly.substring(numberOnly.length()-10,numberOnly.length());
            return(numberOnly);
        }
        catch (Exception ex){
            return(" ");
        }
    }
    public static  void  saveUserToRealm(String username, String phoneNumber){
        User user = new User(username,FormatPhoneNumber(phoneNumber));
        try {
            realm.beginTransaction();
            int nextID;
            try {
                nextID = realm.where(User.class).max("id").intValue() + 1;  // increment id of course, because Realm does not support auto increment yet.
            } catch (Exception e) {
                nextID = 0;
            }
            user.id=nextID; // need for realm
            realm.copyToRealm(user);
            realm.commitTransaction();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public  void loadData() {
        List<User> users = realm.where(User.class).findAll();
        if (users.size() != 0) {
            myTrackers.clear();
            for (int i = 0; i < users.size(); i++) {
                myTrackers.put(users.get(i).PhoneNumber, users.get(i).Username);
            }

     /*   if(users.size()!=0 && !Login.loginFirstTime){
            myTrackers.clear();
            for (int i = 0; i < users.size(); i++) {
                myTrackers.put(users.get(i).PhoneNumber, users.get(i).Username);
            }
            Intent intent =new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
      *//*  else if(!Login.loginFirstTime){ //users is empty and it is the first time to log in
            Intent intent =new Intent(context, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }*//*else if(Login.loginFirstTime){ // the user logged in before...
            Intent intent =new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }*/

      /*  if(!Login.loginFirstTime){
            Intent intent =new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }*/

        }
    }

    public static void deleteAllUsersFromFirebase(){
        DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("Users").removeValue();
        deleteALLUsersFromRealm();
    }



    public  static void deleteUserFromRealm(String phoneNumber) {
        try {
            realm.beginTransaction();
            realm.where(User.class)
                    .equalTo("PhoneNumber",phoneNumber).findFirst().deleteFromRealm();
            realm.commitTransaction();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public static  void deleteALLUsersFromRealm() {
        try {
            realm.beginTransaction();
            realm.where(User.class).findAll().deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public static void deleteTrackerToFirebase(String PhoneNumber ) {
        DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("Users").child(GlobalInfo.PhoneNumber).child("Trackers")
                .child(PhoneNumber).removeValue();
    }
}
