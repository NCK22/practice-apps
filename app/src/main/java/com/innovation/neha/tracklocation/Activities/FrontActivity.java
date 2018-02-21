package com.innovation.neha.tracklocation.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

//import com.vedbiz.www.salesperson.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.innovation.neha.tracklocation.AppController;
import com.innovation.neha.tracklocation.Broadcasts.CheckInternet;
import com.innovation.neha.tracklocation.R;
import com.innovation.neha.tracklocation.Services.SendBroadcastService;
import com.innovation.neha.tracklocation.Services.TrackLocService;
import com.innovation.neha.tracklocation.Storage.SPrefUserInfo;
//import com.innovation.neha.tracklocation.Storage.SPrefVisitInfo;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FrontActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout new_visit_layout,place_order_layout,order_history_layout,manage_cust_layout;
    TextView new_visit_edt;
    ProgressDialog progressDialog;
    public String tag_string_req = "stringrequest";
    public static boolean isVisitStarted=false;
    PopupWindow popupWindow;
    private TextView dialog_result;
    View customView;
    Button verify;
    public  static final int RequestPermissionCode  = 1 ;
    public static final int RESULT_OK = -1;
    public static ConnectivityManager connMgr;
    public static android.net.NetworkInfo network_enabled;
    private boolean gps_enabled=false,broadcastFlag = false;
    private String v_name,v_loc,dateforreq,timeforreq;;
    public static String v_id;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Intent servIntent;
    static Context context;
    public static AlertDialog.Builder builder;
    public static boolean builderFlag=false;
    private SPrefUserInfo sPrefUserInfo;
    private CheckInternet checkInternet;
//    private SPrefVisitInfo sPrefVisitInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        new_visit_layout=(LinearLayout)findViewById(R.id.new_visit_layout);
        place_order_layout=(LinearLayout)findViewById(R.id.place_order_layout);
        order_history_layout=(LinearLayout)findViewById(R.id.order_history_layout);
        manage_cust_layout=(LinearLayout)findViewById(R.id.cust_manage_layout);
        new_visit_edt=(TextView)findViewById(R.id.new_visit_edt);

        new_visit_layout.setOnClickListener(this);
        place_order_layout.setOnClickListener(this);
        order_history_layout.setOnClickListener(this);
        manage_cust_layout.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        customView = inflater.inflate(R.layout.activity_dialog, null);
        dialog_result = (TextView) customView.findViewById(R.id.tv_dialog);
        verify = (Button) customView.findViewById(R.id.btn_dialog);
        verify.setOnClickListener(this);


        /*
        *  Checking for permissons of camera,storage & asking for the same if not granted previously
        * */

        EnableRuntimePermissionToAccessCamera();

        servIntent=new Intent(this,SendBroadcastService.class);

