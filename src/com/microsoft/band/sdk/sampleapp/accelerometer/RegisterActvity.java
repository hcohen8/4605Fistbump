package com.microsoft.band.sdk.sampleapp.accelerometer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * Created by test on 11/8/17.
 */

public class RegisterActvity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {


    private DatabaseReference mDatabase;
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    private GoogleApiClient googleApiClient;
    private SignInButton googleButton;
    public static final int SIGN_IN_CODE = 777;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        googleButton = (SignInButton) findViewById(R.id.googleButton);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGooglePlayServicesAvailable(getApplicationContext())) {
                    System.out.println("Is installed");
                    Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(intent, 777);
                } else {
                    System.out.println("Not installed");
                }

            }
        });

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE) {
            System.out.println("YOOOOO");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            System.out.println(result.toString());
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            System.out.println("WE GOOD");
            goMainScreen();
        } else {
            System.out.println("NOT GOOD");
            Toast.makeText(this, "Did not work", Toast.LENGTH_SHORT);
        }
    }

    private void goMainScreen() {
        System.out.println("Success");
        Intent intent = new Intent(this, BandAccelerometerAppActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public boolean isGooglePlayServicesAvailable(Context context){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }
}
