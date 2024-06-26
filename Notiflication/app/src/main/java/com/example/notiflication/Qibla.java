package com.example.notiflication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class Qibla extends AppCompatActivity implements SensorEventListener, LocationListener {

    private ImageView compassImageView;
    private ImageView needleImageView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private LocationManager locationManager;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qibla);;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get references to image views
        compassImageView = findViewById(R.id.compassImageView);
        needleImageView = findViewById(R.id.needleImageView);

        // Initialize sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;

        // Initialize location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert locationManager != null;

        // Check and request location permissions if needed
        checkLocationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-register sensor listeners when the activity is resumed
        registerSensorListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listeners when the activity is paused to save battery
        unregisterSensorListeners();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Handle changes in sensor data
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }

        // Compute device orientation if accelerometer and magnetometer data are available
        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);

            // Convert radians to degrees for azimuth (rotation around the z-axis)
            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);

            // Calculate the Qibla direction based on the current location
            float qiblaDirection = calculateQiblaDirection();

            // Adjust the azimuth angle to point towards the Qibla accurately
            float adjustedAzimuth = azimuthInDegrees - qiblaDirection;

            // Rotate the needle image view based on the adjusted azimuth angle
            needleImageView.setRotation(-adjustedAzimuth);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle changes in sensor accuracy (not used in this example)
    }
    private float getAdjustedAzimuth(float qiblaDirection) {
        // Convert radians to degrees for azimuth (rotation around the z-axis)
        float azimuthInRadians = orientation[0];
        float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);

        // Adjust the azimuth angle to point towards the Qibla accurately
        return azimuthInDegrees - qiblaDirection;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Location changed, recalculate Qibla direction
        float qiblaDirection = calculateQiblaDirection();
        float adjustedAzimuth = getAdjustedAzimuth(qiblaDirection);
        needleImageView.setRotation(-adjustedAzimuth);
    }

    private void registerSensorListeners() {
        // Register sensor listeners for accelerometer and magnetometer
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensorListeners() {
        // Unregister sensor listeners to conserve battery
        sensorManager.unregisterListener(this);
    }

    private void checkLocationPermission() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, start registering sensor listeners
            registerSensorListeners();
            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    private float calculateQiblaDirection() {
        // Get the current location
        Location location = null;
        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            // Handle potential SecurityException
            e.printStackTrace();
        }

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            // Calculate Qibla direction based on the current location and coordinates of the Kaaba
            double phiK = 21.4 * Math.PI / 180.0;
            double lambdaK = 39.8 * Math.PI / 180.0;
            double phi = latitude * Math.PI / 180.0;
            double lambda = longitude * Math.PI / 180.0;
            double psi = 180.0 / Math.PI * Math.atan2(Math.sin(lambdaK - lambda), Math.cos(phi) * Math.tan(phiK) - Math.sin(phi) * Math.cos(lambdaK - lambda));
            return (float) psi;
        } else {
            // Location is not available, handle it accordingly
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            return 0; // or return a default value
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start registering sensor listeners
                registerSensorListeners();
                // Request location updates
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                } catch (SecurityException e) {
                    // Handle potential SecurityException
                    e.printStackTrace();
                }
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onProviderEnabled(String provider) {
        // Check if location permissions are granted before requesting location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Location provider enabled, request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }


    @Override
    public void onProviderDisabled(String provider) {
        // Location provider disabled, unregister sensor listeners
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Handle changes in location provider status
    }
    // Handle back navigation when the back button in the toolbar is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
