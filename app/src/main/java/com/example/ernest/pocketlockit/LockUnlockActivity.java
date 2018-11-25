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
    Button lockButton;
    Switch notificationSwitch;

    boolean currentStatus;
    boolean unlockPressed;
    boolean prevUnlockPressed;
    boolean switchOnOff;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCH1 = "switch1";

    TextView redCircle, greenCircle;
    SharedPreferences sharedPreferences;
    SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_unlock);
        redCircle = (TextView) findViewById(R.id.redcircle);
        greenCircle = (TextView) findViewById(R.id.greencircle);
        this.setTitle("Door Status");

        lockButton = (Button) findViewById(R.id.lockButton);
        notificationSwitch = (Switch) findViewById(R.id.notificationSwitch);

        sharedPreferences = this.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);

        sharedPreferenceHelper = new SharedPreferenceHelper(LockUnlockActivity.this);

        loadData();
        updateViews();


       notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

               if(isChecked){
                   sharedPreferenceHelper.saveToggleValue(true);

                }
                else{
                   sharedPreferenceHelper.saveToggleValue(false);

               }

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
                    redCircle.setVisibility(View.INVISIBLE);
                    greenCircle.setVisibility(View.VISIBLE);
                    sharedPreferenceHelper.saveLockValue(true);
                    lockButton.setText("LOCK");
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
                else if(!currentStatus && !unlockPressed ){

                    redCircle.setVisibility(View.VISIBLE);
                    greenCircle.setVisibility(View.INVISIBLE);
                    lockButton.setText("UNLOCK");
                    prevUnlockPressed = false;
                    Toast toast = Toast.makeText(getApplicationContext(), "Door is Locked" , Toast.LENGTH_SHORT);
                    toast.show();
                    sharedPreferenceHelper.saveLockValue(false);
                }
                else if (currentStatus && !unlockPressed){
                    redCircle.setVisibility(View.INVISIBLE);
                    greenCircle.setVisibility(View.VISIBLE);
                    lockButton.setText("LOCK");
                    sharedPreferenceHelper.saveLockValue(true);
                }
                else {
                    redCircle.setVisibility(View.VISIBLE);
                    greenCircle.setVisibility(View.INVISIBLE);
                    lockButton.setText("UNLOCK");
                    sharedPreferenceHelper.saveLockValue(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lockButton.getText().equals("LOCK")){
                    ledStatus.setValue(false);
                    prevUnlockPressed = false;

                }else{
                    ledStatus.setValue(true);
                    prevUnlockPressed = true;
                }
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
    protected void onRestart(){
        super.onRestart();
        Intent intent = new Intent(LockUnlockActivity.this, MainActivity.class);
        startActivity(intent);
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
