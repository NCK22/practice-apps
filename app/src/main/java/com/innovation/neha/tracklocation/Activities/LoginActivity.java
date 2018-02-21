package com.innovation.neha.tracklocation.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.innovation.neha.tracklocation.AppController;
import com.innovation.neha.tracklocation.Broadcasts.CheckInternet;
import com.innovation.neha.tracklocation.R;
import com.innovation.neha.tracklocation.Storage.SPrefUserInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{


    ProgressDialog progressDialog;
    EditText uname,pass;
    Button check;
    public static String name,pwd;
    String tag_string_req = "string_req";
    Intent intent;
    CheckInternet checkInternet;
    public SPrefUserInfo sPrefUserInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        uname=(EditText)findViewById(R.id.etname);
        pass=(EditText)findViewById(R.id.etpassword);
        check=(Button)findViewById(R.id.btnlogin);

        check.setOnClickListener(this);

        intent=new Intent(this,FrontActivity.class);

        checkInternet=new CheckInternet();
        sPrefUserInfo = new SPrefUserInfo(LoginActivity.this);
    }

    /*
    *  function to check login & open FrontActivity for valid credentials
    * */

    public void checkLogin()
    {

        try {
            name= URLEncoder.encode(String.valueOf(uname.getText().toString()), "UTF-8");
            pwd= URLEncoder.encode(String.valueOf(pass.getText().toString()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String url = "http://www.thinkbank.co.in/Rajeshahi_app/login.php?l_name="+name+"&&pass="+pwd;
         Log.e("URL",url);
       //  Toast.makeText(LoginActivity.this,url,Toast.LENGTH_SHORT).show();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Log.e("Response", response.toString());
                try {

                    progressDialog.dismiss();
                    if(response.equals("0"))
                        Toast.makeText(LoginActivity.this,"Invalid Credentials!",Toast.LENGTH_SHORT).show();
                    else if(response.equals(""))
                        Toast.makeText(LoginActivity.this,"Slow Internet",Toast.LENGTH_SHORT).show();
                    else {
                        //SplashActivity.sPrefUserInfo.setUserInfo(response);
                        sPrefUserInfo.setUserInfo(response);
                        startActivity(intent);
                    }//Toast.makeText(getApplicationContext(), "iprice:"+String.valueOf(iprice), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,"Please enable internet connection",Toast.LENGTH_SHORT).show();
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                Log.e("ResponseError", error.toString());

            }
        }) {

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    @Override
    public void onClick(View view) {

        checkLogin();
    }
}
