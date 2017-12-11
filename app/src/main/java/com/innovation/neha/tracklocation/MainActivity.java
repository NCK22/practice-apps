package com.innovation.neha.tracklocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.innovation.neha.tracklocation.Services.TrackLocService;


public class MainActivity extends AppCompatActivity {


    Button TrackLoc;
    LocationManager locationManager;
    LocationListener locationListener;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent=new Intent(MainActivity.this, TrackLocService.class);

        TrackLoc = (Button) findViewById(R.id.btn_show1);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                Log.e("Location ","changed");
                Toast.makeText(MainActivity.this, "Latitude"+String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Longitude " +location.getLongitude(), Toast.LENGTH_SHORT).show();
                Log.e("lattitude ",String.valueOf(location.getLatitude())+" Longitude"+String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

                Log.e("Status ","changed");

            }

            @Override
            public void onProviderEnabled(String s) {

                Log.e("Provider ","changed");
            }

            @Override
            public void onProviderDisabled(String s) {

                Log.e("Provider ","changed");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                },10);
            }
            return;
        }
        else
        {
            configureButton();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    configureButton();
        }
    }

    @SuppressLint("MissingPermission")
    private void configureButton() {

        Log.e("configureButton","called");
        TrackLoc.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {

               // locationManager.requestLocationUpdates("gps", 2000, 0, locationListener);


                stopService(intent);


            }

        });

       // Log.e("LastKnownLocation",String.valueOf(locationManager.getLastKnownLocation("gps").getLongitude()));
    }
}
