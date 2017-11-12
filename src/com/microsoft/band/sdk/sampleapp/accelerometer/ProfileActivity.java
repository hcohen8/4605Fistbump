package com.microsoft.band.sdk.sampleapp.accelerometer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by test on 11/8/17.
 */

public class ProfileActivity extends Activity {

    private DatabaseReference myRef;
    private static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_info);



        final EditText name = (EditText) findViewById(R.id.updateName);
        final EditText phone = (EditText) findViewById(R.id.updatePhone);
        final EditText email = (EditText) findViewById(R.id.updateEmail);



        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(ProfileActivity.this, BandAccelerometerAppActivity.class);
//				myIntent.putExtra("key", value); //Optional parameters
                ProfileActivity.this.startActivity(myIntent);
            }
        });


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        final String key = prefs.getString("id", null);

        System.out.print("RETRIEVED " + key);

        myRef = FirebaseDatabase.getInstance().getReference().child("users");
		myRef.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User current = dataSnapshot.getValue(User.class);
//                System.out.println("USER " + current);
                System.out.println("CURRENT KEY " + dataSnapshot.getKey());
                if (current != null && dataSnapshot.getKey().matches(key)) {
                    System.out.println("IN HERE");
                    name.setText(current.getName());
                    phone.setText(current.getPhone());
                    email.setText(current.getEmail());
                }

			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {

			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

        Button updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                User user = new User(name.getText().toString(), phone.getText().toString(), email.getText().toString());
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                final String key = prefs.getString("id", null);
                myRef.child(key).setValue(user);
                Toast.makeText(getBaseContext(), "Profile Updated",
                        Toast.LENGTH_LONG).show();
            }
        });


    }

}
