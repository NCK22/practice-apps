package com.innovation.neha.tracklocation.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.innovation.neha.tracklocation.AppController;
import com.innovation.neha.tracklocation.R;
import com.innovation.neha.tracklocation.Services.TrackLocService;
import com.innovation.neha.tracklocation.Storage.SPrefUserInfo;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCustomerActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private TextView id;
    private EditText name,address,contact,gst,pan;
    private Button submit;
    private boolean editFlag=false,viewflag=false;
    ProgressDialog progressDialog ;
    PopupWindow popupWindow;
    private TextView dialog_result;
    View customView;
    Button verify;
    String tag_string_req="string_req",lat,lng,u_id;
    int c_id = 0;
    private SPrefUserInfo sPrefUserInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        id=(TextView)findViewById(R.id.tv_cust_id);

        name=(EditText)findViewById(R.id.et_cust_name);
        address=(EditText)findViewById(R.id.et_cust_addr);
        contact=(EditText)findViewById(R.id.et_cust_contact);
        gst=(EditText)findViewById(R.id.et_cust_gst);
        pan=(EditText)findViewById(R.id.et_cust_pan);

        submit=(Button)findViewById(R.id.btn_cust_submit);

        submit.setOnClickListener(this);
        gst.addTextChangedListener(this);
        pan.setEnabled(false);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        customView = inflater.inflate(R.layout.activity_dialog, null);
        dialog_result = (TextView) customView.findViewById(R.id.tv_dialog);
        verify = (Button) customView.findViewById(R.id.btn_dialog);
        verify.setOnClickListener(this);
        verify.setText("Submit");

        Intent intent=getIntent();

        sPrefUserInfo=new SPrefUserInfo(AddCustomerActivity.this);

        /*
        * for edit customer
        * */
        if(intent.getExtras()!=null)
        {
            editFlag=true;

            id.setText("Customer No. "+intent.getStringExtra("id"));
            name.setText(intent.getStringExtra("name"));
            address.setText(intent.getStringExtra("addr"));
            contact.setText(intent.getStringExtra("cnt"));
            gst.setText(intent.getStringExtra("gst"));
            pan.setText(intent.getStringExtra("pan"));
            lat=intent.getStringExtra("lat");
            lng=intent.getStringExtra("lng");
        }
        else

            /*
            * for new customer
            * */
            generateId();

            /*
            *  places API implementation for location of customer
            * */

        PlaceAutocompleteFragment places = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setHint("Search Location");
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                //  Toast.makeText(getApplicationContext(), place.getName(), Toast.LENGTH_SHORT).show();
               //  Toast.makeText(getApplicationContext(), place.getLatLng().toString(), Toast.LENGTH_SHORT).show();
              //  Toast.makeText(getApplicationContext(), place.getLatLng().toString(), Toast.LENGTH_SHORT).show();
              //  lat=place.getLatLng().toString();

                    lat = String.valueOf(place.getLatLng().latitude);
                    lng = String.valueOf(place.getLatLng().longitude);

                    address.setText(place.getName());


            }

            @Override
            public void onError(Status status) {

                // Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewflag=true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_cust_submit:

                /*
                *  Validations
                * */
                Matcher matcher1=null,matcher2=null;

                if(!gst.getText().equals("")) {
                    String s1 = pan.getText().toString();// get your editext value here
                    //  Toast.makeText(this, s1, Toast.LENGTH_LONG).show();
                    Pattern pattern1 = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
                     matcher1 = pattern1.matcher(s1);

                    String s2 = gst.getText().toString();
                    Pattern pattern2 = Pattern.compile("[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9]{1}[Z]{1}[A-Z0-9]{1}");
                     matcher2 = pattern2.matcher(s2);

                    Log.e("gst",gst.getText().toString());
                }

                // Check if pattern matches
                if(name.getText().toString().equals("") || address.getText().toString().equals("")||
                        contact.getText().toString().equals(""))
                {
                   // Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();

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
                        popupWindow.setOutsideTouchable(false);
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                }

                else if(contact.getText().length()<10)
                    Toast.makeText(this, "Invalid contact no.", Toast.LENGTH_LONG).show();


                else if(!gst.getText().toString().equals("")) {
                   if (!matcher1.matches() || !matcher2.matches()) {

                        Toast.makeText(this, "Invalid pan or GST", Toast.LENGTH_LONG).show();
                    }
                }


                /*
                *  sending data to server
                * */
                else
                    postAllData();
                break;

            case R.id.btn_dialog:
                popupWindow.dismiss();
                if(!dialog_result.getText().toString().equals("please fill all fields! "))
                   finish();
            break;
        }

    }

    public void postAllData() {
        Log.e("postAllData", "called");

        Log.e("c_id", String.valueOf(c_id));

            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
/*
* sending data to server
* */
        try {
            u_id= URLEncoder.encode(String.valueOf(/*SplashActivity.*/sPrefUserInfo.getUserInfo()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

            final String url = "http://www.thinkbank.co.in/Rajeshahi_app/postCustData.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("Response", String.valueOf(response));

                            progressDialog.dismiss();
                            if(response.equals("present"))
                                dialog_result.setText("Customer Exists!");
                            else {
                                if (editFlag == true)
                                    dialog_result.setText("Customer Updated");
                                else
                                    dialog_result.setText("Customer Added!");
                            }
                            editFlag = false;
                            verify.setText("OK");

                            popupWindow = new PopupWindow(
                                    customView,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            );

                            //popupWindow.setOutsideTouchable(false);
                            if(popupWindow.isShowing())
                                popupWindow.dismiss();
                            popupWindow.showAtLocation(contact, Gravity.CENTER, 0, 0);

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
                    params.put("c_id", id.getText().toString().substring(13,id.getText().toString().length()));
                    params.put("u_id",u_id);
                    params.put("c_name", name.getText().toString());
                    params.put("c_addr",address.getText().toString());
                    params.put("c_cnt",contact.getText().toString());
                    params.put("c_gst",gst.getText().toString());
                    params.put("c_pan",pan.getText().toString());
                    params.put("c_lat",lat);
                    params.put("c_lng",lng);
                    if(editFlag==true)
                    params.put("flag","edit");
                    else
                        params.put("flag","add");
                    Log.e("flag", String.valueOf(editFlag));
                    Log.e("lat", String.valueOf(lat));
                    Log.e("lng", String.valueOf(lng));
                 /*   Log.e("ordermain", String.valueOf(payObject));
                    Log.e("imagepath",ConvertImage);*/
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

        }


        /*
        * autogenrated customer number
        * */

    public void generateId() {
        //  progressDialog.setMessage("Loading..");
        //  progressDialog.show();
        String url = "http://www.thinkbank.co.in/Rajeshahi_app/fetchCustId.php";
        // Log.e("URL",url);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Log.e("ResponsePrice", response.toString());
                try {

                    c_id = Integer.parseInt(response)+1;
                    id.setText("Customer No. "+String.valueOf(c_id));


                    Log.e("In Generateid",String.valueOf(c_id));
                    //submit.setText("START");
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

                dialog_result.setText("Please Try Again");

                            verify.setText("OK");

            popupWindow = new PopupWindow(
                    customView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                    );

            //popupWindow.setOutsideTouchable(false);
                            if(popupWindow.isShowing())
                                    popupWindow.dismiss();
                            Log.e("viewflag", String.valueOf(viewflag));
                            if(viewflag==true)
                            popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);


        }
        }) {

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    @Override
    protected void onDestroy() {
        viewflag=false;
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        //        return super.onCreateOptionsMenu(menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_edit)
        {
            Intent intent = new Intent(this,CustHistoryActivity.class);
            startActivity(intent);

        }

        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        if(gst.getText().hashCode() == editable.hashCode())
        {
            if(gst.length()==15){

                pan.setEnabled(true);
                pan.setText(gst.getText().toString().substring(2,12));
                pan.setEnabled(false);
            }
        }
    }
}
