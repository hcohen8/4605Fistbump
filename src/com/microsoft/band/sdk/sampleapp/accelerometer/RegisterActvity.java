package com.microsoft.band.sdk.sampleapp.accelerometer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by test on 11/8/17.
 */

public class RegisterActvity extends Activity {


    private DatabaseReference mDatabase;
    private static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        System.out.println("REFERENCE " + mDatabase);

        final EditText name = (EditText) findViewById(R.id.editName);
        final EditText phone = (EditText) findViewById(R.id.editPhone);
        final EditText email = (EditText) findViewById(R.id.editEmail);

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty()) {
                    User user = new User(name.getText().toString(), phone.getText().toString(), email.getText().toString());
                    String key =  mDatabase.push().getKey();
                    System.out.println("KEY " + key);
                    mDatabase.child("users").child(key).setValue(user);


                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("id", key);

                    editor.apply();

                    Intent myIntent = new Intent(RegisterActvity.this, BandAccelerometerAppActivity.class);
//				myIntent.putExtra("key", value); //Optional parameters
                    RegisterActvity.this.startActivity(myIntent);
                }

            }
        });

    }
}
