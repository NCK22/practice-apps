package com.innovation.neha.tracklocation.Services;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.innovation.neha.tracklocation.Activities.FrontActivity;
import com.innovation.neha.tracklocation.Activities.NewVisitActivity;
import com.innovation.neha.tracklocation.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Neha on 27-11-2017.
 */

public class SendBroadcastService extends Service{

    private boolean gps_enabled = false;
    Context context;
    LocationManager lm;
    Handler handler;
    Intent in;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Service","created");
        context=this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        Log.e("Service","onStartCommand");
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("Inside","run");

                lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!gps_enabled) {

                    Log.e("gps","disabled");
                  //  Log.e("builder",String.valueOf(FrontActivity.builderFlag));
                    //if(FrontActivity.builderFlag==false) {
                        Log.e("inside","inner if");
                        in = new Intent("LocCheckService");
                        sendBroadcast(in);
                  //  }

                }

            }
        },5);



        return START_STICKY;
    }
}