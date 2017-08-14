package com.alyafei.mytracker.Others;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DELL on 3/30/2017.
 */

public class User extends RealmObject {
    @PrimaryKey
    int id ;


    public  String PhoneNumber;

    public  String Username;

    public User()
    {

    }
    //for news details
    public User(String Username, String PhoneNumber)
    {
        this. Username=Username;
        this. PhoneNumber=PhoneNumber;
    }
}
