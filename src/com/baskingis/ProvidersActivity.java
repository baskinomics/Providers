package com.baskingis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Collects location provider information and stores it to the external SD.
 * @author Sean Baskin
 * @version 1.0.0
 *
 */
public class ProvidersActivity extends Activity implements OnClickListener {
	private TextView storageTV;
	//android.location classes
	private LocationManager locationManager;
	protected Location location;
	protected Bundle locationBundle;
	//Location attribute variables
	private static double lat;
	private static double lon;
	private float currentBearing;
	private double currentAltitude;
	private float currentSpeed;
	private long currentTime;
	private float currentAccuracy;
		
	//LocationManager parameters and attributes
	protected String provider;
	protected long updateInterval = 0;
	protected float minDistance = 0;
	private List<GPSLocations> gpsLocs = new ArrayList<GPSLocations>();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	long sysTime = System.currentTimeMillis();
	private Date sysDate = new Date(sysTime);
	//File Information
	private File locFile;
	private File root = android.os.Environment.getExternalStorageDirectory();	
	private File directory = new File(root.getAbsolutePath() + "/location_data");
	//Increment file names
	int k = 0;
	int j = 0;
	int i = 0;
	int observationCount = 0;
	int NUM_OBSERVATIONS_REQUIRED = 30; 
		
