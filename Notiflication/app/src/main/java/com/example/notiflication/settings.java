package com.example.notiflication;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;

import java.util.Objects;

public class settings extends PreferenceFragmentCompat {


    private static final int CALLBACK_CODE = 0;

    private Context contextOfMain;

    private MainActivity mainActivity ;

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private int RG1 = 0;
    private int RG2 = 4;
    private int RG3 = 0;
    private int RG4 = 1;

    private boolean isMosqueSilenceActivated = false;


    public settings(Context c ){
        contextOfMain = c;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefrences, rootKey);


        mainActivity = new MainActivity();
        // Listener for all the groups
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("juristic")) {
                    ListPreference juristicPref = findPreference(key);
                    if (juristicPref != null) {
                        RG1 = Integer.valueOf(juristicPref.getValue());
                        Toast.makeText(getContext(), "Juristic method updated", Toast.LENGTH_SHORT).show();
                    }
                }

                if (key.equals("calculation")) {
                    ListPreference calculatePref = (ListPreference) findPreference(key);
                    if(calculatePref != null) {
                        RG2 = Integer.valueOf((calculatePref.getValue()));

                        Toast.makeText(getActivity(), "Calculation convention updated", Toast.LENGTH_SHORT).show();
                    }
                }
                if (key.equals("latitude")) {
                    ListPreference latitudePref = (ListPreference) findPreference(key);
                    if(latitudePref !=null) {
                        RG3 = Integer.valueOf((latitudePref.getValue()));

                        Toast.makeText(getActivity(), "Latitude adjustment updated", Toast.LENGTH_SHORT).show();
                    }
                }
                if (key.equals("time")) {
                    ListPreference timePref = (ListPreference) findPreference(key);
                    if(timePref != null){
                        RG4 = Integer.valueOf((timePref.getValue()));

                        Toast.makeText(getActivity(), "Time format updated", Toast.LENGTH_SHORT).show();
                    }
                }
                if (key.equals("selected_prayer")) { // Use the key "selected_prayer" as per your ListPreference key
                    // Get the selected prayer time
                    String selectedPrayer = sharedPreferences.getString("selected_prayer", "None");

                    // Check if the selected prayer is not "None"

                    // Handle enabling silent mode based on selected prayer time
                    mainActivity.handlePrayerTimeSilence(selectedPrayer, getContext());                    }
                if(key.equals("mosqueSilence")){
                    // Get SwitchPreference instance
                    SwitchPreference mosqueSilenceSwitch = findPreference("mosqueSilence");

                    if (mosqueSilenceSwitch != null) {
                        // Check if the switch is checked
                        boolean isMosqueSilenceEnabled = mosqueSilenceSwitch.isChecked();
                        if (isMosqueSilenceEnabled) {
                            isMosqueSilenceActivated = true ;
                            Toast.makeText(getContext(), "Silent mode is on for mosque", Toast.LENGTH_SHORT).show();
                            checkLocation();
                        } else {
                            isMosqueSilenceActivated = false ;
                            Toast.makeText(getContext(), "Silent mode if off for mosque", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };
        Preference resetLocationPreference = findPreference("reset_location_preference");

        if (resetLocationPreference != null) {
            resetLocationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    // Logging to check if the click listener is triggered
                    Log.d("SettingsFragment", "Reset location preference clicked");

                    while (!updateLocation()) {
                        Toast.makeText(getContext(), "Location updated successfully", Toast.LENGTH_SHORT).show();
                        updateLocation();
                        return true;
                    }
                    return false; // Return true to indicate the event was handled
                }
            });
        }
    }

    private boolean updateLocation() {
        Location location = MainActivity.getLocation(getContext());
        if (location != null) {
            String summary = "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude();
            Preference locationPreference = findPreference("reset_location_preference");
            if (locationPreference != null) {
                locationPreference.setSummary(summary);
            }
            return true;
        }

        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALLBACK_CODE) {
            Toast.makeText(getContext(), "Access granted!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLocationChanged(@NonNull Location location) {
        // do this method if preference was enabled
        checkLocation();
    }
    public void checkLocation() {
        // do this method if preference was enabled
        Location location = MainActivity.getLocation(getContext());

        if(isMosqueSilenceActivated && location != null ) {
            //  Detect Mosque Entry
            for (Mosques mosqueLocation : MainActivity.mosqueLocations) {
                float[] distance = new float[1];//
                Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                        mosqueLocation.latitude, mosqueLocation.longitude, distance);
                // Check if the distance is within a  radius 100 meters
                if (distance[0] < 10000) {
                    // Step 4: Activate Silent Mode
                    AudioManager audioManager = (AudioManager) contextOfMain.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    /// Cancel the notification by its ID
                    Toast.makeText(getContext(), "testy", Toast.LENGTH_SHORT).show();
                     NotificationManager notificationManager = (NotificationManager) contextOfMain.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                    break; // Stop checking other mosque locations
                }
            }
        }
    }

    public int getRG1() {
        return RG1;
    }

    public int getRG2() {
        return RG2;
    }

    public int getRG3() {
        return RG3;
    }

    public int getRG4() {
        return RG4;
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
}
