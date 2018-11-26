package com.example.ernest.pocketlockit;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.ernest.pocketlockit.App.CHANNEL;
import static com.example.ernest.pocketlockit.LockUnlockActivity.SHARED_PREFS;

public class MainActivity extends AppCompatActivity {

    private Animation shakeIt;
    // Declaring Database Instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    DatabaseReference passwordRef = myRef.child("Password");
    DatabaseReference motionStatusRef = myRef.child("MotionStatus");
    DatabaseReference ledResponse = myRef.child ("LockResponse");

    protected String currentDbPassword;
    protected Button verifyButton;
    protected EditText passwordEditText;
    protected boolean motionStatus;

    private NotificationManagerCompat notificationManager;
    TextView redCircle, greenCircle;

    SharedPreferenceHelper sharedPreferenceHelper;
    Vibrator vibrator;

    boolean lockStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("PocketLock-it");
        shakeIt = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        redCircle = (TextView) findViewById(R.id.redcircle);
        greenCircle = (TextView) findViewById(R.id.greencircle);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        sharedPreferenceHelper = new SharedPreferenceHelper(MainActivity.this);

        notificationManager = NotificationManagerCompat.from(this);

       // Intent activityIntent = new Intent(this, MainActivity.class);
        Intent activityIntent = new Intent(Intent.ACTION_DIAL);
        activityIntent.setData(Uri.parse("tel:"));
        final PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,0);

        passwordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentDbPassword = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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
                                .setColor(Color.BLUE)
                                .setContentIntent(contentIntent)
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.image)
                                .build();
                        notificationManager.notify(1, notification);
                    }
                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    String formattedDate = dateFormat.format(date);
                    String currentDate = DateFormat.getDateInstance().format(calendar.getTime()) + "\n" + formattedDate;
                    DatabaseHelper dbhelper = new DatabaseHelper(MainActivity.this);
                    dbhelper.insertLogItem(new LogItem(-1, currentDate, "Motion Detected"));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (passwordEditText.getText().toString().equals(currentDbPassword)){

                    goToLockUnlockActivity();
                }
                else {
                     verifyButton.startAnimation((shakeIt));
                     vibrator.vibrate(100);
                     Toast toast = Toast.makeText(getApplicationContext(), "Password is incorrect", Toast.LENGTH_SHORT);
                     toast.show();
                 }
            }
        });

        ledResponse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lockStatus = dataSnapshot.getValue(boolean.class);
                if (lockStatus){
                    redCircle.setVisibility(View.INVISIBLE);
                    greenCircle.setVisibility(View.VISIBLE);
                }
                else{
                    redCircle.setVisibility(View.VISIBLE);
                    greenCircle.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (lockStatus){
            redCircle.setVisibility(View.INVISIBLE);
            greenCircle.setVisibility(View.VISIBLE);
        }
        else{
            redCircle.setVisibility(View.VISIBLE);
            greenCircle.setVisibility(View.INVISIBLE);
        }
    }

    void goToLockUnlockActivity(){
        Intent intent = new Intent(MainActivity.this, LockUnlockActivity.class);
        startActivity(intent);
    }


}