	// Define a listener that responds to location updates
	private LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	    	// Called when a new location is found
	    	getCurrentLocationInfo();
	    	// Add location fix to ArrayList
	    	gpsLocs.add(new GPSLocations(lat, lon, currentBearing, currentAltitude, currentSpeed, currentTime, currentAccuracy));
	    	observationCount++;
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	sysTime = System.currentTimeMillis();
	    	Date sysDate = new Date(sysTime);
			storageTV.setText("Obervation #" + (NUM_OBSERVATIONS_REQUIRED - observationCount) + " " + sdf.format(sysDate));
		}

	    public void onStatusChanged(String provider, int status, Bundle extras) {
	    	// Called when the status of the GPS satellites has changed.
	    }

	    public void onProviderEnabled(String provider) {
	    	// When location provider is enabled
	    }

	    public void onProviderDisabled(String provider) {
	    	// When location provider is disabled
	    	locationManager.removeUpdates(locationListener);
	    }
	  };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Add Click listeners for all buttons
        View gpsButton = findViewById(R.id.gps);
        gpsButton.setOnClickListener(this);
        View cellButton = findViewById(R.id.cell);
        cellButton.setOnClickListener(this);
        View wifiButton = findViewById(R.id.wifi);
        wifiButton.setOnClickListener(this);
        View startButton = findViewById(R.id.start);
        startButton.setOnClickListener(this);
        View stopButton = findViewById(R.id.stop);
        stopButton.setOnClickListener(this);
        View saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(this);
        
        // Create a LocationManager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// Listener for GPS Status...	
	    final GpsStatus.Listener onGpsStatusChange = new GpsStatus.Listener(){
	        public void onGpsStatusChanged(int event){
	            switch(event){
	                case GpsStatus.GPS_EVENT_STARTED:
	                        // Started...
                        Toast.makeText(ProvidersActivity.this, "GPS has started!", Toast.LENGTH_LONG).show();
	                break;
	                case GpsStatus.GPS_EVENT_FIRST_FIX:
	                        // First Fix...
	                        Toast.makeText(ProvidersActivity.this, "GPS has 1st fix", Toast.LENGTH_LONG).show();
	                break;
	                case GpsStatus.GPS_EVENT_STOPPED:
	                        // Stopped...
	                break;
	                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
	                        // Satellite update
	                break;
	            }
	        }
	    };
		
	    locationManager.addGpsStatusListener(onGpsStatusChange);
        storageTV = (TextView) findViewById(R.id.storageTV);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
		//TODO
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	//TODO
    }
    
    /**
     * Retrieves most recent location information.
     */
    public void getCurrentLocationInfo() {
    	// Request last known location
    	location = locationManager.getLastKnownLocation(provider);
    	if(location != null) {
    		lat = location.getLatitude();
    		lon = location.getLongitude();
    		currentTime = location.getTime();    		
			if (location.hasBearing()) {
				currentBearing = location.getBearing();
			}
			if (location.hasAltitude()) {
	    		currentAltitude = location.getAltitude();
			}
			if (location.hasSpeed()) {
	    		currentSpeed = location.getSpeed();
			}
			if (location.hasAccuracy()) {
	    		currentAccuracy = location.getAccuracy();
			}
    	}
    }
    
    /** 
     * Checks and displays the external media status.
     */
    private void checkExternalMedia() {
    	boolean mExternalStorageAvailable = false;
    	boolean mExternalStorageWritable = false;
    	String storageState = Environment.getExternalStorageState();
    	
    	if (Environment.MEDIA_MOUNTED.equals(storageState)) {
    		// Can read and write media
    		mExternalStorageAvailable = true;
    		mExternalStorageWritable = true;
    	}
    	
    	else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)) {
    		mExternalStorageAvailable = true;
    		mExternalStorageWritable = false;
    	}
    	
    	else {
    		mExternalStorageAvailable = false;
    		mExternalStorageWritable = true;
    	}
    	
    	storageTV.append("\n\n" + "External Media: readable= " + 
    						mExternalStorageAvailable + 
    						" writable: " +
    						mExternalStorageWritable);
		storageTV.append("\n\n" + "Obervation #" + (NUM_OBSERVATIONS_REQUIRED - observationCount) + " " + sdf.format(sysDate));
    }
    
    /** 
     * Writes the location data to a text file on the SD card. 
     */
    private void writeLocInfoToSDFile() {
    	 	
    	try {
    		FileOutputStream fOS = new FileOutputStream(locFile);
    		PrintWriter printWriter = new PrintWriter(fOS);
    		directory.mkdirs();
    		//For CSV format
    		printWriter.println("Latitude,Longitude,Altitude,Bearing,Speed,Time,Accuracy" + "\r");
    		for (GPSLocations gpslocation : gpsLocs) {
				String printLat = Double.toString(gpslocation.getLat());
				String printLon = Double.toString(gpslocation.getLon());
				//GPS Timestamp
				long gpsTime = gpslocation.getTime();
				Date gpsDate = new Date(gpsTime);
				String gpsPrintTime = sdf.format(gpsDate);
				//System Timestamp
				Date sysDate = new Date(sysTime);
				String sysPrintTime = sdf.format(sysDate);
				String printBearing = Float.toString(gpslocation.getBearing());
				String printAlt = Double.toString(gpslocation.getAltitude());
				String printSpeed = Float.toString(gpslocation.getSpeed());
			    String printAccuracy = Float.toString(gpslocation.getAccuracy());
				//Print variables
				printWriter.println(printLat + "," + printLon + "," + printBearing + "," + printAlt + "," + printSpeed + "," + gpsPrintTime + "," + sysPrintTime + "," + printAccuracy + "\r");
			}
    		printWriter.flush();
    		printWriter.close();
    		fOS.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    		Log.i("Media", "** File not found. Was a WRITE_EXTERNAL_STORAGE permission added to the AndroidManifest.xml? **");
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	storageTV.append("\n\n" + "File written to " + locFile);
    }
    
    /**
     * Sets listener for button clicks.
     * @param v
     */
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.gps:
                Log.i("Button","GPS button pushed");
                provider = LocationManager.GPS_PROVIDER;
                locFile = new File(directory, "gpsData_" + k + ".txt");
                k++;
                checkExternalMedia();
            break;
            
            case R.id.cell:
                Log.i("Button","Cell button pushed");
                provider = LocationManager.NETWORK_PROVIDER;
                locFile = new File(directory, "cellData_" + i + ".txt");
                i++;
                checkExternalMedia();
            break;
            
            case R.id.wifi:
                Log.i("Button","WiFi button pushed");
                provider = LocationManager.NETWORK_PROVIDER;
                locFile = new File(directory, "wifiData_" + j + ".txt");
                j++;
                checkExternalMedia();
            break;
            
            case R.id.start:
                Log.i("Button","Start button pushed");
                locationManager.requestLocationUpdates(provider, updateInterval, minDistance, locationListener);
                getCurrentLocationInfo();
                gpsLocs.add(new GPSLocations(lat, lon, currentBearing, currentAltitude, currentSpeed, currentTime, currentAccuracy));
            break;	
            
            case R.id.stop:
                Log.i("Button","Stop button pushed");
                locationManager.removeUpdates(locationListener);
            break;	
            
            case R.id.save:
                Log.i("Button","Save button pushed");
                writeLocInfoToSDFile();
            break;	
        }	
    }
}