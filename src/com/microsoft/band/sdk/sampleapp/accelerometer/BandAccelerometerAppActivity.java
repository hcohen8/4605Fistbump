//Copyright (c) Microsoft Corporation All rights reserved.  
// 
//MIT License: 
// 
//Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
//documentation files (the  "Software"), to deal in the Software without restriction, including without limitation
//the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
//to permit persons to whom the Software is furnished to do so, subject to the following conditions: 
// 
//The above copyright notice and this permission notice shall be included in all copies or substantial portions of
//the Software. 
// 
//THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
//TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
//THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
//CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//IN THE SOFTWARE.
package com.microsoft.band.sdk.sampleapp.accelerometer;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.notifications.BandNotificationManager;
import com.microsoft.band.notifications.VibrationType;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.SampleRate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import com.microsoft.band.tiles.BandIcon;
import com.microsoft.band.tiles.BandTile;

import android.*;
import android.Manifest;
import android.app.DownloadManager;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVWriter;

import static java.security.AccessController.getContext;


public class BandAccelerometerAppActivity extends Activity {

	private BandClient client = null;
	private Button btnStart;
	private Button btnNotif;
	private TextView txtStatus;
	private TextView gyroscopeStatus;
	private GraphView graph;
	private GraphView graph2;
    private ArrayList<DataPoint> aPointsX;
    private ArrayList<DataPoint> aPointsY;
    private ArrayList<DataPoint> aPointsZ;
    private ArrayList<DataPoint> gPointsX;
    private ArrayList<DataPoint> gPointsY;
    private ArrayList<DataPoint> gPointsZ;
	private LineGraphSeries<DataPoint> seriesX1, seriesY1, seriesZ1, seriesX2, seriesY2, seriesZ2;
	private double y,x;
	private boolean isRecording = false;
	private int count = 0;
    long tStart;
	ArrayList<String> names = new ArrayList<>();
	private DatabaseReference myRef;
	private LocationManager locationManager;
	private LocationListener listener;
	private static final String MY_PREFS_NAME = "MyPrefsFile";
	String inputLine;

	private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
            if (event != null) {



                long tEnd = System.currentTimeMillis();
                long tDelta = tEnd - tStart;
                double elapsedSeconds = tDelta / 1000.0;
				updateGraph1(elapsedSeconds, event.getAccelerationX(), event.getAccelerationY(), event.getAccelerationZ());
//            	appendToUI(String.format(" X = %.3f \n Y = %.3f\n Z = %.3f", event.getAccelerationX(),
//            			event.getAccelerationY(), event.getAccelerationZ()), false);

            }
        }
    };

    private BandGyroscopeEventListener gyroscopeEventListener = new BandGyroscopeEventListener() {
		@Override
		public void onBandGyroscopeChanged(BandGyroscopeEvent bandGyroscopeEvent) {

            long tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            double elapsedSeconds = tDelta / 1000.0;
			updateGraph2(elapsedSeconds, bandGyroscopeEvent.getAngularVelocityX(), bandGyroscopeEvent.getAngularVelocityY(), bandGyroscopeEvent.getAngularVelocityZ());
//			appendToUI(String.format(" X = %.3f \n Y = %.3f\n Z = %.3f", bandGyroscopeEvent.getAngularVelocityX(),
//					bandGyroscopeEvent.getAngularVelocityY(), bandGyroscopeEvent.getAngularVelocityZ()), true);

		}

	};


	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);


		myRef = FirebaseDatabase.getInstance().getReference();

		final TextView mTextView = (TextView) findViewById(R.id.text);
		mTextView.setClickable(true);
		mTextView.setMovementMethod(LinkMovementMethod.getInstance());
		String text = "<a href='https://8080-dot-3230247-dot-devshell.appspot.com/?authuser=0'> Clickable </a>";
		mTextView.setText(Html.fromHtml(text));
		final Button myButton = (Button) findViewById(R.id.myButton);

		myButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				URL url = null;
				try {
					url = new URL("https://8080-dot-3230247-dot-devshell.appspot.com/?key=AIzaSyDcMHb1iEge8NdY0wCCh116zUOERBUqPOw");
//					url = new URL("http://10.0.2.2:8080/");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				HttpURLConnection urlConnection = null;
				try {
					System.out.println("CONNECTING");
					urlConnection = (HttpURLConnection) url.openConnection();
					System.out.println("RESULTS: " + urlConnection.toString());

				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					InputStreamReader streamReader = new InputStreamReader(urlConnection.getInputStream());
					BufferedReader reader = new BufferedReader(streamReader);
					StringBuilder stringBuilder = new StringBuilder();
//					InputStream in = new BufferedInputStream((InputStream) urlConnection.getInputStream());
//					byte[] contents = new byte[1024];
//					int bytesRead = 0;
//					String strContents = "";
//					while((bytesRead = in.read(contents)) != -1) {
//						strContents += new String(contents, 0, bytesRead);
//					}
//					System.out.println(strContents);

					while((inputLine = reader.readLine()) != null){
						stringBuilder.append(inputLine);
					}
					//Close our InputStream and Buffered reader
					reader.close();
					streamReader.close();
					//Set our result equal to our stringBuilder
					String result = stringBuilder.toString();
					System.out.println(result);
					mTextView.setText(result.toString());
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					urlConnection.disconnect();
				}

			}
		});







