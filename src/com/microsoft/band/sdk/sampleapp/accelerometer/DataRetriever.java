package com.microsoft.band.sdk.sampleapp.accelerometer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by test on 11/28/17.
 */

public class DataRetriever extends AsyncTask<String, String, String> {


    // The variable is moved here, we only need it here while displaying the
    // progress dialog.
    TextView txtView;

    @Override
    protected void onPreExecute() {

        // Set the variable txtView here, after setContentView on the dialog
        // has been called! use dialog.findViewById().

    }

    @Override
    protected String doInBackground(String... strings) {
        URL url = null;
        String output = "";
        try {
            url = new URL("https://8080-dot-3230247-dot-devshell.appspot.com/work_magic?data=1,2,3\\n4,5,6\\n7,8,9\\n");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            output = in.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return output;
    }
}
