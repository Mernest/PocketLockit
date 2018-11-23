package com.example.ernest.pocketlockit;

import android.app.Notification;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.ernest.pocketlockit.App.CHANNEL;

public class MainActivity extends AppCompatActivity {


    // Declaring Database Instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    DatabaseReference passwordRef = myRef.child("Password");
    DatabaseReference motionStatusRef = myRef.child("MotionStatus");

    protected String currentDbPassword;
    protected Button verifyButton;
    protected EditText passwordEditText;

//    ImageView cactus;
//    ImageView redcactus;
//    ImageView greencactus;

    private NotificationManagerCompat notificationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        cactus = (ImageView) findViewById(R.id.cactus);
//        cactus.setVisibility(View.VISIBLE);
//        redcactus = (ImageView) findViewById(R.id.redcactus);
//        redcactus.setVisibility(View.INVISIBLE);
//        greencactus = (ImageView)findViewById(R.id.greencactus);
//        greencactus.setVisibility(View.INVISIBLE);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        notificationManager = NotificationManagerCompat.from(this);


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
                boolean motionStatus = dataSnapshot.getValue(boolean.class);
                if (motionStatus){
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

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                cactus.setVisibility(View.INVISIBLE);
//                redcactus.setVisibility(View.INVISIBLE );
//                greencactus.setVisibility(View.VISIBLE);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        cactus.setVisibility(View.VISIBLE);
//                        greencactus.setVisibility(View.INVISIBLE);
//                    }}, 2000);

                 if (passwordEditText.getText().toString().equals(currentDbPassword)){

                    goToLockUnlockActivity();
                }
                else {
//                     cactus.setVisibility(View.INVISIBLE);
//                     greencactus.setVisibility(View.INVISIBLE);
//                     redcactus.setVisibility(View.VISIBLE);
                     //new Handler().postDelayed(new Runnable() {
                       //  @Override
                       //  public void run() {
                        //     cactus.setVisibility(View.VISIBLE);
                       //      redcactus.setVisibility(View.INVISIBLE);
                       //  }}, 2000);
                     //Toast toast = Toast.makeText(getApplicationContext(), currentDbPassword, Toast.LENGTH_SHORT);
                     Toast toast = Toast.makeText(getApplicationContext(), "Password is incorrect", Toast.LENGTH_SHORT);
                     toast.show();
                 }
            }
        });


    }

    void goToLockUnlockActivity(){
        Intent intent = new Intent(MainActivity.this, LockUnlockActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

}
