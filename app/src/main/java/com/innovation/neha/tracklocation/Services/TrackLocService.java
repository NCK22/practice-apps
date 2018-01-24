package com.innovation.neha.tracklocation.Services;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class TrackLocService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    Button track;
    TextView tlat, tlng, tme;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 2;
    public static  boolean permissionresult=false;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String mLastUpdateTime;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    GoogleApiClient mGoogleApiClient;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;

    GoogleApiClient googleApiClient;

    // Tag used to cancel the request
    String tag_string_req = "string_req";

    String url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/SendLocation.php";
    public static String vid="";
    Context context,ctxperm;
    public static TrackLocService instance=null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance=this;
        context=this;
        if (!isGooglePlayServicesAvailable()) {
            //finish();
        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

       /* tlat=(TextView)findViewById(R.id.tv_lat);
        tlng=(TextView)findViewById(R.id.tv_lng);
        tme=(TextView)findViewById(R.id.tv_timeg);
        track = (Button) findViewById(R.id.btn_track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI();
            }
        });*/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
         /*mFusedLocationClient.getLastLocation().addOnSuccessListener((Executor) mGoogleApiClient, new OnSuccessListener<Location>() {
             @Override
             public void onSuccess(Location location) {

                 if (location != null) {

                    // Toast.makeText(FusedActivity.this, "Longitude:" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                     //Toast.makeText(FusedActivity.this, "Lattitude:" + location.getLatitude(), Toast.LENGTH_SHORT).show();
                     // Logic to handle location object
                 }
             }
         });*/
        }


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }
            }
        };

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        instance=null;
        Log.e("Service","Destroyed");
    }
    /*@Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }

    }*/

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
          //  GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
           Log.e("GMS Error",GooglePlayServicesUtil.GMS_ERROR_DIALOG);
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("onConnected-isConnected", String.valueOf(mGoogleApiClient.isConnected()));
        startLocationUpdates();
    }

    private void startLocationUpdates() {

        Log.e("inside","startlocationupdate");
       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           Log.e("permission","not granted");
           permissionresult=false;
              return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
            Log.d(TAG, "Location update started ..............: ");


    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.e("isRunning", String.valueOf(NewVisitActivity.isRunning));
        if(NewVisitActivity.isRunning==true)
            sendLocationString();
        else if(NewVisitActivity.isRunning==false)
            stopLocationUpdates();
       // getLocationString();
       //getLocatonObject();
      //  updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            // Toast.makeText(this, "Lattitude:"+lat, Toast.LENGTH_SHORT).show();
            // Toast.makeText(this, "Longitude:"+lng, Toast.LENGTH_SHORT).show();

           // tlat.setText("Lattitide :  "+String.valueOf(mCurrentLocation.getLatitude()));
            //tlng.setText("Longitude :  "+String.valueOf(mCurrentLocation.getLongitude()));
            //tme.setText("Updated at :  "+mLastUpdateTime);

           /* Log.e("Lattitude",String.valueOf(mCurrentLocation.getLatitude()));
            Log.e("Longitude",String.valueOf(mCurrentLocation.getLongitude()));
            Log.e("Time",String.valueOf(mLastUpdateTime));
*/

        } else {
            Log.e(TAG, "location is null ...............");
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    public void sendLocationString() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
//        pDialog.show();

        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Response",String.valueOf(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("ErrorResponse",String.valueOf(error));
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("v_id", vid);
                params.put("v_lat", String.valueOf(mCurrentLocation.getLatitude()));
                params.put("v_long", String.valueOf(mCurrentLocation.getLongitude()));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }

    public void getLocationString() {

        Log.e("getLocationString","called");
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray=null;
                Log.e("Response", response.toString());
                try {
                    jsonArray=new JSONArray(response);
                    Log.e("Json",String.valueOf(jsonArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    Log.e("Json",String.valueOf(jsonArray));
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.e("ResponseError", error.toString());

            }
        }){

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void getLocatonObject(){
        Log.e("getLocationObject","called");
        String url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/SendLocation.php?p_id=49";


        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response",String.valueOf(response));
                      //  Toast.makeText(getBaseContext(),"Response:"+response,Toast.LENGTH_SHORT).show();
                        // the response is already constructed as a JSONObject!
                        try {
                            //Log.e("Response",String.valueOf(response));
                    String element=String.valueOf( response.getJSONArray("w1"));
                            Log.e("Response",String.valueOf( response.getJSONArray("w1")));
                       //    Toast.makeText(getBaseContext(),"Element:"+element,Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("ResponseCatch",String.valueOf(e));
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("ResponseError",String.valueOf(error));
                    }
                });

        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Log.e("Service", "onStart fired ....");
      vid= intent.getStringExtra("id");
        mGoogleApiClient.connect();
        return START_NOT_STICKY;

    }

   }
