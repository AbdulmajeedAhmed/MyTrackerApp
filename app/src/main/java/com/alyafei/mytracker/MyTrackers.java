package com.alyafei.mytracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alyafei.mytracker.Others.User;
import com.alyafei.mytracker.Others.GlobalInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class MyTrackers extends AppCompatActivity {
    ArrayList<User>  listnewsData ;
    MyCustomAdapter myAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trackers);
        listView=(ListView)findViewById(R.id.listView); // get the view
        listnewsData= new ArrayList<User>(); // create new list.
        myAdapter=new MyCustomAdapter(listnewsData,this);// create new adapter and pass to it our arrayList.
        listView.setAdapter(myAdapter); // set the adapter to your list view..
        refreshData();



    }



        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_contact_list, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.add:
                    CheckUserPermsions();
                    return true;

                case R.id.goback:
                   //new GlobalInfo(this).saveUserToRealm();
                   finish();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        PickContact();//

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;// you can put any thing ..



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PickContact(); //gps call
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

    void PickContact(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    // Declare
    static final int PICK_CONTACT=1;
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor cursor =  getContentResolver().query(contactData, null, null, null, null);
                    if (cursor.moveToFirst()) {


                        String id =cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String cNumber="No number";
                        if (hasPhone.equalsIgnoreCase("1")) { //"1" if there is at least one phone number
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = GlobalInfo.FormatPhoneNumber (phones.getString(phones.getColumnIndex("data1")));
                            System.out.println("number is:"+cNumber);
                        }
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        // know add it to my Map == the temporary database.
                        GlobalInfo.myTrackers.put(cNumber,name);
                        GlobalInfo.saveUserToRealm(name,cNumber); // save to realm
                        addTrackerToFirebase(cNumber); //save to firebase
                        refreshData();
                        //update firebase and
                        //update list
                        //update database
                    }
                }
                break;
        }
    }

    private void refreshData() {
        listnewsData.clear();
        for(Map.Entry map:GlobalInfo.myTrackers.entrySet()){
            listnewsData.add(new User(map.getValue().toString(),map.getKey().toString()));//
        }
        myAdapter.notifyDataSetChanged();
    }

    private void addTrackerToFirebase(String cNumber) {
        DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("Users").child(cNumber).child("Trackers")
                .child(GlobalInfo.PhoneNumber).setValue(true);
        // add myNumber to the one who i chose him. He will track me..
    }

    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<User> listnewsDataAdpater ;
        Context context;
        public MyCustomAdapter(ArrayList<User>  listnewsDataAdpater, Context context) {
            this.listnewsDataAdpater=listnewsDataAdpater;
            this. context=context;
        }


        @Override
        public int getCount() {
            return listnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getLayoutInflater();
            final View myView = mInflater.inflate(R.layout.single_row_conact, null);

            final User s = listnewsDataAdpater.get(position);

            TextView txv_username=( TextView)myView.findViewById(R.id.txv_username);
            txv_username.setText(s.Username);

            TextView txv_phoneNumber=( TextView)myView.findViewById(R.id.txv_phoneNumber);
            txv_phoneNumber.setText(s.PhoneNumber);

            ImageView deleteUser=(ImageView)myView.findViewById(R.id.deleteUser);

            deleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDeleting(s);
                }
            });
            return myView;
        }
        private void confirmDeleting(final User user) {

            new AlertDialog.Builder(context)
                    .setTitle("Delete "+user.Username+" ?")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listnewsDataAdpater.remove(user); // delete all user
                            GlobalInfo.myTrackers.remove(user.PhoneNumber);// delete user with the key = phone number..
                            deleteTrackerToFirebase(user.PhoneNumber); //delete from firebase
                            new GlobalInfo(context).deleteUserFromRealm(user.PhoneNumber); // delete from realm.
                            refreshData();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();

        }
        private void deleteTrackerToFirebase(String PhoneNumber ) {
            DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
            mDatabaseReference.child("Users").child(PhoneNumber).child("Trackers")
                    .child(GlobalInfo.PhoneNumber).removeValue();
        }
    }






}
