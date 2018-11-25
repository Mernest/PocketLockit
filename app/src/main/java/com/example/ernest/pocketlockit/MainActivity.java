package com.example.ernest.pocketlockit;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.ernest.pocketlockit.App.CHANNEL;
import static com.example.ernest.pocketlockit.LockUnlockActivity.SHARED_PREFS;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCH1 = "switch1";
    // Declaring Database Instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    DatabaseReference passwordRef = myRef.child("Password");
    DatabaseReference motionStatusRef = myRef.child("MotionStatus");

    protected String currentDbPassword;
    protected Button verifyButton;
    protected EditText passwordEditText;
    protected boolean motionStatus;
    //protected boolean receivedToggle;

    private NotificationManagerCompat notificationManager;

    SharedPreferenceHelper sharedPreferenceHelper;
    //SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyButton = (Button) findViewById(R.id.verifyButton);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        sharedPreferenceHelper = new SharedPreferenceHelper(MainActivity.this);
        String verification = String.valueOf(sharedPreferenceHelper.getToggleValue());
        Toast toast = Toast.makeText(getApplicationContext(), verification , Toast.LENGTH_SHORT);
        toast.show();
        //sharedPreferences = getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);

        notificationManager = NotificationManagerCompat.from(this);


        //sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        //receivedToggle = sharedPreferences.getBoolean("SWITCH1",false);

        passwordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentDbPassword = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

       // SharedPreferences result = getSharedPreferences("toggleValue", Context.MODE_PRIVATE);
        //Intent data = new Intent();
        //receivedToggle = result.getBoolean("Value", true);
        //receivedToggle = data.getBooleanExtra("toggleValue",false);
       // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //receivedToggle = sharedPreferences.getBoolean(SWITCH1, false);
      //  receivedToggle = sharedPreferenceHelper.getToggleValue();

       // if (sharedPreferenceHelper.getToggleValue()) {
            motionStatusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    motionStatus = dataSnapshot.getValue(boolean.class);
                    if (motionStatus && sharedPreferenceHelper.getToggleValue()) {
                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL).setSmallIcon(R.drawable.ic_stat_name)
                                .setContentTitle("Motion")
                                .setContentText("Someone is close to your door")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_STATUS)
                                .build();
                        notificationManager.notify(1, notification);
                    }
                    // Toast toast = Toast.makeText(getApplicationContext(), motionStatus, Toast.LENGTH_SHORT);
                    //toast.show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        //}
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (passwordEditText.getText().toString().equals(currentDbPassword)){

                    goToLockUnlockActivity();
                }
                else {
                     //Toast toast = Toast.makeText(getApplicationContext(), currentDbPassword, Toast.LENGTH_SHORT);
                     Toast toast = Toast.makeText(getApplicationContext(), "Password is incorrect", Toast.LENGTH_SHORT);
                     toast.show();
                 }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferenceHelper = new SharedPreferenceHelper(MainActivity.this);

        String verification = String.valueOf(sharedPreferenceHelper.getToggleValue());
        Toast toast = Toast.makeText(getApplicationContext(), verification , Toast.LENGTH_SHORT);
        toast.show();
    }

    void goToLockUnlockActivity(){
        Intent intent = new Intent(MainActivity.this, LockUnlockActivity.class);
        startActivity(intent);
    }

}
