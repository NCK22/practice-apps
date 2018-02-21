package com.innovation.neha.tracklocation.Broadcasts;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.innovation.neha.tracklocation.Activities.FrontActivity;
import com.innovation.neha.tracklocation.Activities.NewVisitActivity;
import com.innovation.neha.tracklocation.Services.TrackLocService;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by Neha on 06-10-2017.
 */

public class CheckLocation extends BroadcastReceiver {

    Boolean flg=true;
    Context context;
    private boolean gps_enabled = false;
    @Override
    public void onReceive( Context context,  Intent intent) {

        this.context=context;
        Log.e("check location", "onreceive");

       intent =new Intent(context,FrontActivity.class);
       intent.putExtra("flag","locoff");

        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gps_enabled) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }

        if (gps_enabled) {
            Intent intent1=new Intent(context, TrackLocService.class);
          //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent);
        }

           // isLocationEnabled();

    }

    public void isLocationEnabled()
    {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (!gps_enabled) {

            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(context);
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
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                    isLocationEnabled();
                                }
                            });
            builder.create().show();
        }
    }

}
