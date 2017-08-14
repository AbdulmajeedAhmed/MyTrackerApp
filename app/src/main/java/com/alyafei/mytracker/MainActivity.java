package com.alyafei.mytracker;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alyafei.mytracker.Others.GlobalInfo;
import com.alyafei.mytracker.Others.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ArrayList<User> listnewsData ;
    MyCustomAdapter myAdapter;
    ListView listView;
    DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.listView); // get the view


        listnewsData= new ArrayList<User>(); // create new list.
        myAdapter=new MyCustomAdapter(listnewsData,this);// create new adapter and pass to it our arrayList., context is the context of the activity not this of the listener..
        listView.setAdapter(myAdapter); // set the adapter to your list view..


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user=listnewsData.get(position);
                GlobalInfo.UpdatesInfo(user.PhoneNumber);
                Intent intent= new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("PhoneNumber",user.PhoneNumber);
                startActivity(intent);
            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshData(this);

    }

    private void refreshData(Context mcontext) {
        listnewsData.clear();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("Users").child(GlobalInfo.PhoneNumber).child("Trackers")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                listnewsData.clear();

                if(map==null){
                    listnewsData.add( new User("null","null"));
                    //myAdapter.notifyDataSetChanged();
                    return;
                }

                // get all contact to list
                ArrayList<User> list_contact = new ArrayList<User>();
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                assert cursor != null;
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    list_contact.add(new User( name,GlobalInfo.FormatPhoneNumber(phoneNumber)
                    ));
                }


                // to to compare .. only the phone number stored in my phone will appear.
                for (  String Numbers : map.keySet()) {
                    for (User user : list_contact) {

                        //IsFound = SettingSaved.WhoIFindIN.get(cs.Detals);  // for case who i could find list
                        if (user.PhoneNumber.length() > 0)
                            if (Numbers.contains(user.PhoneNumber)) {
                                listnewsData.add(new User(user.Username, user.PhoneNumber));
                                break;
                            }

                    }
                }

                myAdapter.notifyDataSetChanged();
            } // on data change

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); // event listener..

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addTracker:
                Intent intent = new Intent(this,MyTrackers.class);
                startActivity(intent);

                return true;

            case R.id.help:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = getLayoutInflater();
            final View myView;
            final User s = listnewsDataAdpater.get(position);
           final Context context=this.context;
            if (!s.Username.equalsIgnoreCase("null")) { //normal view
                myView = mInflater.inflate(R.layout.single_row_conact, null);
                TextView txv_username = (TextView) myView.findViewById(R.id.txv_username);
                txv_username.setText(s.Username);

                TextView txv_phoneNumber = (TextView) myView.findViewById(R.id.txv_phoneNumber);
                txv_phoneNumber.setText(s.PhoneNumber);

                ImageView deleteUser = (ImageView) myView.findViewById(R.id.deleteUser);

                deleteUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // confirmDeleting(s,getApplicationContext());
                        new AlertDialog.Builder(context)
                                .setTitle("Delete "+s.Username+" ?")
                                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                       // GlobalInfo.myTrackers.remove(s.PhoneNumber);// delete user with the key = phone number..

                                        GlobalInfo.deleteTrackerToFirebase(s.PhoneNumber); //delete from firebase
                                        GlobalInfo.deleteUserFromRealm(s.PhoneNumber); // delete from realm.
                                        listnewsDataAdpater.remove(listnewsDataAdpater.get(position));
                                        notifyDataSetChanged();

                                        refreshData(context);
                                    }
                                })
                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                     //  Toast.makeText( context,"test" , Toast.LENGTH_SHORT);
                                    }
                                })
                                .show();
                    }
                });
                return myView;
            }
            else{ // the list i empty...
                myView = mInflater.inflate(R.layout.news_ticket_no_news, null);
                return myView;
        }

    }
        private void confirmDeleting(final User user, final Context mcontext) {

        /*    try {

            }catch (Exception e){
                e.printStackTrace();
            }*/


        }

    }


    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        refreshData(this);//

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;// you can put any thing ..



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refreshData(getApplicationContext()); //gps call
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
}
