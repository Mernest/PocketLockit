package com.example.ernest.pocketlockit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    // Declaring Buttons
    Button unlockButton;
    Button lockButton;

    TextView unlockTextView;


   final DatabaseReference ledStatus = myRef.child("led1").child("status");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       unlockButton = (Button) findViewById(R.id.unlockButton);
        lockButton = (Button) findViewById(R.id.lockButton);

       /*ledStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("file", "Value is " + value);
                unlockTextView.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("file", "Failed to read value", databaseError.toException());
            }
        });*/

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ledStatus.setValue("ON");
            }
        });

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ledStatus.setValue("OFF");
            }
        });
    }
}