//		try {
//			getConnectedBandClient();
//		} catch (Exception e) {
//			System.out.println("Could not get DAT");
//		}
//		btnNotif.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
////				List<BandTile> tileList = null;
////				try {
////					tileList = client.getTileManager().getTiles().await();
////				} catch (Exception e) {
////					System.out.println("Could not get tiles.");
////				}
////				try {
////					System.out.println("CLICKED");
////					for (BandTile bt : tileList) {
////						bt.getTileId();
////					}
////				} catch (Exception e) {}
//				String details = "Contace information exchanged with " + "Alex";
//				try{
//					System.out.println("&&&&&&&&&&&&&&Clicked");
//					BandNotificationManager man = client.getNotificationManager();
//					System.out.println("&&&&&&&&&&&&&&Got Notif Manager");
//					System.out.println(man.toString());
//					if (man == null) {
//						System.out.println("&&&&&&&&&&MAN IS NULL");
//					}
////					BandPendingResult vib = man.vibrate(VibrationType.NOTIFICATION_ONE_TONE);
//					System.out.println("&&&&&&&&&&&&&&About to vibrate");
////					vib.await();
//					client.getNotificationManager().showDialog(UUID.fromString("Sleep"), "Information Recieved", details).await();
////				} catch (BandException e) {
//				} catch (Exception e) {
//
//					System.out.println("NO CAN DO");
//					// handle BandException
//				}
//
//			}
//		});

		ListView myListView =  (ListView) findViewById(android.R.id.list);
		myListView.setFastScrollEnabled(true);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, names){

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view =super.getView(position, convertView, parent);

				TextView textView=(TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
				textView.setTextColor(Color.BLACK);

				return view;
			}
		};
		myListView.setAdapter(adapter);
		System.out.println("CHECK1");
		ImageView img = (ImageView) findViewById(R.id.profilePicture);
		img.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(BandAccelerometerAppActivity.this, ProfileActivity.class);
//				myIntent.putExtra("key", value); //Optional parameters
				BandAccelerometerAppActivity.this.startActivity(myIntent);
			}
		});


		myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
				Intent myIntent = new Intent(BandAccelerometerAppActivity.this, ContactInfoActivity.class);
				System.out.println("HERE " + names.get(position));
				myIntent.putExtra("name", names.get(position)); //Optional parameters
				BandAccelerometerAppActivity.this.startActivity(myIntent);
			}
		});

		System.out.println("CHECK2");
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		System.out.println("LOCATION MANAGER: " + locationManager.toString());

		listener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				System.out.println("HERE1");
				SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
				final String key = prefs.getString("id", null);
				System.out.println("UPDATE: " + location.getLongitude() + " " + location.getLatitude());
				myRef.child("locations").child(key).child("coords").setValue(location.getLongitude() + ", " + location.getLatitude());
			}

			@Override
			public void onStatusChanged(String s, int i, Bundle bundle) {
				System.out.println("HERE2");
			}

			@Override
			public void onProviderEnabled(String s) {
				System.out.println("HERE3");
			}

			@Override
			public void onProviderDisabled(String s) {
				System.out.println("DISABLED");
				Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(i);
			}
		};
		System.out.println("CONFIGURE");
		configure();

//		myRef.addChildEventListener(new ChildEventListener() {
//			@Override
//			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//				hospitals.add(dataSnapshot.getKey());
//				hospitalAdapter.notifyDataSetChanged();
//
//			}

//			@Override
//			public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//			}
//
//			@Override
//			public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//			}
//
//			@Override
//			public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//			}
//
//			@Override
//			public void onCancelled(DatabaseError databaseError) {
//
//			}
//		});




