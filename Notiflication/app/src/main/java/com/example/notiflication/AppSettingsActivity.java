package com.example.notiflication;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class AppSettingsActivity extends AppCompatActivity {
    public static final String PREF_SILENT = "silent";
    LocationManager locationManager;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, new settings(this))
                    .commit();


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            // Sets the Toolbar to act as the ActionBar for this Activity window.
            // Make sure the toolbar exists in the activity and is not null
            Intent returnIntent = new Intent();
            setResult(0, returnIntent);
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }




}