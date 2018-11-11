package com.example.ernest.pocketlockit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    boolean currentStatus;
    String toStringStatus;
    ImageView redLock;
    ImageView greenUnlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_unlock);

        unlockButton = (Button) findViewById(R.id.unlockButton);
        lockButton = (Button) findViewById(R.id.lockButton);
        redLock = (ImageView) findViewById(R.id.redLock);
        greenUnlock = (ImageView) findViewById(R.id.greenUnlock);

        ledResponse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentStatus = dataSnapshot.getValue(boolean.class);
                toStringStatus = String.valueOf(currentStatus);

                if (currentStatus){
                    unlockButton.setEnabled(false);
                    lockButton.setEnabled(true);
                    greenUnlock.setVisibility(View.VISIBLE);
                    redLock.setVisibility(View.INVISIBLE);
                    Toast toast = Toast.makeText(getApplicationContext(), "Door is Unlocked" , Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    unlockButton.setEnabled(true);
                    lockButton.setEnabled(false);
                    greenUnlock.setVisibility(View.INVISIBLE);
                    redLock.setVisibility(View.VISIBLE);
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

            }
        });

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ledStatus.setValue(false);

            }
        });
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
                Toast.makeText(this,"Edit Button Clicked",Toast.LENGTH_SHORT).show();
                goToPasswordChangeActivity();
        }
        return true;
    }

    void goToPasswordChangeActivity(){
        Intent intent = new Intent(LockUnlockActivity.this, PasswordChangeActivity.class);
        startActivity(intent);
    }

}