//        startService(servIntent);


        Log.e("FrontActivity","called");
        Intent intent=getIntent();
        if(intent.getExtras()!=null)
        {
            broadcastFlag=true;
            if(intent.getStringExtra("flag").equals("locoff"))
            {
                Log.e("builderFlag", String.valueOf(builderFlag));
                     isLocationEnabled();
            }

        }

        sPrefUserInfo=new SPrefUserInfo(FrontActivity.this);
        checkInternet=new CheckInternet();
     //   sPrefVisitInfo=new SPrefVisitInfo(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        //        return super.onCreateOptionsMenu(menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       if(id == R.id.action_logout)
        {
            logout();
        }

        return true;
    }

    /*
    *  functon for logout
    * */
    private void logout() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(FrontActivity.this);
        builder.setMessage("Do you want do Sign-out?")
                .setTitle("Sign-out!!!")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        //SplashActivity.sPrefUserInfo.setUserInfo("");
                        sPrefUserInfo.setUserInfo("");
                        sPrefUserInfo.setVisitId("");
                        if(TrackLocService.instance!=null)
                            TrackLocService.instance.stopSelf();
                        TrackLocService.instance=null;
                        Intent intent=new Intent(FrontActivity.this,LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION );
                        startActivity(intent);
                        finish();

                    }
                });

        builder.create();
        builder.show();


}


    /*
    *  function for getting info about current ongoing visit
    * */

    public void getCurrentVisit()
    {
        progressDialog.setMessage("Loading..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String url;
        if(broadcastFlag==true)
        {
            SPrefUserInfo sPrefUserInfo = new SPrefUserInfo(FrontActivity.this);
            sPrefUserInfo.getUserInfo();
               url = "http://www.thinkbank.co.in/Rajeshahi_app/getCurrentVisit.php?u_id="+sPrefUserInfo.getUserInfo();
        }
        else {
            //Log.e("userinfo", SplashActivity.sPrefUserInfo.getUserInfo());
            Log.e("userinfo front", sPrefUserInfo.getUserInfo());
            url = "http://www.thinkbank.co.in/Rajeshahi_app/getCurrentVisit.php?u_id=" + /*SplashActivity.*/sPrefUserInfo.getUserInfo();
        }
        Log.e("URL",url);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response",String.valueOf(response));
                        // Toast.makeText(getBaseContext(),"Response:"+response,Toast.LENGTH_SHORT).show();
                        // the response is already constructed as a JSONObject!

                        progressDialog.dismiss();
                        try {

                            if(broadcastFlag==true)
                            {

                                v_id=response.getString("w1");
                                sPrefUserInfo.setVisitId(response.getString("w1"));
                                Log.e("sp front vid",sPrefUserInfo.getVisitId());
                              //  v_name=response.getString("w2");
                               // v_loc=response.getString("w3");
                                stopVisit();
                                isVisitStarted=false;

                            }
                            else
                                isVisitStarted=true;
                            Log.e("In getCurrentVisit",response.getString("w1"));
                            sPrefUserInfo.setVisitId(response.getString("w1"));
                            Log.e("sp front vid",sPrefUserInfo.getVisitId());

                                sPrefUserInfo.setVisitInfo("yes");
                            Log.e("isVisitStarted", String.valueOf(isVisitStarted));
                            if(isVisitStarted==true)
                            {
                                isLocationEnabled();
                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("ResponseCatch",String.valueOf(e));
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        isVisitStarted=false;
                        sPrefUserInfo.setVisitInfo("no");
                        progressDialog.dismiss();
                        Log.e("ResponseError",String.valueOf(error));
                    }
                });

        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);


    }

    @Override
    protected void onResume() {
        super.onResume();

        context=this;
        Log.e("inside","onResume");
        //Log.e("isVisitStarted", String.valueOf(isVisitStarted));


        if(broadcastFlag==false) {
            connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

            network_enabled = connMgr.getActiveNetworkInfo();

            //  network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.e("network", String.valueOf(network_enabled));
            if (network_enabled != null) {
                Log.e("network", String.valueOf(network_enabled));

            /*
            *  if connected , get info of current ongoing visit
            * */
                if (network_enabled.isConnected()) {
                    Log.e("network", "if");
                    getCurrentVisit();




                }
            /*
            *  if not connected, ask to enable internet
            * */
                else {
                    Log.e("network", "else");
                    isNetworkEnabled();
                    // getCurrentVisit();
                }
            } else {
                isNetworkEnabled();
            }

        }

      //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            this.registerReceiver(checkInternet,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
       // }
       /* else
        {
            if(TrackLocService.instance!=null)
                TrackLocService.instance=null;
            //Intent intent=new Intent(Fthis,TrackLocService.class);
            startService(new Intent(FrontActivity.this,TrackLocService.class));
        }*/

    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {

            // New Visit
            case R.id.new_visit_layout:
                startActivity(new Intent(getApplicationContext(),NewVisitActivity.class));
                break;

            // Place Order
            case R.id.place_order_layout:
                Log.e("isVisitStarted", String.valueOf(isVisitStarted));

                if(isVisitStarted==true)
                    startActivity(new Intent(getApplicationContext(),PlaceOrderActivity.class));
                else
                {
                    dialog_result.setText("Start Visit to place order");
                    verify.setText("OK");
                    popupWindow = new PopupWindow(
                            customView,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );

                   // popupWindow.setOutsideTouchable(true);
                    popupWindow.showAtLocation(place_order_layout, Gravity.CENTER, 0, 0);
                }
              //  Toast.makeText(getApplicationContext(),"Place Order",Toast.LENGTH_SHORT).show();
                break;

            // Order History
            case R.id.order_history_layout:
                startActivity(new Intent(getApplicationContext(),HistoryActivity.class));
              //  Toast.makeText(getApplicationContext(),"Order History",Toast.LENGTH_SHORT).show();
                break;

            // Manage Customers
            case R.id.cust_manage_layout:
                startActivity(new Intent(getApplicationContext(),AddCustomerActivity.class));
                break;

            // popup button
            case R.id.btn_dialog://popupWindow.dismiss();
            if(dialog_result.getText().toString().equals("Journey Stopped!"))
            {
                Log.e("yes","yes");
                popupWindow.dismiss();
                finish();
            }
            else
                popupWindow.dismiss();

            break;
        }
    }

    /*
    *  function to check is permissions are granted & asking for the same if not granted
    * */
    public void EnableRuntimePermissionToAccessCamera(){

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) &&
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED))
        {

            // Printing toast message after enabling runtime permission.
            //   Toast.makeText(this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, RequestPermissionCode);

        }
        progressDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // {Some Code}

                }
            }
        }
    }


    /*
    *  function to ask user to start internet if not connected.Takes to the setting page on OK click
    * */
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
                                FrontActivity.this.startActivityForResult(new Intent(action),2);
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();


                                connMgr = (ConnectivityManager)FrontActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

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

    public void isLocationEnabled()
    {
        builderFlag=true;
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (!gps_enabled) {


            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            final String message = "Disabling location will stop"
                    + " your visit"
                    + " Do you still want to turn off location?";

            builder.setMessage(message)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    //context.this.startActivityForResult(new Intent(action),1);
                                    d.dismiss();
                                 //   builderFlag=false;
                                    getCurrentVisit();
                                  //  broadcastFlag=false;
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                    FrontActivity.this.startActivityForResult(new Intent(action),1);

                                   // isLocationEnabled();
                                }
                            });
            builder.setCancelable(false);
            builder.create().show();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            switch (requestCode) {
                case 1:
                    Log.e("test", "onActivityResult");
                    if(gps_enabled)
                    finish();
                    else
                        isLocationEnabled();
                   // broadcastFlag=false;
                    break;
            }


        }


    }

    public void stopVisit() {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        dateforreq=mYear+"-"+(mMonth+1)+"-"+mDay;
        Log.e("dateforreq",dateforreq);

        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        //time.setText(convertTimeFormat(mHour + ":" + mMinute));
        timeforreq = mHour + ":" + mMinute + ":00";
        Log.e("timeforreq",timeforreq);

        // stopService(Servintent);
            if(TrackLocService.instance!=null)
                TrackLocService.instance.stopSelf();/*onDestroy()*/;

            String url = "http://www.thinkbank.co.in/Rajeshahi_app/postVisitData.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("Response", String.valueOf(response));

                            progressDialog.dismiss();
                           // isRunning=false;
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

                            popupWindow.showAtLocation(place_order_layout, Gravity.CENTER, 0, 0);




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
                    params.put("v_id", v_id);
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

       // SplashActivity.sPrefUserInfo.setVisitInfo("no");


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

    @Override
    protected void onDestroy() {

        try {
            if (checkInternet != null)
                unregisterReceiver(checkInternet);
        }catch(IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}


