package com.innovation.neha.tracklocation.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.innovation.neha.tracklocation.R;
import com.innovation.neha.tracklocation.Services.TrackLocService;
import com.innovation.neha.tracklocation.Storage.SPrefUserInfo;

/**
 * Created by Neha on 24-11-2017.
 */

public class SplashActivity extends AppCompatActivity {

    Intent intent;
    private SPrefUserInfo sPrefUserInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(android.R.style.SplashTheme);
        setContentView(R.layout.activity_splash);

        Log.e("Splashscreen","created");
        sPrefUserInfo = new SPrefUserInfo(SplashActivity.this);

        Thread splash= new Thread(){
            public void run(){
                try{
                    Log.e("Thread","started");
                    sleep(3000);
                    Intent Servintent=new Intent(SplashActivity.this, TrackLocService.class);
                   // startService(Servintent);
                    if(sPrefUserInfo.getUserInfo().equals(""))
                        intent= new Intent(SplashActivity.this, LoginActivity.class);
                    else
                    intent = new Intent(SplashActivity.this, FrontActivity.class);

                    startActivity(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    finish();
                }
            }

        };
        splash.start();


    }
}
