package com.innovation.neha.tracklocation.Broadcasts;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import com.innovation.neha.tracklocation.Activities.FrontActivity;
import com.innovation.neha.tracklocation.Activities.SplashActivity;
import com.innovation.neha.tracklocation.Services.TrackLocService;
import com.innovation.neha.tracklocation.Storage.SPrefUserInfo;
//import com.innovation.neha.tracklocation.Storage.SPrefVisitInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;


/**
 * Created by Neha on 06-10-2017.
 */

public class CheckInternet extends BroadcastReceiver {

    Boolean flg=true;
    SPrefUserInfo sPrefUserInfo;
    private int mYear, mMonth, mDay, mHour, mMinute;
    public String dateforreq="",timeforreq="";

    //SPrefUserInfo sPrefUserInfo;
    @Override
    public void onReceive( Context context,  Intent intent) {

        Log.e("check interet","onreceive");
         ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        sPrefUserInfo=new SPrefUserInfo(context);
//        sPrefVisitInfo=new SPrefVisitInfo(context);

        /* android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);*/

        NetworkInfo mobile=connMgr.getActiveNetworkInfo();


        if (mobile != null){

            if(mobile.isConnected()) {
                // Do something
                Toast.makeText(context,"Network Available", Toast.LENGTH_SHORT).show();
                Log.e("Network Available ", "Flag No 1");

                if(TrackLocService.instance!=null) {
                    TrackLocService.instance=null;
                }

                datePicker();
                    intent=new Intent(context,TrackLocService.class);
                intent.putExtra("uid",sPrefUserInfo.getUserInfo());
                    intent.putExtra("vid", sPrefUserInfo.getVisitId());
                    intent.putExtra("time",dateforreq);
                    Log.e("CIvid", sPrefUserInfo.getVisitId());
                Log.e("dateforreq CI", dateforreq);

                    context.startService(intent);


            }

        }
        else {
            Toast.makeText(context,"Network Not Available", Toast.LENGTH_SHORT).show();
            Log.e("Network not Available ", "Flag No 1");
            /*try {




                 setMobileDataEnabled(context,flg);
            } catch (ClassNotFoundException e) {
                Toast.makeText(context,e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                Toast.makeText(context,e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                Toast.makeText(context,e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Toast.makeText(context,e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Toast.makeText(context,e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }*/
        }

    }

    private void datePicker() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);


        timeforreq = mHour + ":" + mMinute + ":00";

        dateforreq=mYear+"-"+(mMonth + 1)+"-"+mDay+" "+timeforreq;



    }

    public void setMobileDataEnabled(Context context, Boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
         ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
         Class conmanClass = Class.forName(conman.getClass().getName());
         Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
         Object connectivityManager = connectivityManagerField.get(conman);
         Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());

        /*Class[] cArg = new Class[2];
        cArg[0] = String.class;
        cArg[1] = Boolean.TYPE;*/

         Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled",Boolean.TYPE);

        /*Object[] pArg = new Object[2];
        pArg[0] = context.getPackageName();
        pArg[1] = true;*/
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }

}