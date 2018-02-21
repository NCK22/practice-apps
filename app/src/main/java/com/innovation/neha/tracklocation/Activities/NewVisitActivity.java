package com.innovation.neha.tracklocation.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.innovation.neha.tracklocation.AppController;
import com.innovation.neha.tracklocation.R;
import com.innovation.neha.tracklocation.Services.TrackLocService;
import com.innovation.neha.tracklocation.Storage.SPrefUserInfo;
//import com.innovation.neha.tracklocation.Storage.SPrefVisitInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewVisitActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private EditText visit_name, visit_loc;
    private TextView dialog_result, visit_id, date, time;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private LinearLayout date_time_layout;
    private List<String> list_loc = new ArrayList<String>();
    ProgressDialog progressDialog;
    public static int v_id;
    public String tag_string_req = "stringrequest",dateforreq,timeforreq;
    View customView;
    Button verify, submit;
    PopupWindow popupWindow;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    Fragment place_autocomplete_fragment;
    public static  Intent Servintent;
    public static boolean isRunning=false;
    private boolean gps_enabled = false;
   // private boolean network_enabled = false;
    ConnectivityManager connMgr;
    android.net.NetworkInfo network_enabled;
    private SPrefUserInfo sPrefUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        visit_name = (EditText) findViewById(R.id.et_vName);
        visit_loc = (EditText) findViewById(R.id.et_loc);
        visit_id = (TextView) findViewById(R.id.text_visit_id);
        date = (TextView) findViewById(R.id.text_date);
        time = (TextView) findViewById(R.id.text_time);
        date_time_layout = (LinearLayout) findViewById(R.id.date_time_layout);
        submit = (Button) findViewById(R.id.btn_vsubmit);

        sPrefUserInfo=new SPrefUserInfo(NewVisitActivity.this);

        submit.setOnClickListener(this);
        date_time_layout.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

       connMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        network_enabled=connMgr.getActiveNetworkInfo();

      //  network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.e("network", String.valueOf(network_enabled));
        if(network_enabled!=null) {
            Log.e("network", String.valueOf(network_enabled));

            if (network_enabled.isConnected()) {
                Log.e("network", "if");
                getCurrentVisit();
            } else {
                Log.e("network", "else");
                isNetworkEnabled();
                // getCurrentVisit();
            }
        }
        else
            isNetworkEnabled();



     //   Log.e("visit",SplashActivity.sPrefUserInfo.getVisitInfo().toString());


     /*   if(SplashActivity.sPrefUserInfo.getVisitInfo().equals("")||
                SplashActivity.sPrefUserInfo.getVisitInfo().equals("no"))
        {
            submit.setText("START");
        }
        else
        {
          //  getCurrentVisit();


        }*/

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        customView = inflater.inflate(R.layout.activity_dialog, null);

        /*dialog_result = (TextView) dialog.findViewById(R.id.tv_dialog);
        dialog_image=(ImageView)dialog.findViewById(R.id.img_dialog);*/

        dialog_result = (TextView) customView.findViewById(R.id.tv_dialog);
        //  dialog_image=(ImageView)customView.findViewById(R.id.img_dialog);
        verify = (Button) customView.findViewById(R.id.btn_dialog);
        verify.setOnClickListener(this);



       /* mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this,this)
                .build();

        selectLocation();*/

        PlaceAutocompleteFragment places = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setHint("Search Location");
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

              //  Toast.makeText(getApplicationContext(), place.getName(), Toast.LENGTH_SHORT).show();
               // Toast.makeText(getApplicationContext(), place.getLatLng().toString(), Toast.LENGTH_SHORT).show();

                if (visit_loc.getText().length() == 0)
                    visit_loc.setText(place.getName());
                else
                    visit_loc.append("," + place.getName());

            }

            @Override
            public void onError(Status status) {

               // Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();

            }
        });

         Servintent=new Intent(this,TrackLocService.class);

       // isLocationEnabled();
      //  isNetworkEnabled();
    }

    /*  @Override
      protected void onStart() {
          super.onStart();
          mGoogleApiClient.connect();
      }

      @Override
      protected void onStop() {
          mGoogleApiClient.disconnect();
          super.onStop();
      }
  */
    public void selectLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(NewVisitActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void getCurrentVisit()
    {
        Log.e("Inside","getcurrentvisit");
        progressDialog.setMessage("Loading..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
      //  String url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/getCurrentVisit.php?u_id="+SplashActivity.sPrefUserInfo.getUserInfo();
        String url = "http://www.thinkbank.co.in/Rajeshahi_app/getCurrentVisit.php?u_id="+/*SplashActivity.*/sPrefUserInfo.getUserInfo();
         Log.e("URL",url);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response",String.valueOf(response));
                        // Toast.makeText(getBaseContext(),"Response:"+response,Toast.LENGTH_SHORT).show();
                        // the response is already constructed as a JSONObject!


                        try {

                            if(response.equals("[]"))
                            {
                                generateId();
                            }
                            else
                            {
                                visit_id.setText(response.getString("w1"));
                                visit_name.setText(response.getString("w2"));
                                visit_loc.setText(response.getString("w3"));

                                visit_name.setEnabled(false);

                                Log.e("In getCurrentVisit",response.getString("w1"));
                                submit.setText("STOP");
                                progressDialog.dismiss();
                            }

                            // Log.e("id",response.getString("w1"));
                            //Log.e("name",response.getString("w2"));

                           /* for(int i = 0; i<response.names().length(); i++){

                                String key=response.names().getString(i);
                                String value= String.valueOf(response.get(response.names().getString(i)));*/


                              //  value=value.replaceAll("[\\[,\\],\",]","");
                               //  Log.e("ResponseNameLoop", "key = " + key + " value = " + value);
                                //Log.e("ResponseListElement",list_prod.get(0));
                          //  }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("ResponseCatch",String.valueOf(e));
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        generateId();
                        Log.e("ResponseError",String.valueOf(error));
                    }
                });

        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.btn_dialog:popupWindow.dismiss();
            if(dialog_result.getText().equals("Journey Started!")||
                    dialog_result.getText().equals("Journey Stopped!"))
                    finish();
            break;


            case R.id.date_time_layout:

                datePicker();
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                time.setText(convertTimeFormat(mHour + ":" + mMinute));
                timeforreq = mHour + ":" + mMinute + ":00";
                //timePicker();
                //datePicker();
                break;

            case R.id.btn_vsubmit:


                if (ActivityCompat.checkSelfPermission(NewVisitActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(NewVisitActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {



                Log.e("submit text",submit.getText().toString());
                if (submit.getText().equals("START")) {
                    Log.e("start", "start");

                    if (!gps_enabled)
                        isLocationEnabled();
                    else
                        startVisit();

                }
                else if (submit.getText().equals("STOP")){
                    Log.e("stop","stop");
                    if(!gps_enabled )
                        isLocationEnabled();
                    else
                    stopVisit();
                }

                Log.e("id", visit_id.getText().toString());
                Log.e("name", visit_name.getText().toString());

                Log.e("date", date.getText().toString());
                Log.e("time", time.getText().toString());

                }
                else
                    ActivityCompat.requestPermissions(NewVisitActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                            200);

                break;
        }
    }

    private void datePicker() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);


                       dateforreq=year+"-"+(monthOfYear + 1)+"-"+dayOfMonth;


                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }

    private void timePicker() {

       final Calendar datetime = Calendar.getInstance();
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {


                         Calendar c = Calendar.getInstance();
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);
                        Log.e("time", "time 1 " + hourOfDay + ":" + minute);
                        Log.e("datetime", String.valueOf(datetime.getTimeInMillis()));
                        Log.e("c", String.valueOf(c.getTimeInMillis()));


                        if (c.getTimeInMillis() >= datetime.getTimeInMillis()) {

                            time.setText(convertTimeFormat(hourOfDay + ":" + minute));

                            timeforreq = hourOfDay + ":" + minute + ":00";
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Select Valid Time",Toast.LENGTH_SHORT).show();
                    }
                }, mHour, mMinute, false);


        timePickerDialog.show();
    }

    private String convertTimeFormat(String time) {
        try {
            DateFormat f1 = new SimpleDateFormat("HH:mm"); //HH for hour of the day (0 - 23)
            Date d = f1.parse(time);
            DateFormat f2 = new SimpleDateFormat("h:mm a");
            String ftime = f2.format(d);
            Log.e("time", "ftime 2 " + ftime);

            return ftime;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void generateId() {
      //  progressDialog.setMessage("Loading..");
      //  progressDialog.show();
        String url = "http://www.thinkbank.co.in/Rajeshahi_app/fetchVisitId.php";
        // Log.e("URL",url);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Log.e("ResponsePrice", response.toString());
                try {

                    v_id = Integer.parseInt(response);
                    visit_id.setText(String.valueOf(v_id));
                    Log.e("In Generateid",String.valueOf(v_id));
                    submit.setText("START");
                    sPrefUserInfo.setVisitId(response);
                    progressDialog.dismiss();

                    //Toast.makeText(getApplicationContext(), "iprice:"+String.valueOf(iprice), Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                Log.e("ResponseError", error.toString());

            }
        }) {

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void startVisit() {
        Log.e("postAllData", "called");


        if (visit_id.getText().toString().equals("") ||
                visit_name.getText().toString().equals("") ||
                visit_loc.getText().toString().equals("") ||
                date.getText().toString().equals("") ||
                time.getText().toString().equals("")) {
            Log.e("Empy", "empty");
            dialog_result.setText("please fill all fields! ");
            verify.setText("OK");

            popupWindow = new PopupWindow(
                    customView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            //  popupWindow.setAnimationStyle(R.style.animationdialog);
            // popupWindow.setElevation(20.5f);
            //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
            popupWindow.setOutsideTouchable(true);
             popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            String url = "http://www.thinkbank.co.in/Rajeshahi_app/postVisitData.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("Response", String.valueOf(response));

                            progressDialog.dismiss();
                            dialog_result.setText("Journey Started!");
                            verify.setText("OK");
                            isRunning=true;
                            sPrefUserInfo.setVisitInfo("yes");
                            popupWindow = new PopupWindow(
                                    customView,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            );
                            //  popupWindow.setAnimationStyle(R.style.animationdialog);
                            // popupWindow.setElevation(20.5f);
                            //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                            popupWindow.setOutsideTouchable(false);
                            // if(getCurrentFocus().getParent()!=null)
                            //    ((ViewGroup)getCurrentFocus().getParent()).removeView(getCurrentFocus());

                            popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);

                            Servintent.putExtra("id",visit_id.getText().toString());
                            Servintent.putExtra("time",dateforreq+" "+timeforreq);
                            startService(Servintent);
                            date.setText("");
                            time.setText("");
                            submit.setText("STOP");



                            // revokeUriPermissionfileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("ErrorResponse", String.valueOf(error));
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("v_id", visit_id.getText().toString());
                    params.put("u_id",/*SplashActivity.*/sPrefUserInfo.getUserInfo());
                    params.put("v_name", visit_name.getText().toString());
                    params.put("v_loc", visit_loc.getText().toString());
                    params.put("v_time", dateforreq+" "+timeforreq);
                    params.put("flag","start");

                  //  Log.e("Today is " , String.valueOf(date.getTime()));

                    Log.e("Date",  dateforreq+ "   "+timeforreq);
                  /*  Log.e("ordermain", String.valueOf(payObject));
                    Log.e("imagepath", ConvertImage);*/
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

        }

       // sPrefVisitInfo.setVisitInfo("yes");
     //   SplashActivity.sPrefUserInfo.setStartTimeInfo(dateforreq+" "+timeforreq);
        Log.e("timeinfo",/*SplashActivity.*/sPrefUserInfo.getStartTimeInfo());
    }

    public void stopVisit() {

        if(visit_name.getText().toString().equals("")||
                visit_loc.getText().toString().equals("")||
                date.getText().toString().equals("") ||
                time.getText().toString().equals("")
                ){
            Log.e("Empy", "empty");
            dialog_result.setText("please fill all fields! ");
            verify.setText("OK");

            popupWindow = new PopupWindow(
                    customView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            //  popupWindow.setAnimationStyle(R.style.animationdialog);
            // popupWindow.setElevation(20.5f);
            //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
            // popupWindow.showAtLocation
        }
        else {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            // stopService(Servintent);
            if(TrackLocService.instance!=null)
            TrackLocService.instance.onDestroy();

            String url = "http://www.thinkbank.co.in/Rajeshahi_app/postVisitData.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("Response", String.valueOf(response));

                            progressDialog.dismiss();
                            isRunning=false;
                            sPrefUserInfo.setVisitInfo("");
                            dialog_result.setText("Journey Stopped!");
                            verify.setText("OK");

                            popupWindow = new PopupWindow(
                                    customView,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            );
                            //  popupWindow.setAnimationStyle(R.style.animationdialog);
                            // popupWindow.setElevation(20.5f);
                            //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                            popupWindow.setOutsideTouchable(false);
                            // if(getCurrentFocus().getParent()!=null)
                            //    ((ViewGroup)getCurrentFocus().getParent()).removeView(getCurrentFocus());

                            popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                            submit.setText("START");
                            generateId();
                            visit_id.setText(String.valueOf(v_id));
                            visit_loc.setText("");
                            visit_name.setText("");
                            visit_name.setEnabled(true);
                            visit_loc.setEnabled(true);



                            // revokeUriPermissionfileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("ErrorResponse", String.valueOf(error));
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("v_id", visit_id.getText().toString());
                    params.put("v_time", dateforreq + " " + timeforreq);
                    params.put("flag", "stop");


                   /* Log.e("ordersub", String.valueOf(obj));
                    Log.e("ordermain", String.valueOf(payObject));
                    Log.e("imagepath", ConvertImage);*/
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
        }

      //  sPrefVisitInfo.setVisitInfo("no");
    }

    public void isLocationEnabled()
    {

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable either GPS or any other location"
                + " service to find current location.  Click OK to go to"
                + " location services settings to let you do so.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                NewVisitActivity.this.startActivityForResult(new Intent(action),1);
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    public void isNetworkEnabled()
    {



        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        final String action = Settings.ACTION_SETTINGS;
        final String message = "Enable network data.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {

                              /*  Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                                startActivity(intent);*/
                                NewVisitActivity.this.startActivityForResult(new Intent(action),2);
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                                connMgr = (ConnectivityManager)NewVisitActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                                network_enabled=connMgr.getActiveNetworkInfo();
                                if(network_enabled!=null) {
                                    Log.e("network", String.valueOf(network_enabled));

                                    if (network_enabled.isConnected()) {
                                        Log.e("network", "if");
                                        getCurrentVisit();
                                    } else {
                                        Log.e("network", "else");
                                        isNetworkEnabled();
                                        // getCurrentVisit();
                                    }
                                }
                                else
                                    isNetworkEnabled();

                            }
                        });
        builder.setCancelable(false);
        builder.create().show();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            switch (requestCode) {
                case 1:
                    Log.e("test", "onActivityResult");
                    if (submit.getText().equals("START"))
                        startVisit();
                    else if (submit.getText().equals("STOP"))
                        stopVisit();
                    break;
            }
        }
           else if (requestCode == 2) {
            switch (requestCode) {
                case 2:
                    Log.e("test", "onActivityResult");
                    getCurrentVisit();
                    break;
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // {Some Code}
                    startService(Servintent);
                    submit.setText("STOP");
                }
            }
        }
    }

             @Override
             public void onConnected(@Nullable Bundle bundle) {

             }

             @Override
             public void onConnectionSuspended(int i) {

             }

             @Override
             public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

             }




    public void EnableRuntimeLocationPermission()
    {
        if (ActivityCompat.checkSelfPermission(NewVisitActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(NewVisitActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        }
        else {
            ActivityCompat.requestPermissions(NewVisitActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        }
    }
         }

