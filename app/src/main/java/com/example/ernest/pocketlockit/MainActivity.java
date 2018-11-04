package com.example.ernest.pocketlockit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    // Declaring Database Instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    DatabaseReference passwordRef = myRef.child("Password");

    protected String currentDbPassword;
    protected Button verifyButton;
    protected EditText passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyButton = (Button) findViewById(R.id.verifyButton);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        passwordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentDbPassword = dataSnapshot.getValue(String.class);

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

}
