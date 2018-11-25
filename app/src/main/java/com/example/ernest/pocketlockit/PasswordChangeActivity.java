package com.example.ernest.pocketlockit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PasswordChangeActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    protected Button saveButton;
    protected EditText currentPasswordEditText;
    protected EditText newPasswordEditText;
    protected EditText verifyNewPasswordEditText;
    protected String currentDbPassword;

    protected String getCurrentPasswordEditText;
    protected String getNewPasswordEditText;
    protected String getVerifyNewPasswordEditText;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
        checkFields();
        }
    };

    private void checkFields(){
        getCurrentPasswordEditText = currentPasswordEditText.getText().toString();
        getNewPasswordEditText = newPasswordEditText.getText().toString();
        getVerifyNewPasswordEditText = verifyNewPasswordEditText.getText().toString();

        if(getCurrentPasswordEditText.equals(currentDbPassword) && getNewPasswordEditText.equals(getVerifyNewPasswordEditText) && !getNewPasswordEditText.equals("")
                && !getVerifyNewPasswordEditText.equals("")){
            saveButton.setEnabled(true);
        }
        else {
            saveButton.setEnabled(false);
        }
    }

    DatabaseReference passwordRef = myRef.child("Password");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        this.setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveButton = (Button) findViewById(R.id.saveButton);
        currentPasswordEditText = (EditText) findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = (EditText) findViewById(R.id.newPasswordEditText);
        verifyNewPasswordEditText = (EditText) findViewById(R.id.verifyNewPasswordEditText);


        passwordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentDbPassword = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        currentPasswordEditText.addTextChangedListener(mTextWatcher);
        newPasswordEditText.addTextChangedListener(mTextWatcher);
        verifyNewPasswordEditText.addTextChangedListener(mTextWatcher);

        checkFields();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordRef.setValue(getNewPasswordEditText);
                saveButton.setVisibility(View.GONE);

                currentPasswordEditText.setFocusable(false);
                newPasswordEditText.setFocusable(false);
                verifyNewPasswordEditText.setFocusable(false);
                goToLockUnlockActivity();

            }
        });
    }

    void goToLockUnlockActivity(){
        Intent intent = new Intent(PasswordChangeActivity.this, LockUnlockActivity.class);
        startActivity(intent);
    }
}
