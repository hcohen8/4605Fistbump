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


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.SampleRate;



import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.view.View;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;



import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;


public class BandAccelerometerAppActivity extends Activity {

	private BandClient client = null;
	private Button btnStart;
	private TextView txtStatus;
	private TextView gyroscopeStatus;
	private GraphView graph;
	private GraphView graph2;
    private ArrayList<DataPoint> aPoints;
    private ArrayList<DataPoint> gPoints;
	private LineGraphSeries<DataPoint> seriesX1, seriesY1, seriesZ1, seriesX2, seriesY2, seriesZ2;
	private double y,x;
	private boolean isRecording = false;
	private int count = 0;
	long startTime;

	
	private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
            if (event != null) {

				count++;

				updateGraph1(event.getTimestamp(), event.getAccelerationX(), event.getAccelerationY(), event.getAccelerationZ());
//            	appendToUI(String.format(" X = %.3f \n Y = %.3f\n Z = %.3f", event.getAccelerationX(),
//            			event.getAccelerationY(), event.getAccelerationZ()), false);

            }
        }
    };

    private BandGyroscopeEventListener gyroscopeEventListener = new BandGyroscopeEventListener() {
		@Override
		public void onBandGyroscopeChanged(BandGyroscopeEvent bandGyroscopeEvent) {

			updateGraph2(bandGyroscopeEvent.getTimestamp(), bandGyroscopeEvent.getAngularVelocityX(), bandGyroscopeEvent.getAngularVelocityY(), bandGyroscopeEvent.getAngularVelocityZ());
//			appendToUI(String.format(" X = %.3f \n Y = %.3f\n Z = %.3f", bandGyroscopeEvent.getAngularVelocityX(),
//					bandGyroscopeEvent.getAngularVelocityY(), bandGyroscopeEvent.getAngularVelocityZ()), true);

		}

	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


		x = 0.0;
		y = 0.0;
        aPoints = new ArrayList<>();
        gPoints = new ArrayList<>();
		gyroscopeStatus = (TextView) findViewById(R.id.gyroscopeStats);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        btnStart = (Button) findViewById(R.id.btnStart);
		graph = (GraphView) findViewById(R.id.graph1);
		graph2 = (GraphView) findViewById(R.id.graph2);
		seriesX1 = new LineGraphSeries<>();
		seriesY1 = new LineGraphSeries<>();
		seriesZ1 = new LineGraphSeries<>();
		seriesX1.setColor(Color.BLUE);
		seriesY1.setColor(Color.GREEN);
		seriesZ1.setColor(Color.MAGENTA);
		graph.addSeries(seriesX1);
		graph.addSeries(seriesY1);
		graph.addSeries(seriesZ1);

		seriesX2 = new LineGraphSeries<>();
		seriesY2 = new LineGraphSeries<>();
		seriesZ2 = new LineGraphSeries<>();
		seriesX2.setColor(Color.BLUE);
		seriesY2.setColor(Color.GREEN);
		seriesZ2.setColor(Color.MAGENTA);
		graph2.addSeries(seriesX2);
		graph2.addSeries(seriesY2);
		graph2.addSeries(seriesZ2);

        btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txtStatus.setText("");

				if (!isRecording) {
					btnStart.setText("Stop");
					new GyroscopeSubscriptionTask().execute();
					new AccelerometerSubscriptionTask().execute();
					isRecording = true;
				} else {
					btnStart.setText("Start");
					try {
						client.getSensorManager().unregisterAccelerometerEventListener(mAccelerometerEventListener);
						client.getSensorManager().unregisterGyroscopeEventListener(gyroscopeEventListener);
					} catch (BandIOException e) {
						appendToUI(e.getMessage(), false);
					}
                    isRecording = false;
				}


			}
		});




//		for(int i =0; i<100; i++) {
//
//			y = Math.sin(x);
//			series.appendData(new DataPoint(x, y), true, 100);
//		}

    }
    
    @Override
	protected void onResume() {
		super.onResume();
		txtStatus.setText("");
	}
	
    @Override
	protected void onPause() {
		super.onPause();
		if (client != null) {
			try {
				client.getSensorManager().unregisterAccelerometerEventListener(mAccelerometerEventListener);
				client.getSensorManager().unregisterGyroscopeEventListener(gyroscopeEventListener);
			} catch (BandIOException e) {
				appendToUI(e.getMessage(), false);
			}
		}
	}


	private void updateGraph1 (final long timestamp, final float x, final float y, final float z)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				seriesX1.appendData(new DataPoint(timestamp, x), true, 25);
				seriesY1.appendData(new DataPoint(timestamp, y), true, 25);
				seriesZ1.appendData(new DataPoint(timestamp, z), true, 25);
                aPoints.add(new DataPoint(timestamp, x));
				graph.removeAllSeries();
				graph.addSeries(seriesX1);
				graph.addSeries(seriesY1);
				graph.addSeries(seriesZ1);
			}
		});
	}

	private void updateGraph2 (final long timestamp, final float x, final float y, final float z)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				seriesX2.appendData(new DataPoint(timestamp, x), true, 25);
				seriesY2.appendData(new DataPoint(timestamp, y), true, 25);
				seriesZ2.appendData(new DataPoint(timestamp, z), true, 25);
                gPoints.add(new DataPoint(timestamp, x));
				graph2.removeAllSeries();
				graph2.addSeries(seriesX2);
				graph2.addSeries(seriesY2);
				graph2.addSeries(seriesZ2);
			}
		});
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

