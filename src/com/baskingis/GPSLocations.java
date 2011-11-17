package com.baskingis;

public class GPSLocations {
	double lat;
	double lon;
	float currentBearing;
	double currentAltitude;
	float currentSpeed;
	long currentTime;
	float currentAccuracy;
	
	public GPSLocations(double lat, double lon, float currentBearing, double currentAltitude, float currentSpeed, long currentTime, float currentAccuracy) {
		this.lat = lat;
		this.lon = lon;
		this.currentBearing = currentBearing;
		this.currentAltitude = currentAltitude;
		this.currentSpeed = currentSpeed;
		this.currentTime = currentTime;
		this.currentAccuracy = currentAccuracy;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	public float getBearing() {
		return currentBearing;
	}

	public double getAltitude() {
		return currentAltitude;
	}
	
	public float getSpeed() {
		return currentSpeed;
	}
	
	public long getTime() {
		return currentTime;
	}
	
	public float getAccuracy() {
		return currentAccuracy;
	}
}
