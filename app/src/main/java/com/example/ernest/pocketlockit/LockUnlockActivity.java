package com.example.ernest.pocketlockit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class LockUnlockActivity extends AppCompatActivity {


    // Declaring Database Instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    // Declaring needed references
    final DatabaseReference ledResponse = myRef.child ("LockResponse");
    final DatabaseReference ledStatus = myRef.child("LockStatus");

    // Needed Declarations
    Button unlockButton;
    Button lockButton;
    Switch notificationSwitch;

    boolean currentStatus;
    boolean unlockPressed;
    boolean prevUnlockPressed;
    boolean switchOnOff;
    boolean toggle;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCH1 = "switch1";

   // protected SharedPreferenceHelper sharedPreferenceHelper;

    //SharedPreferences.Editor editor = sharedPreferences.edit();

    SharedPreferences sharedPreferences;
    SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_unlock);

        unlockButton = (Button) findViewById(R.id.unlockButton);
        lockButton = (Button) findViewById(R.id.lockButton);
        notificationSwitch = (Switch) findViewById(R.id.notificationSwitch);

        sharedPreferences = this.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);

        sharedPreferenceHelper = new SharedPreferenceHelper(LockUnlockActivity.this);

        loadData();
        updateViews();


       notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //SharedPreferences sharedPreferences = getSharedPreferences("toggleValue", Context.MODE_PRIVATE);
            //SharedPreferences.Editor editor = sharedPreferences.edit();
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

               if(isChecked){
                   // toggle =true;
                   sharedPreferenceHelper.saveToggleValue(true);
                   String verification = String.valueOf(sharedPreferenceHelper.getToggleValue());
                   Toast toast = Toast.makeText(getApplicationContext(), verification , Toast.LENGTH_SHORT);
                   toast.show();

//                    editor.putBoolean("Value", true);
//                    editor.apply();
//                    Intent i = new Intent();
//                    i.putExtra("toggleValue",true);
                }
                else{
                   //toggle = false;
                   sharedPreferenceHelper.saveToggleValue(false);
                   String verification = String.valueOf(sharedPreferenceHelper.getToggleValue());
                   Toast toast = Toast.makeText(getApplicationContext(), verification , Toast.LENGTH_SHORT);
                   toast.show();
               }
//                toggle =false;
//                editor.putBoolean("Value", false);
//                editor.apply();
//                    Intent i = new Intent();
//                    i.putExtra("toggleValue",true);
               saveData();

            }

        });

        ledStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                unlockPressed = dataSnapshot.getValue(boolean.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ledResponse.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentStatus = dataSnapshot.getValue(boolean.class);


                if (currentStatus && unlockPressed){
                    unlockButton.setEnabled(false);
                    lockButton.setEnabled(true);
                    Toast toast = Toast.makeText(getApplicationContext(), "Door is Unlocked" , Toast.LENGTH_SHORT);
                    toast.show();

                    if(prevUnlockPressed) {
                        Calendar calendar = Calendar.getInstance();
                        Date date = calendar.getTime();
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        String formattedDate = dateFormat.format(date);
                        String currentDate = DateFormat.getDateInstance().format(calendar.getTime()) + "\n" + formattedDate;
                        DatabaseHelper dbhelper = new DatabaseHelper(LockUnlockActivity.this);
                        dbhelper.insertLogItem(new LogItem(-1, currentDate, "Door opened"));
                        prevUnlockPressed = true;
                    }
                }
                else {
                    unlockButton.setEnabled(true);
                    lockButton.setEnabled(false);
                    prevUnlockPressed = false;
                    Toast toast = Toast.makeText(getApplicationContext(), "Door is Locked" , Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ledStatus.setValue(true);
                prevUnlockPressed = true;
            }
        });

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ledStatus.setValue(false);
                prevUnlockPressed = false;

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();

        switch(id){
            case R.id.passwordChangeMenu:
                goToPasswordChangeActivity();
                return true;
            case R.id.logMenu:
                finish();
                goToLogActivity();
                return true;
        }
        return true;
    }

    void goToPasswordChangeActivity(){
        Intent intent = new Intent(LockUnlockActivity.this, PasswordChangeActivity.class);
        startActivity(intent);
    }

    void goToLogActivity(){
        Intent intent = new Intent(LockUnlockActivity.this, LogActivity.class);
        startActivity(intent);
    }

    public void saveData(){
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWITCH1,notificationSwitch.isChecked());
       // editor.putBoolean("switchValue",switchOnOff);
        editor.apply();
    }
    public void loadData(){
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        switchOnOff = sharedPreferences.getBoolean(SWITCH1,false);
    }
    public void updateViews(){
        notificationSwitch.setChecked(switchOnOff);
    }


}