//		x = 0.0;
//		y = 0.0;
//        aPointsX = new ArrayList<>();
//        aPointsY = new ArrayList<>();
//        aPointsZ = new ArrayList<>();
//        gPointsX = new ArrayList<>();
//        gPointsY = new ArrayList<>();
//        gPointsZ = new ArrayList<>();
//		gyroscopeStatus = (TextView) findViewById(R.id.gyroscopeStats);
//        txtStatus = (TextView) findViewById(R.id.txtStatus);
//        btnStart = (Button) findViewById(R.id.btnStart);
//		graph = (GraphView) findViewById(R.id.graph1);
//		graph2 = (GraphView) findViewById(R.id.graph2);
//		seriesX1 = new LineGraphSeries<>();
//		seriesY1 = new LineGraphSeries<>();
//		seriesZ1 = new LineGraphSeries<>();
//		seriesX1.setColor(Color.BLUE);
//		seriesY1.setColor(Color.GREEN);
//		seriesZ1.setColor(Color.MAGENTA);
//		graph.addSeries(seriesX1);
//		graph.addSeries(seriesY1);
//		graph.addSeries(seriesZ1);
//
//		seriesX2 = new LineGraphSeries<>();
//		seriesY2 = new LineGraphSeries<>();
//		seriesZ2 = new LineGraphSeries<>();
//		seriesX2.setColor(Color.BLUE);
//		seriesY2.setColor(Color.GREEN);
//		seriesZ2.setColor(Color.MAGENTA);
//		graph2.addSeries(seriesX2);
//		graph2.addSeries(seriesY2);
//		graph2.addSeries(seriesZ2);
//
//        btnStart.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				txtStatus.setText("");
//
//				if (!isRecording) {
//					btnStart.setText("Stop");
//					new GyroscopeSubscriptionTask().execute();
//					new AccelerometerSubscriptionTask().execute();
//					isRecording = true;
//                    tStart = System.currentTimeMillis();
//				} else {
//					btnStart.setText("Start");
//					try {
//						client.getSensorManager().unregisterAccelerometerEventListener(mAccelerometerEventListener);
//						client.getSensorManager().unregisterGyroscopeEventListener(gyroscopeEventListener);
//					} catch (BandIOException e) {
//						appendToUI(e.getMessage(), false);
//					}
//
//					try {
//                        exportToCSV(aPointsX, aPointsY, aPointsZ, gPointsX, gPointsY, gPointsZ);
//                        seriesX1 = new LineGraphSeries<DataPoint>();
//                        seriesY1 = new LineGraphSeries<>();
//                        seriesZ1 = new LineGraphSeries<>();
//                        seriesX2 = new LineGraphSeries<>();
//                        seriesY2 = new LineGraphSeries<>();
//                        seriesZ2 = new LineGraphSeries<>();
//                        aPointsX = new ArrayList<DataPoint>();
//                        aPointsY = new ArrayList<DataPoint>();
//                        aPointsZ = new ArrayList<DataPoint>();
//                        gPointsX = new ArrayList<DataPoint>();
//                        gPointsY = new ArrayList<DataPoint>();
//                        gPointsZ = new ArrayList<DataPoint>();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    isRecording = false;
//				}
//
//
//			}
//		});




//		for(int i =0; i<100; i++) {
//
//			y = Math.sin(x);
//			series.appendData(new DataPoint(x, y), true, 100);
//		}

    }
    
