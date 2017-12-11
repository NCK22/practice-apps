package com.innovation.neha.tracklocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.innovation.neha.tracklocation.Services.TrackLocService;

/**
 * Created by Neha on 24-11-2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(android.R.style.SplashTheme);
        setContentView(R.layout.activity_splash);

        Log.e("Splashscreen","created");
        Thread splash= new Thread(){
            public void run(){
                try{
                    Log.e("Thread","started");
                    sleep(3000);
                    Intent Servintent=new Intent(SplashActivity.this, TrackLocService.class);
                   // startService(Servintent);
                    Intent intent = new Intent(SplashActivity.this, PlaceOrderActivity.class);
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
