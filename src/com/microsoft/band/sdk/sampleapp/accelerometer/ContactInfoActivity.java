package com.microsoft.band.sdk.sampleapp.accelerometer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by test on 11/8/17.
 */

public class ContactInfoActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_contact);

        String passedName = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            passedName = extras.getString("name");

        }

        TextView name =  (TextView) findViewById(R.id.contactName);
        TextView phone =  (TextView) findViewById(R.id.contactPhone);
        TextView email =  (TextView) findViewById(R.id.contactEmail);

        name.append(passedName);
        phone.append("123-456-7890");
        email.append("hank@gmail.com");

        Button backButton = (Button) findViewById(R.id.backButton1);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(ContactInfoActivity.this, BandAccelerometerAppActivity.class);
//				myIntent.putExtra("key", value); //Optional parameters
                ContactInfoActivity.this.startActivity(myIntent);
            }
        });


    }
}