//    @Override
//	protected void onResume() {
//		super.onResume();
//		txtStatus.setText("");
//	}
//
//    @Override
//	protected void onPause() {
//		super.onPause();
//		if (client != null) {
//			try {
//				client.getSensorManager().unregisterAccelerometerEventListener(mAccelerometerEventListener);
//				client.getSensorManager().unregisterGyroscopeEventListener(gyroscopeEventListener);
//			} catch (BandIOException e) {
//				appendToUI(e.getMessage(), false);
//			}
//		}
//	}




	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode){
			case 10:
				System.out.println("Configure");
				configure();
				break;
			default:
				System.out.println("NO PERMISSION");
				break;
		}
	}

	void configure(){
		// first check for permissions
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
						,10);
			}
			return;
		}
		System.out.println("FINAL");
		locationManager.requestLocationUpdates("gps", 5000, 0, listener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, listener);
	}


	private void updateGraph1 (final double timestamp, final float x, final float y, final float z)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				seriesX1.appendData(new DataPoint(timestamp, x), true, 25);
				seriesY1.appendData(new DataPoint(timestamp, y), true, 25);
				seriesZ1.appendData(new DataPoint(timestamp, z), true, 25);
                aPointsX.add(new DataPoint(timestamp, x));
                aPointsY.add(new DataPoint(timestamp, y));
                aPointsZ.add(new DataPoint(timestamp, z));
				graph.removeAllSeries();
				graph.addSeries(seriesX1);
				graph.addSeries(seriesY1);
				graph.addSeries(seriesZ1);
			}
		});
	}

	private void updateGraph2 (final double timestamp, final float x, final float y, final float z)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				seriesX2.appendData(new DataPoint(timestamp, x), true, 25);
				seriesY2.appendData(new DataPoint(timestamp, y), true, 25);
				seriesZ2.appendData(new DataPoint(timestamp, z), true, 25);
                gPointsX.add(new DataPoint(timestamp, x));
                gPointsY.add(new DataPoint(timestamp, y));
                gPointsZ.add(new DataPoint(timestamp, z));
				graph2.removeAllSeries();
				graph2.addSeries(seriesX2);
				graph2.addSeries(seriesY2);
				graph2.addSeries(seriesZ2);
			}
		});
	}

	private void exportToCSV(ArrayList<DataPoint> xAccData, ArrayList<DataPoint> yAccData, ArrayList<DataPoint> zAccData, ArrayList<DataPoint> xGyrData, ArrayList<DataPoint> yGyrData, ArrayList<DataPoint> zGyrData) throws IOException {
        String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        Date d = new Date();
        String fileName = "LightSwitchData" + d.toString() + ".csv";
        String filePath = csv + File.separator + fileName;
        CSVWriter writer = new CSVWriter(new FileWriter(filePath));
        ArrayList<String[]> strList = new ArrayList<>();
        for(int i = 0; i < xAccData.size(); i++) {
            String[] str = {String.valueOf(xAccData.get(i).getX()), String.valueOf(xAccData.get(i).getY()), String.valueOf(yAccData.get(i).getY()), String.valueOf(zAccData.get(i).getY()), String.valueOf(xGyrData.get(i).getY()), String.valueOf(yGyrData.get(i).getY()), String.valueOf(zGyrData.get(i).getY())};
            strList.add(str);
        }
        writer.writeAll(strList);
        try{
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        count++;
    }



    private class AccelerometerSubscriptionTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (getConnectedBandClient()) {
					appendToUI("Band is connected.\n", false);
					client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS128);
				} else {
					appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n", false);
				}
			} catch (BandException e) {
				String exceptionMessage="";
				switch (e.getErrorType()) {
				case UNSUPPORTED_SDK_VERSION_ERROR:
					exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
					break;
				case SERVICE_ERROR:
					exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
					break;
				default:
					exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
					break;
				}
				appendToUI(exceptionMessage, false);

			} catch (Exception e) {
				appendToUI(e.getMessage(), false);
			}
			return null;
		}
	}

	private class GyroscopeSubscriptionTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (getConnectedBandClient()) {
					appendToUI("Band is connected.\n",false);
					client.getSensorManager().registerGyroscopeEventListener(gyroscopeEventListener, SampleRate.MS128);
				} else {
					appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n",false);
				}
			} catch (BandException e) {
				String exceptionMessage="";
				switch (e.getErrorType()) {
					case UNSUPPORTED_SDK_VERSION_ERROR:
						exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
						break;
					case SERVICE_ERROR:
						exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
						break;
					default:
						exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
						break;
				}
				appendToUI(exceptionMessage, false);

			} catch (Exception e) {
				appendToUI(e.getMessage(), false);
			}
			return null;
		}
	}

    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }

	private void appendToUI(final String string, boolean isGyroscope) {
		if (!isGyroscope) {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					txtStatus.setText(string);
				}
			});
		} else {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					gyroscopeStatus.setText(string);
				}
			});
		}
	}

	private boolean getConnectedBandClient() throws InterruptedException, BandException {
		if (client == null) {
			BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
			if (devices.length == 0) {
				appendToUI("Band isn't paired with your phone.\n", false);
				return false;
			}
			client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
		} else if (ConnectionState.CONNECTED == client.getConnectionState()) {
			return true;
		}
		System.out.println("My state: " + client.getConnectionState().toString());
		appendToUI("Band is connecting...\n", false);
		return ConnectionState.CONNECTED == client.connect().await();
	}
}

