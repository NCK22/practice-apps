package com.innovation.neha.tracklocation.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.innovation.neha.tracklocation.AppController;
import com.innovation.neha.tracklocation.R;
import com.innovation.neha.tracklocation.Storage.SPrefUserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.*;



import fr.ganfra.materialspinner.MaterialSpinner;
import id.zelory.compressor.Compressor;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

//import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner;

public class PlaceOrderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, Animation.AnimationListener, TextWatcher, View.OnFocusChangeListener {

    Dialog dialog;
    private static final int DIALOG_SHOW_TIME = 5000;
    public static File file;

    MaterialSpinner product,weight;

    List<String> list_prod=new ArrayList<String>();
    List<String>list_wt=new ArrayList<String>();
    List<String>list_clnt=new ArrayList<String>();
    String[] clients;

    EditText cash_amt,cheque_amt,dd_amt,credit_amt,cheque_no,dd_no,qty;
    AutoCompleteTextView clnt_name;
    RadioButton cash,cheque,dd,credit;
    LinearLayout ll_cash,ll_cheque,ll_dd,ll_credit,ll_radio_parent,ll_image;
    FloatingActionButton plus,minus;
    ImageView iplus;
    TextView ord_no,bag,price,total,restamt,prevbal;
    Button submit,done;
    View customView,customView2;
    TextView dialog_result,dialog_result2;
    ImageView dialog_image;
    Button verify;
    public static PopupWindow popupWindow,popupWindow2;
    public static int ordercnt=0;
    DecimalFormat dec;



   HashMap<String,JSONObject> suborderMap=new HashMap<String,JSONObject>();
    JSONObject payObject=new JSONObject();
    //JSONObject suborder = new JSONObject();
    JSONArray suborderArray=new JSONArray();
    JSONObject obj=new JSONObject();

    String tag_string_req = "jsonobj_req";
    Double iprice;
    int count=0;
    public static String method="",intentprodname="",intentweight;
    public static String rcvd_amt="0.0",crdt_amt="0.0";
    public static double tot=0.0,fprice=0.0,rest=0.0,oldprice=0.0,prevblnc=0.0,rec_rest_tot=0.0;
    public static boolean rcvIntentP=false,rcvIntentW=false,editFlag=false,subEditFlag=false,equalFlag=false,newCustomer=false;

    private static final int CAMERA_CAPTURE_CODE = 501;

    Button CaptureImageFromCamera,UploadImageToServer,yes,no,chooseImageFromGallery;
    ImageView ImageViewHolder;
    EditText imageName;
    ProgressDialog progressDialog ;
    Intent intent ;
    public  static final int RequestPermissionCode  = 1 ;
    Bitmap bitmap;
    boolean check = true;
    String GetImageNameFromEditText;
    String ImageNameFieldOnServer = "image_name" ;
    String ImagePathFieldOnServer = "image_path" ;
    String ImageUploadPathOnSever ="http://www.thinkbank.co.in/Rajeshahi_app_testing/capture_img_upload_to_server.php" ;
    String tag_string_req2 = "string_req";
    private Uri mCameraFileUri;

    Animation slideDownAnimation;

    private SPrefUserInfo sPrefUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        sPrefUserInfo=new SPrefUserInfo(PlaceOrderActivity.this);

        dec = new DecimalFormat(".##");

        ll_cash=(LinearLayout)findViewById(R.id.ll_cash);
        ll_cheque=(LinearLayout)findViewById(R.id.ll_cheque);
        ll_dd=(LinearLayout)findViewById(R.id.ll_dd);
        ll_credit=(LinearLayout)findViewById(R.id.ll_credit);
        ll_radio_parent=(LinearLayout)findViewById(R.id.ll_radio_parent);
        ll_image=(LinearLayout)findViewById(R.id.ll_image);

        cash_amt=(EditText)findViewById(R.id.et_cash_rcvd_amt);
        cheque_amt=(EditText)findViewById(R.id.et_chq_rcvd_amt);
        dd_amt=(EditText)findViewById(R.id.et_dd_rcvd_amt);
        credit_amt=(EditText)findViewById(R.id.et_credit_rcvd_amt);
        cheque_no=(EditText)findViewById(R.id.et_chq_no);
        dd_no=(EditText)findViewById(R.id.et_dd_no);

        ord_no=(TextView)findViewById(R.id.tv_ord_no);
        clnt_name=(AutoCompleteTextView) findViewById(R.id.et_clnt_name);


        /*
        *  For Client Name autocomplete field
        * */

        populateClntList();

        qty=(EditText)findViewById(R.id.et_qty);

        cash=(RadioButton)findViewById(R.id.rb_cash);
        cheque=(RadioButton)findViewById(R.id.rb_chq);
        dd=(RadioButton)findViewById(R.id.rb_dd);
        credit=(RadioButton)findViewById(R.id.rb_credit) ;

        product=(MaterialSpinner) findViewById(R.id.sp_prod);
        weight=(MaterialSpinner) findViewById(R.id.sp_weight);

        bag=(TextView)findViewById(R.id.tv_bag);
        price=(TextView)findViewById(R.id.tv_price);
        total=(TextView)findViewById(R.id.tv_tot_amt);
        restamt=(TextView)findViewById(R.id.tv_rest_amt);
        prevbal=(TextView)findViewById(R.id.tv_prev_bal);

        plus=(FloatingActionButton)findViewById(R.id.fab_plus);
        minus=(FloatingActionButton)findViewById(R.id.fab_minus);

       // submit=(Button)findViewById(R.id.btn_submit);
        done=(Button)findViewById(R.id.btn_done);

      //  iplus=(ImageView)findViewById(R.id.img_overplus);

        CaptureImageFromCamera = (Button)findViewById(R.id.btn_Capture);
        chooseImageFromGallery = (Button)findViewById(R.id.btn_Choose);
        ImageViewHolder = (ImageView)findViewById(R.id.imageView);
        UploadImageToServer = (Button) findViewById(R.id.btn_upload);

        /*
        *  permission
        * */

        EnableRuntimePermissionToAccessCamera();
        //EnableRuntimePermissionToAccessStorage();

       // submit.setOnClickListener(this);
        done.setOnClickListener(this);

        plus.setOnClickListener(this);
        minus.setOnClickListener(this);

        cash.setOnClickListener(this);
        cheque.setOnClickListener(this);
        dd.setOnClickListener(this);
        credit.setOnClickListener(this);

        CaptureImageFromCamera.setOnClickListener(this);
        chooseImageFromGallery.setOnClickListener(this);
        UploadImageToServer.setOnClickListener(this);

        product.setOnItemSelectedListener(this);
        weight.setOnItemSelectedListener(this);

        qty.addTextChangedListener(this);
        cash_amt.addTextChangedListener(this);
        cheque_amt.addTextChangedListener(this);
        credit_amt.addTextChangedListener(this);
        dd_amt.addTextChangedListener(this);
        total.addTextChangedListener(this);
        bag.addTextChangedListener(this);

        clnt_name.setOnFocusChangeListener(this);
        cash_amt.setOnFocusChangeListener(this);
        cheque_amt.setOnFocusChangeListener(this);
        dd_amt.setOnFocusChangeListener(this);
        restamt.addTextChangedListener(this);


     //   dialog = new SpotsDialog(this,R.style.Custom);
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle("Please enter all values!");
        dialog.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        customView = inflater.inflate(R.layout.activity_dialog,null);

        /*dialog_result = (TextView) dialog.findViewById(R.id.tv_dialog);
        dialog_image=(ImageView)dialog.findViewById(R.id.img_dialog);*/

        dialog_result = (TextView) customView.findViewById(R.id.tv_dialog);
      //  dialog_image=(ImageView)customView.findViewById(R.id.img_dialog);
        verify=(Button)customView.findViewById(R.id.btn_dialog);
        verify.setOnClickListener(this);


        LayoutInflater inflater2 = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        customView2 = inflater2.inflate(R.layout.activity_del_dialog, null);

        dialog_result2 = (TextView) customView2.findViewById(R.id.tv_del_dialog);
        yes = (Button) customView2.findViewById(R.id.btn_del_dialog_yes);
        no = (Button) customView2.findViewById(R.id.btn_del_dialog_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);


        enableAll();
        cash.setEnabled(false);
        cheque.setEnabled(false);
        dd.setEnabled(false);
        credit.setEnabled(false);
        CaptureImageFromCamera.setEnabled(true);
        rcvd_amt="0.0";
      //  Toast.makeText(this,"oncreate"+rcvd_amt,Toast.LENGTH_SHORT).show();

       // Toast.makeText(this,random(),Toast.LENGTH_SHORT).show();
        ord_no.setText("Order No:"+random());

        //Creating the ArrayAdapter instance having the product list
        ArrayAdapter aaprod = new ArrayAdapter(this,android.R.layout.simple_spinner_item,list_prod);
        aaprod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        product.setAdapter(aaprod);

        //Creating the ArrayAdapter instance having the weight list
        ArrayAdapter aaweight=new ArrayAdapter(this,android.R.layout.simple_spinner_item,list_wt);
        aaweight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weight.setAdapter(aaweight);

        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slidedown);
        slideDownAnimation.setAnimationListener(this);



        Intent intent=getIntent();
       // Toast.makeText(getApplicationContext(),String.valueOf(intent.getExtras()),Toast.LENGTH_SHORT).show();
        /*
        *  Edit Mode
        * */
if(intent.getExtras()!=null) {


   // Toast.makeText(getApplicationContext(),"Edit inside",Toast.LENGTH_SHORT).show();
    editFlag=true;
    credit.setEnabled(false);

    ll_image.setVisibility(View.GONE);
//    ImageViewHolder.setVisibility(View.GONE);
 //   CaptureImageFromCamera.setVisibility(View.GONE);
  //  CaptureImageFromCamera.setEnabled(false);
    ll_radio_parent.setEnabled(true);

    Log.e("indexflag",intent.getStringExtra("flag"));
    /*
    *  Main Edit
    * */
    if(intent.getStringExtra("flag").toString().equals("main")) {


        ord_no.setText(intent.getStringExtra("ord"));
        count= Integer.parseInt(intent.getStringExtra("count"));
        bag.setText(intent.getStringExtra("count"));
        clnt_name.append(intent.getStringExtra("cust"));
        total.setText("Total Amt. " + intent.getStringExtra("tot"));
       // restamt.setText("Rest Amt."+intent.getStringExtra("rest"));
        tot= Double.parseDouble(intent.getStringExtra("tot"));
        Log.e("tot from intent",String.valueOf(tot));
        String intentmethod = intent.getStringExtra("method");
        method=intent.getStringExtra("method");
        rcvd_amt=intent.getStringExtra("rcvd");
        Log.e("rcvd intent",rcvd_amt);
        rec_rest_tot=Double.parseDouble(intent.getStringExtra("rest"));
        Log.e("rec_rest_tot intent",""+rec_rest_tot);
        crdt_amt=intent.getStringExtra("crdt");
        Log.e("crdt intent",crdt_amt);
        String c = "cash", ch = "cheque", d = "dd",cr="credit";
        Log.e("method",intentmethod);
        if (intentmethod.equals(c)) {

            cash.setChecked(true);
            cash.setSelected(true);
            cash.setEnabled(true);
            cash_amt.setEnabled(true);
            restamt.setText("Total Amt to be paid: "+rec_rest_tot);
          //  cash_amt.setText(intent.getStringExtra("rcvd"));
           // credit.setEnabled(true);

        }

      else if(intentmethod.equals(cr))
        {
            credit.setChecked(true);
            credit.setSelected(true);
          //  credit.setEnabled(true);
            credit_amt.setEnabled(true);
            credit_amt.setText(crdt_amt);
            credit_amt.setEnabled(false);
            cash.setEnabled(true);
        }
        else if (intentmethod.equals(ch)) {

            Log.e("Cheque No",intent.getStringExtra("cheque"));
            cheque.setChecked(true);
            cheque.setSelected(true);
            cheque_amt.setEnabled(true);
            cheque_no.setEnabled(true);
            cheque_amt.setText(intent.getStringExtra("rcvd"));
            cheque_no.setText(intent.getStringExtra("cheque"));
        } else if (intentmethod.equals(d)) {

            Log.e("dd No",intent.getStringExtra("dd"));
            dd.setChecked(true);
            dd.setSelected(true);
            dd_amt.setEnabled(true);
            dd_no.setEnabled(true);
            dd_amt.setText(intent.getStringExtra("rcvd"));
            dd_no.setText(intent.getStringExtra("dd"));
        }

        populateProdSpinner();
       // minus.setEnabled(false);
    }
    /*
    * Sub Edit
    * */
    else if(intent.getStringExtra("flag").toString().equals("sub"))
    {
     //   Log.e("indexord",intent.getStringExtra("ord"));


        subEditFlag=true;
        rcvIntentP=true;
        rcvIntentW=true;
        bag.setText("1");
        intentprodname=intent.getStringExtra("p");
        intentweight=intent.getStringExtra("w");
        rec_rest_tot=Double.parseDouble(intent.getStringExtra("rest"));
        Log.e("inptentprodname",intentprodname);
        Log.e("intentweightname",intentweight);
         qty.setText(intent.getStringExtra("q"));
         price.setText("Price:"+intent.getStringExtra("pr"));
        oldprice=Double.parseDouble(intent.getStringExtra("pr"));
        ord_no.setText(intent.getStringExtra("ord"));
        clnt_name.setText(intent.getStringExtra("cust"));
        populateProdSpinner();
//         disableAll();
        CaptureImageFromCamera.setEnabled(false);
       ImageViewHolder.setEnabled(false);
        total.setText("Total Amt." + intent.getStringExtra("tot"));
        tot=Double.parseDouble(intent.getStringExtra("tot"))-Double.parseDouble(intent.getStringExtra("pr"));
        Log.e("price in sub intent",intent.getStringExtra("pr"));
        Log.e("tot in sub intent",""+tot);
    }
}
/*
*  New Order
* */
else
{
    editFlag=false;
    subEditFlag=false;
    populateProdSpinner();
}



Log.e("SubEditFlag", String.valueOf(subEditFlag));

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume","called");
        Log.e("rest in onResume",""+rest);
       // checkPrevBal();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Inside","onDestroy");
    }

    @Override
    protected void onStop() {
        rest=0.0;
        editFlag=subEditFlag=false;
        oldprice=0.0;
        super.onStop();
        Log.e("Inside","onStop");
        rest=0.0;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
          if(adapterView.getId()==R.id.sp_prod) {

                     Log.e("adapter view","sp_prod");
              if (product.getSelectedItemPosition() != 0)
                  /*
                  *  populate weights of selected product
                  * */
                  populateWtSpinner(product.getSelectedItem().toString());
                  /*
                  *  Resetting of weight & product spinners
                  * */
              else if(product.getSelectedItemPosition()==0)
              {
                  list_wt.clear();
                  weight.setSelection(0);
              }
              Log.e("SubEditFlag", String.valueOf(subEditFlag));

          }
          else if(adapterView.getId()==R.id.sp_weight)
          {
             // Toast.makeText(getApplicationContext(),String.valueOf(rcvIntentW),Toast.LENGTH_SHORT).show();
              qty.setText("");
              Log.e("adapter view","sp_wt");
              if(rcvIntentW==true)
              {
                  //rcvIntentW=false;
                  //rcvIntentP=false;
              }
              else {
                  if (weight.getSelectedItemPosition() != 0)
                  {
                      /*
                      *  get price of selected item for selected weight
                      * */
                             // Toast.makeText(getApplicationContext(),"Run",Toast.LENGTH_SHORT).show();
                              getProdPrice(product.getSelectedItem().toString(), weight.getSelectedItem().toString());
                      qty.setText("");

                  }

              }

              Log.e("SubEditFlag", String.valueOf(subEditFlag));
          }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.rb_cash:  method="cash";
                                // Toast.makeText(this,"Changed to cash",Toast.LENGTH_SHORT).show();
                                cheque.setChecked(false);
                                dd.setChecked(false);
                                credit.setChecked(false);
                                cash_amt.setEnabled(true);

                                dd_amt.setText("");
                                dd_no.setText("");
                                cheque_amt.setText("");
                                cheque_no.setText("");
                                credit_amt.setText("");
                                cheque_amt.setEnabled(false);
                                credit_amt.setEnabled(false);
                                cheque_no.setEnabled(false);
                                dd_amt.setEnabled(false);
                                dd_no.setEnabled(false);


                                ll_cash.setBackgroundColor(Color.parseColor("#FFF176"));
                                ll_cheque.setBackgroundColor(Color.parseColor("#BBDEFB"));
                                ll_dd.setBackgroundColor(Color.parseColor("#BBDEFB"));
                                ll_credit.setBackgroundColor(Color.parseColor("#BBDEFB"));

                                ll_radio_parent.setBackgroundColor(Color.parseColor("#FFF176"));
                                if(editFlag==true)
                                    restamt.setText("Tot Amt to be paid: "+String.format("%.02f", rec_rest_tot));
                                else
                            restamt.setText("Tot Amt to be paid: "+String.format("%.02f", tot));
                             // restamt.setText("Tot Amt to be paid: "+String.format("%.02f", rest));
              //  restamt.setText("Tot Amt to be paid: "+String.format("%.02f", tot+prevblnc));

                            /* if(!credit_amt.getText().toString().equals("")&&cash_amt.getText().toString().equals("")) {
                             Log.e("Inside rb_cash","first if");
                                 Log.e("Inside rb_cash rest",""+rest);
                                 cash_amt.setText(String.valueOf(rest));
                             }


                                if(!cash_amt.getText().toString().equals("") &&!credit_amt.getText().toString().equals("")) {
                                    if (rest != 0) {
                                        Toast.makeText(PlaceOrderActivity.this, "Enter correct amounts", Toast.LENGTH_SHORT).show();
                                       // credit_amt.setText("");
                                    }

                                }*/
                            crdt_amt="0.0";
                    break;

            case R.id.rb_chq:   method="cheque";
                               // Toast.makeText(this,"Changed to chq",Toast.LENGTH_SHORT).show();
                                cash.setChecked(false);
                                dd.setChecked(false);

                                cheque_amt.setEnabled(true);
                                cheque_no.setEnabled(true);

                                dd_amt.setText("");
                                dd_no.setText("");
                                cash_amt.setText("");
                                dd_amt.setEnabled(false);
                                dd_no.setEnabled(false);
                                cash_amt.setEnabled(false);

                                ll_cash.setBackgroundColor(Color.parseColor("#BBDEFB"));
                                ll_cheque.setBackgroundColor(Color.parseColor("#FFF176"));
                                ll_dd.setBackgroundColor(Color.parseColor("#BBDEFB"));

                                ll_radio_parent.setBackgroundColor(Color.parseColor("#FFF176"));
                                restamt.setText("Tot Amt to be paid: "+String.format("%.02f", tot));
                                break;

            case R.id.rb_dd:    method="dd";
                               // Toast.makeText(this,"Changed to dd",Toast.LENGTH_SHORT).show();

                                cheque.setChecked(false);
                                cash.setChecked(false);


                                dd_amt.setEnabled(true);
                                dd_no.setEnabled(true);

                                cheque_amt.setText("");
                                cheque_no.setText("");
                                cash_amt.setText("");
                                cheque_amt.setEnabled(false);
                                cheque_no.setEnabled(false);
                                cash_amt.setEnabled(false);

                                ll_cash.setBackgroundColor(Color.parseColor("#BBDEFB"));
                                ll_cheque.setBackgroundColor(Color.parseColor("#BBDEFB"));
                                ll_dd.setBackgroundColor(Color.parseColor("#FFF176"));

                                ll_radio_parent.setBackgroundColor(Color.parseColor("#FFF176"));
                                restamt.setText("Tot Amt to be paid: "+String.format("%.02f", tot));
                                break;


            case R.id.rb_credit:  method="credit";
                // Toast.makeText(this,"Changed to cash",Toast.LENGTH_SHORT).show();
                cash.setChecked(false);
                cash_amt.setText("");
               cash_amt.setEnabled(false);


                ll_credit.setBackgroundColor(Color.parseColor("#FFF176"));
                ll_cash.setBackgroundColor(Color.parseColor("#BBDEFB"));
                ll_radio_parent.setBackgroundColor(Color.parseColor("#FFF176"));

               // restamt.setText("Tot Amt to be paid"+String.format("%.02f", tot));
              /*  if(!cash_amt.getText().toString().equals("")&&credit_amt.getText().toString().equals(""))
                  //  credit_amt.setText(String.valueOf(rest));

                 if(!cash_amt.getText().toString().equals("") &&!credit_amt.getText().toString().equals("")) {
                    if (rest!=0)
                        Toast.makeText(PlaceOrderActivity.this, "Enter correct amounts", Toast.LENGTH_SHORT).show();
                    credit_amt.setText("");

                }*/

                credit_amt.setEnabled(true);
                rcvd_amt="0.0";
              if(editFlag==false) {
                  restamt.setText("Tot Amt to be paid: " + String.format("%.02f", tot));
                  credit_amt.setText(String.valueOf(tot));
                  credit_amt.setEnabled(false);
              }if(editFlag==true)
              {
                  if(credit_amt.getText().toString().equals(""))
                  {
                      credit_amt.setText(String.valueOf(rec_rest_tot));
                      credit_amt.setEnabled(false);
                  }

              }
                break;


            /*
            *  adding suborder in object
            * */

            case R.id.fab_plus: if(clnt_name.getText().toString().equals("") ||
                                product.getSelectedItemPosition()==0 ||
                                weight.getSelectedItemPosition()==0 ||
                                 qty.getText().toString().equals("")
                    )
            {
                Log.e("Empy","empty");
                dialog_result.setText("please fill all fields! ");
                verify.setText("OK");

                popupWindow = new PopupWindow(
                        customView,
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT
                );
              //  popupWindow.setAnimationStyle(R.style.animationdialog);
               // popupWindow.setElevation(20.5f);
                //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                popupWindow.setOutsideTouchable(false);
                if(popupWindow.isShowing())
                {
                    popupWindow.dismiss();
                }
                popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER,0,0);
              //  Toast.makeText(getApplicationContext(),String.valueOf(popupWindow.getAnimationStyle()), Toast.LENGTH_SHORT).show();
            }
            else {
                putSuborder();
                cash.setEnabled(true);
                cheque.setEnabled(true);
                dd.setEnabled(true);
                credit.setEnabled(true);
                clear();
               // int pluss = Integer.parseInt(bag.getText().toString()) + 1;
             //   if(subEditFlag==false)
               // bag.setText(String.valueOf(pluss));
                // Toast.makeText(this,"fprice;"+String.valueOf(fprice),Toast.LENGTH_SHORT).show();
                Log.e("fprice", String.valueOf(fprice));
                Log.e("tot", String.valueOf(tot));

                tot = tot + fprice;
                if(editFlag==true )
                    rec_rest_tot=rec_rest_tot+fprice;
                Log.e("tot", String.valueOf(tot));
                //tot=tot+Double.parseDouble(price.getText().toString());
                total.setText("Total Amt: " + String.valueOf(tot));
            }
                                break;


            /*
            *  Removing recent order from set
            * */
            case R.id.fab_minus: if(bag.getText().toString().equals("0"))
            {
                Log.e("Empty","empty");
                dialog_result.setText("Cart is Empty");
                verify.setText("OK");

                popupWindow = new PopupWindow(
                        customView,
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT
                );
                //  popupWindow.setAnimationStyle(R.style.animationdialog);
                // popupWindow.setElevation(20.5f);
                //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                popupWindow.setFocusable(true);
                popupWindow.setTouchable(true);
                popupWindow.setOutsideTouchable(false);

                if(popupWindow.isShowing())
                {
                    popupWindow.dismiss();
                }
                popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER,0,0);
               // Toast.makeText(getApplicationContext(),String.valueOf(popupWindow.getAnimationStyle()), Toast.LENGTH_SHORT).show();
            }
            else {
                //obj.remove(bag.getText().toString());
                int minuss = Integer.parseInt(bag.getText().toString()) - 1;
                clear();
                Log.e("tot before removal", String.valueOf(tot));
                Log.e("minuss", String.valueOf(minuss));
                if (minuss >= 0) {
                    Log.e("Inside if", "minus");
                    //bag.setText(String.valueOf(minuss));
                   // tot = tot - fprice; old
                    try {

                        Log.e("price to be removed", obj.getJSONObject(bag.getText().toString()).getString("v6"));
                        if(editFlag==true)
                            rec_rest_tot=rec_rest_tot-Double.parseDouble(obj.getJSONObject(bag.getText().toString()).getString("v6"));
                        tot=tot-Double.parseDouble(obj.getJSONObject(bag.getText().toString()).getString("v6"));
                        obj.remove(bag.getText().toString());
                        bag.setText(String.valueOf(minuss));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
               // Log.e("tot after removal", String.valueOf(tot));
                //tot=tot+Double.parseDouble(price.getText().toString());
                total.setText("Total Amt: " + String.valueOf(tot));
                Log.e("Removed ", String.valueOf(minuss));
            }
                                    break;

            /*
            *  order finalization
            * */
            case R.id.btn_done:

                dialog_result2.setText("Do you really want to make this order final?");
                yes.setText("Yes");
                popupWindow2 = new PopupWindow(
                        customView2,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                //  popupWindow.setAnimationStyle(R.style.animationdialog);
                // popupWindow.setElevation(20.5f);
                //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                popupWindow2.setOutsideTouchable(true);
                popupWindow2.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);


               /* if(clnt_name.getText().toString().equals("") ||
                        product.getSelectedItemPosition()==0 ||
                        weight.getSelectedItemPosition()==0 ||
                        qty.getText().toString().equals("")
                        )
                {
                    Log.e("Empy","empty");
                    dialog_result.setText("please fill all fields! ");
                    verify.setText("OK");

                    popupWindow = new PopupWindow(
                            customView,
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT
                    );
                    //  popupWindow.setAnimationStyle(R.style.animationdialog);
                    // popupWindow.setElevation(20.5f);
                    //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                    popupWindow.setOutsideTouchable(false);
                    if(popupWindow.isShowing())
                    {
                        popupWindow.dismiss();
                    }
                    popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER,0,0);
                   // Toast.makeText(getApplicationContext(),String.valueOf(popupWindow.getAnimationStyle()), Toast.LENGTH_SHORT).show();
                }
                else {
                 //   Toast.makeText(getApplicationContext(), "Editflag" + String.valueOf(editFlag) + "subEditFlag:"
                    //        + String.valueOf(subEditFlag), Toast.LENGTH_SHORT).show();
                    if (subEditFlag == true) {
                        int plussd = Integer.parseInt(bag.getText().toString()) + 1;
                        bag.setText(String.valueOf(plussd));
                        Log.e("Editflag", "true");
                        Log.e("tot", String.valueOf(tot));
                        Log.e("tot", String.valueOf(fprice));
                        tot = tot + fprice;
                        total.setText("Tot.Amt." + tot);
                        putSuborder();
                        cash.setEnabled(true);
                        cheque.setEnabled(true);
                        dd.setEnabled(true);
                        rest = tot - Double.parseDouble(rcvd_amt);
                        Log.e("rcvd", String.valueOf(Double.parseDouble(rcvd_amt)));
                        Log.e("tot", String.valueOf(tot));
                        Log.e("rest", String.valueOf(rest));
                        if (rest < 0)
                            restamt.setText("All Paid!" + "Extra Amt: " + String.valueOf(Math.abs(rest)));
                        else if (rest == 0)
                            restamt.setText("All paid!");
                        else
                            restamt.setText(String.valueOf("Amt to be paid: " + rest));
                        //subEditFlag=false;
                    } else {

                        putSuborder();
                        cash.setEnabled(true);
                        cheque.setEnabled(true);
                        dd.setEnabled(true);
                        int plussdn = Integer.parseInt(bag.getText().toString()) + 1;
                        bag.setText(String.valueOf(plussdn));
                        Log.e("fprice", String.valueOf(fprice));
                        Log.e("tot", String.valueOf(tot));
                        tot = tot + fprice;
                        total.setText("Total Amt:" + String.valueOf(tot));
                       // Toast.makeText(this,"done"+rcvd_amt,Toast.LENGTH_SHORT).show();
                        rest = tot - Double.parseDouble(rcvd_amt);
                        Log.e("rcvd", String.valueOf(Double.parseDouble(rcvd_amt)));
                        Log.e("tot", String.valueOf(tot));
                        Log.e("rest", String.valueOf(rest));
                        if (rest < 0)
                            restamt.setText("All Paid!" + "Extra Amt: " + String.valueOf(Math.abs(rest)));
                        else if (rest == 0)
                            restamt.setText("All paid!");
                        else
                            restamt.setText(String.valueOf("Amt to be paid: " + rest));
                    }

                    clear();
                }*/
                                 break;



            case R.id.btn_del_dialog_yes:

                popupWindow2.dismiss();

                if(yes.getText().toString().equals("Add Customer"))
                {
                    Intent intent=new Intent(this, AddCustomerActivity.class);
                    startActivity(intent);
                }
                else {
                    if (clnt_name.getText().toString().equals("") ||
                            product.getSelectedItemPosition() == 0 ||
                            weight.getSelectedItemPosition() == 0 ||
                            qty.getText().toString().equals("")
                            ) {
                        Log.e("Empty", "empty");
                        dialog_result.setText("please fill all fields! ");
                        verify.setText("OK");

                        popupWindow = new PopupWindow(
                                customView,
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT
                        );
                        //  popupWindow.setAnimationStyle(R.style.animationdialog);
                        // popupWindow.setElevation(20.5f);
                        //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                        popupWindow.setOutsideTouchable(false);
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                        // Toast.makeText(getApplicationContext(),String.valueOf(popupWindow.getAnimationStyle()), Toast.LENGTH_SHORT).show();
                    }


                    else {
                        //   Toast.makeText(getApplicationContext(), "Editflag" + String.valueOf(editFlag) + "subEditFlag:"
                        //        + String.valueOf(subEditFlag), Toast.LENGTH_SHORT).show();
                        if (subEditFlag == true) {
                            int plussd = Integer.parseInt(bag.getText().toString()) + 1;
                            bag.setText("1");
                            Log.e("SubEditflag y4", "true");
                            Log.e("tot", String.valueOf(tot));
                            Log.e("fprice", String.valueOf(fprice));
                            tot = tot + fprice;
                            rec_rest_tot=rec_rest_tot+fprice;
                            total.setText("Tot.Amt." + tot);
                            putSuborder();
                            cash.setEnabled(true);
                            cheque.setEnabled(true);
                            dd.setEnabled(true);
                            credit.setEnabled(true);
                           /* if(crdt_amt.equals("0.0")) {
                                Log.e("yes4","rcvd");
                                rest = tot - Double.parseDouble(rcvd_amt);
                            }
                            else if(!crdt_amt.equals("0.0")) {
                                Log.e("yes4","crdt");
                                rest = 0.0;
                                                            }*/
                           // rest = prevblnc+(tot - Double.parseDouble(rcvd_amt));
                           // rest = tot - Double.parseDouble(rcvd_amt);

                            rest=rec_rest_tot-Double.parseDouble(rcvd_amt);
                           /* Log.e("crdt yes4", String.valueOf(Double.parseDouble(crdt_amt)));
                            Log.e("rcvd y4", String.valueOf(Double.parseDouble(rcvd_amt)));
                            Log.e("tot y4", String.valueOf(tot));
                            Log.e("rec_rest_tot y4", String.valueOf(tot));
                            Log.e("rest y4", String.valueOf(rest));*/
                            if (rest < 0) {
                                rest = tot;
                                restamt.setText("Yes Amt to be paid: " + String.format("%.02f", rest));
                                // Toast.makeText(this, "Amount exceeded!", Toast.LENGTH_SHORT).show();
                                dialog_result.setText("Amount exceeded!");
                                verify.setText("OK");
                                popupWindow = new PopupWindow(
                                        customView,
                                        LayoutParams.MATCH_PARENT,
                                        LayoutParams.MATCH_PARENT
                                );
                                //  popupWindow.setAnimationStyle(R.style.animationdialog);
                                // popupWindow.setElevation(20.5f);
                                //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                                popupWindow.setOutsideTouchable(false);
                                if (popupWindow.isShowing())
                                    popupWindow.dismiss();

                                 popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                            } else if (rest == 0)
                                restamt.setText("yes2 All paid!");
                            else
                                restamt.setText("yes2 Amt to be paid: " + String.format("%.02f", rest));
                            //subEditFlag=false;
                        } else {

                            putSuborder();
                            cash.setEnabled(true);
                            cheque.setEnabled(true);
                            dd.setEnabled(true);
                            credit.setEnabled(true);
                            int plussdn = Integer.parseInt(bag.getText().toString()) + 1;
                            Log.e("subeditflag", String.valueOf(subEditFlag));
                           // bag.setText(String.valueOf(plussdn));
                            Log.e("fprice", String.valueOf(fprice));
                            Log.e("tot", String.valueOf(tot));
                            Log.e("oldprice", String.valueOf(oldprice));
                            tot=tot-oldprice;
                            tot = tot + fprice;
                            if(editFlag==true) {
                                rec_rest_tot = rec_rest_tot + fprice;
                                rest=rec_rest_tot-Double.parseDouble(rcvd_amt);
                            }
                            else
                                rest = tot - Double.parseDouble(rcvd_amt);
                            total.setText("Total Amt: " + String.valueOf(tot));

                            /*if(crdt_amt.equals("0.0")) {
                               // rest = prevblnc+(tot - Double.parseDouble(rcvd_amt));
                                rest = tot - Double.parseDouble(rcvd_amt);
                            }
                            else if(!crdt_amt.equals("0.0"))
                            {
                                rest=0.0;
                            }*/
                            Log.e("rcvd", String.valueOf(Double.parseDouble(rcvd_amt)));
                            Log.e("tot", String.valueOf(tot));
                            Log.e("rest", String.valueOf(rest));
                            if (rest < 0) {
                                rest = rest+tot;
                                restamt.setText("Amt to be paid: " + String.format("%.02f", rest));
                                //  Toast.makeText(this, "Amount exceeded!", Toast.LENGTH_SHORT).show();
                                dialog_result.setText("Amount exceeded!");
                                verify.setText("OK");
                                popupWindow = new PopupWindow(
                                        customView,
                                        LayoutParams.MATCH_PARENT,
                                        LayoutParams.MATCH_PARENT
                                );
                                //  popupWindow.setAnimationStyle(R.style.animationdialog);
                                // popupWindow.setElevation(20.5f);
                                //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                                popupWindow.setOutsideTouchable(false);
                                if (popupWindow.isShowing()) {
                                    popupWindow.dismiss();
                                }
                                popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                            } else if (rest == 0)
                                restamt.setText("yes4 All paid!");
                            else
                                restamt.setText("yes4 Amt to be paid: " + String.format("%.02f", rest));
                        }

                        clear();


                        clnt_name.setEnabled(false);
                        product.setEnabled(false);
                        weight.setEnabled(false);
                        qty.setEnabled(false);
                        plus.setEnabled(false);
                        minus.setEnabled(false);
                        done.setEnabled(false);
                    }
                }

                break;

            case R.id.btn_del_dialog_no:
                if(newCustomer==true)
                {
                    clnt_name.setText("");
                    clear();
                }

                popupWindow2.dismiss();
                break;


                /*
                *  Submit Order
                * */

            case R.id.btn_upload:/*********************************************/
                                //suborders
                            Log.e("orderSubordermapzize", String.valueOf(suborderMap.size()));
               // Toast.makeText(getApplicationContext(),"Editflag"+String.valueOf(editFlag)+"subEditFlag:"
                    //    +String.valueOf(subEditFlag),Toast.LENGTH_SHORT).show();

                try {
                            //for(String key:suborderMap.keySet()) {


                             // JSONObject obj1 = new JSONObject(suborderMap);
                                  //  obj=new JSONObject();
                                  //  obj.put(key.toString(),suborderMap.get(key));

                               // Log.e("ordersub obj", String.valueOf(obj1));
                               // suborderArray.put(obj1);
                               // Log.e("orderSuborderarray", String.valueOf(suborderArray.length()));
                                //Log.e("Json", obj.toString());
                          //  }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                                /****************************************************/
                                //main order

                progressDialog=new ProgressDialog(this);
                progressDialog.setMessage("Please wait");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                                if(method.equals("cash"))
                                {
                                    rcvd_amt=cash_amt.getText().toString();
                                }
                                else if(method.equals("cheque")){

                                    rcvd_amt=cheque_amt.getText().toString();
                                }
                                else if(method.equals("dd")){

                                    rcvd_amt=dd_amt.getText().toString();
                                }

                                try {
                                    payObject.put("ord",ord_no.getText().toString());
                                    payObject.put("tot",String.valueOf(tot));
                                    payObject.put("method",method);
                                    payObject.put("rcvd_amt",rcvd_amt);
                                    payObject.put("chequeno",cheque_no.getText().toString());
                                    payObject.put("ddno",dd_no.getText().toString());
                                    payObject.put("credit",crdt_amt);
                                    payObject.put("cust",clnt_name.getText().toString());
                                    payObject.put("user",/*SplashActivity.*/sPrefUserInfo.getUserInfo());
                                    if(editFlag==true)
                                        payObject.put("rest",String.valueOf(rec_rest_tot-Double.parseDouble(rcvd_amt)));
                                    else
                                        payObject.put("rest",String.valueOf(tot-Double.parseDouble(rcvd_amt)));
                                    Log.e("Json",payObject.toString());

                                    Log.e("editFlag",""+editFlag);
                                    Log.e("subEditFlag","" +subEditFlag);

                                    postAllData();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                break;

            case R.id.btn_dialog:Log.e("BTN DIALOG","VERIFY");
            Log.e("Text",dialog_result.getText().toString());
                                    popupWindow.dismiss();
                                    if(dialog_result.getText().toString().equals("Order placed!")||
                                            dialog_result.getText().toString().equals("Order Not placed.\nPlease Try Again !")
                                            || dialog_result.getText().toString().equals("Order Updated!"))
                                    {
                                        Log.e("Text","yes");
                                        editFlag=subEditFlag=false;
                                        oldprice=0.0;
                                        finish();
                                    }
                                   UploadImageToServer.setEnabled(true);

                                         Log.e("Text","no");

                                    break;

            /*
            *  Capturinf image & pasting at imageview
            * */

            case R.id.btn_Capture:

               /* String s="";
                cash_amt.setFocusable(false);
                if(!cash_amt.getText().toString().equals(""))
                   s =String.format("%.02f", Float.parseFloat(cash_amt.getText().toString()));
                    cash_amt.append(s);*/
                progressDialog.show();
                if (ContextCompat.checkSelfPermission
                        (this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission
                        (this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)

                {
                    Log.e("permission","denied");
                    progressDialog.dismiss();
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    }, RequestPermissionCode);
                    captureImage();
                    // Printing toast message after enabling runtime permission.
                    //   Toast.makeText(this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

                } else {


                    captureImage();

                }
                progressDialog.dismiss();

                                    break;

           /* case R.id.btn_upload:   GetImageNameFromEditText = imageName.getText().toString();
                                    UploadImageWithVolly();
                                    break;
*/
            case R.id.btn_Choose :
                chooseImage();
                break;


        }

      /*  if(view.getId()==R.id.rb_cash)
        else if(view.getId()==R.id.rb_chq)
      else  if(view.getId()==R.id.rb_dd)
          else if(view.getId()==R.id.fab_plus){


                *//*JSONObject suborder=new JSONObject();

            try {
                suborder.put("v1",ord_no.getText().toString());
                suborder.put("v2",clnt_name.getText().toString());
                suborder.put("v3",product.getSelectedItem().toString());
                suborder.put("v4",weight.getSelectedItem().toString());
                suborder.put("v5",qty.getText().toString());
                suborder.put("v6",price.getText().toString());

                int ono= Integer.parseInt(bag.getText().toString())+1;
                suborderMap.put(String.valueOf(ono),suborder);

*//*                tot=tot+fprice;

           *//* } catch (Exception e) {
                e.printStackTrace();
            }
            ;*//*


       *//*     put json objects into one jsonarray
            try {


                for(int i = 0; i < 2; i++) {
                    // 1st object
                    JSONObject suborder = new JSONObject();
                    suborder.put("v1",ord_no.getText().toString());
                    suborder.put("v2",clnt_name.getText().toString());
                    suborder.put("v3",product.getSelectedItem().toString());
                    suborder.put("v4",weight.getSelectedItem().toString());
                    suborder.put("v5",qty.getText().toString());
                    suborder.put("v6",price.getText().toString());

                    suborderArray.put(suborder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Log.e("Json",suborderArray.get(0).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }*//*
            iplus.startAnimation(slideDownAnimation);



       */
    }

    /*
    *  function to clear product,weight,qty & price
    * */
    public void clear()
    {
        //clnt_name.setText("");
        product.setSelection(0);
        weight.setSelection(0);
        qty.setText("");
        price.setText("Price : 0.0");
      //  total.setText("0.0");
    }
    public void newOrder()
    {
       Intent intent=new Intent(this,PlaceOrderActivity.class);
        startActivity(intent);

    }

    /*
    *  function to save suborder locally
    * */
    public void putSuborder()
    {
        try {
Log.e("Inside","putsuborder");

          int  q= Integer.parseInt(qty.getText().toString());
           // Double p= Double.valueOf(price.getText().toString());
            Double p=fprice;
            int flg=0;
            String key="";


              //Log.e("Yes","yes");
              /*for (Map.Entry<String,JSONObject> entry:suborderMap.entrySet()) {

                 Log.e("mapkey",entry.getKey());
                // Log.e("mapvalue", String.valueOf(entry.getValue()));
                 if(entry.getValue().has("v3"))
                 {
                     Log.e("jsonvalue", entry.getValue().getString("v3"));
                     if(entry.getValue().getString("v3").equals(product.getSelectedItem().toString())
                             &&
                             entry.getValue().getString("v4").equals(weight.getSelectedItem().toString()))
                     {
                         q= q+Integer.parseInt(entry.getValue().getString("v5"));
                         p= p+Double.valueOf(entry.getValue().getString("v6"));
                         key =entry.getKey();
                         flg=1;
                     }
                 }
                  Log.e("mapflg",String.valueOf(flg));
                  if(flg==1) {
                      Log.e("mapvalue",String.valueOf(suborderMap.get(key)));
                      obj.remove(key);
                      Log.e("mapkeyafter",entry.getKey());
                      Log.e("mapvalue",String.valueOf(suborderMap.get(key)));
                      Log.e("mapsize",String.valueOf(suborderMap.size()));

                  }


              }*/
            Iterator<String> keys = obj.keys();
            String keyin="";
            while(keys.hasNext() ) {
                 keyin = (String)keys.next();
                if ( obj.get(keyin) instanceof JSONObject ) {
                    Log.e("putsuborder","first if");
                    JSONObject xx = new JSONObject(obj.get(keyin).toString());

                    Log.e("v3",xx.get("v3").toString());
                    Log.e("Prod",product.getSelectedItem().toString());
                    Log.e("v4",xx.get("v4").toString());

                    if(xx.getString("v3").equals(product.getSelectedItem().toString()))
                    {
                        if(xx.getString("v4").equals(weight.getSelectedItem().toString())) {
                            Log.e("putsuborder","inner if");
                            Log.e("equal", "equal");
                            equalFlag=true;
                            //  int newq= Integer.parseInt(obj.getString("v5"));
                            // Log.e("nq", obj.getString("v5"));

                            if (subEditFlag == true) {
                                q = Integer.parseInt(xx.getString("v5"));

                            } else {
                                q = q + Integer.parseInt(xx.getString("v5"));
                                p = p + Double.valueOf(xx.getString("v6"));
                            }

                            Log.e("q", String.valueOf(q));
                            Log.e("p", String.valueOf(p));
                            flg = 1;
                            Log.e("obj size", String.valueOf(obj.length()));
                            obj.remove(keyin);
                            Log.e("obj size", String.valueOf(obj.length()));
                            break;
                        }
                        else
                        {
                            Log.e("putsuborder","inner else");

                        }
                    }
                }
            }

                Log.e("Yes", "no");
                JSONObject suborder = new JSONObject();
                suborder.put("v1", ord_no.getText().toString());
                suborder.put("v2", clnt_name.getText().toString());
                suborder.put("v3", product.getSelectedItem().toString());
                suborder.put("v4", weight.getSelectedItem().toString());
                suborder.put("v5", String.valueOf(q));
                suborder.put("v6", String.valueOf(p));

                int ono=0;
                if(equalFlag==false && subEditFlag==false)
               ono = Integer.parseInt(bag.getText().toString()) + 1;
                else if(!keyin.equals(""))
                    ono= Integer.parseInt(keyin);
            Log.e("ono",""+ono);
                //Log.e("ordersuborder",String.valueOf(suborder));

                obj.put(String.valueOf(ono), suborder);

                //Log.e("orderobj",String.valueOf(obj));
                suborderMap.put(String.valueOf(ono), suborder);
                //  Log.e("orderSubordermapzize", String.valueOf(suborderMap.size()));
            Log.e("equalFlag",""+equalFlag);
            if(equalFlag==false && subEditFlag==false)
            bag.setText(String.valueOf(ono));
            else
                equalFlag=false;



        } catch (JSONException e) {

        }
    }


    /*
    *  populating spinner for product with available products
    * */
    public void populateProdSpinner()
    {
       /* Log.e("inptentprodname",intentprodname);
        Log.e("intentweightname",intentweight);*/
        if(list_prod!=null)
            list_prod.clear();


        Log.e("populateProdSpinner","called");
        String url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/fetchProdSpin.php";
       // String url = "http://www.thinkbank.co.in/Rajeshahi_app_testingfetchProdSpin.php";


        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response",String.valueOf(response));
                       // Toast.makeText(getBaseContext(),"Response:"+response,Toast.LENGTH_SHORT).show();
                        // the response is already constructed as a JSONObject!
                        try {

                               for(int i = 0; i<response.names().length(); i++){

                                String key=response.names().getString(i);
                                String value= String.valueOf(response.get(response.names().getString(i)));
                                value=value.replaceAll("[\\[,\\],\",]","");
                                list_prod.add(value);


                              //  Log.e("ResponseNameLoop", "key = " + key + " value = " + value);
                                //Log.e("ResponseListElement",list_prod.get(0));
                            }

                            Log.e("index", String.valueOf(list_prod.indexOf(intentprodname)));
                                if(rcvIntentP==true)
                                {
                                    product.setSelection(list_prod.indexOf(intentprodname)+1);
                                   // rcvIntentP=false;
                                }
                           // String element=String.valueOf( response.getJSONArray("w1"));
                           // Log.e("Response",String.valueOf( response.getJSONArray("w1")));
                          //  Toast.makeText(getBaseContext(),"Element:"+element,Toast.LENGTH_SHORT).show();

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


    /*
    * populating spinner for weight for selected product
    * */
    public void populateWtSpinner(String prod) {

        final ProgressDialog progressDialog=new ProgressDialog(PlaceOrderActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        if(list_wt!=null)
            list_wt.clear();

        Log.e("populateWtSpinner","called");
        try {
            prod= URLEncoder.encode(String.valueOf(prod), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/fetchWghtSpin.php?p_name="+prod;
        //String url = "http://www.thinkbank.co.in/Rajeshahi_app_testingfetchWghtSpin.php?p_name="+prod;
        Log.e("URL",url);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response",String.valueOf(response));
                       // Toast.makeText(getBaseContext(),"Response:"+response,Toast.LENGTH_SHORT).show();
                        // the response is already constructed as a JSONObject!
                        try {

                            for(int i = 0; i<response.names().length(); i++){

                                String key=response.names().getString(i);
                                Log.e("key",key);
                                String value= String.valueOf(response.get(response.names().getString(i)));
                                value=value.replaceAll("[\\[,\\],\",]","");
                                list_wt.add(value);
                                weight.setSelection(0);
                                //  Log.e("ResponseNameLoop", "key = " + key + " value = " + value);
                                //Log.e("ResponseListElement",list_prod.get(0));
                            }

                            if(rcvIntentW==true)
                            {
                                Log.e("indexlist",String.valueOf(list_wt.indexOf(intentweight)));
                                weight.setSelection(list_wt.indexOf(intentweight)+1);
                                rcvIntentW=false;
                                rcvIntentP=false;
                                disableAll();
                            }

                            progressDialog.dismiss();

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
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {


        iplus.setVisibility(View.INVISIBLE);
        int s=Integer.parseInt(bag.getText().toString())+1;
        bag.setText(String.valueOf(s));


    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        //Toast.makeText(this, "beforetextchanged", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

       // Toast.makeText(this, "textchanged", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void afterTextChanged(Editable editable) {


       // Toast.makeText(this, "aftertextchangedstart"+rcvd_amt, Toast.LENGTH_SHORT).show();

        /*
        *  changing price according to changing quantity
        * */

        if(qty.getText().hashCode() == editable.hashCode()) {
            String qs = qty.getText().toString();
           // Toast.makeText(this, qs, Toast.LENGTH_SHORT).show();
            if(product.getSelectedItemPosition()!=0 && weight.getSelectedItemPosition()!=0) {
                if (!qs.isEmpty()) {

                    //Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
                    int q = Integer.parseInt(qty.getText().toString());
                    // Toast.makeText(this, String.valueOf(iprice), Toast.LENGTH_SHORT).show();
                    fprice = q * iprice;
                    fprice=Double.parseDouble(String.format("%.02f", fprice));
                    price.setText("Price : " + String.format("%.02f", fprice));
                   // price.setText("Price : " + String.valueOf(fprice));
                    //   Toast.makeText(getApplicationContext(),"iprice:"+String.valueOf(iprice),Toast.LENGTH_SHORT).show();
                    //   Toast.makeText(getApplicationContext(),"fprice:"+String.valueOf(fprice),Toast.LENGTH_SHORT).show();
                } else
                    price.setText("Price : 0.0");
            }

            Log.e("SubEditFlag", String.valueOf(subEditFlag));
        }

        /*
        * changing rest amount as per changing received amount
        */

        else if(cash_amt.getText().hashCode()==editable.hashCode())
        {
            if(!cash_amt.getText().toString().equals(""))
            {
               Log.e("tot at cash",String.valueOf(tot));
                //cash_amt.setText(String.format("%.02f",Float.parseFloat(cash_amt.getText().toString())));
                String s=cash_amt.getText().toString();
               if(cash_amt.getText().toString().contains("."))
               {
                   int length=cash_amt.getText().toString().substring(cash_amt.getText().toString().indexOf(".")).length();
                   if(length>3) {
                        cash_amt.setText("");
                       cash_amt.append(s.substring(0, s.length() - 1));
                       Log.e("String length", "" + length);
                       //cash_amt.setText(String.format("%.02f",Float.parseFloat(cash_amt.getText().toString())));
                   }
               }

                rcvd_amt=String.format("%.02f",Float.parseFloat(cash_amt.getText().toString()));
                Log.e("rcvd amt in cash textc",rcvd_amt);
               /* if(!crdt_amt.equals("")) {
                    Log.e("Inside"," cash if");
                    Log.e("crdt",crdt_amt);
                    rest = tot - (Double.parseDouble(rcvd_amt) + Double.parseDouble(crdt_amt));
                }
                else {
                    Log.e("Inside","cash else");*/
               Log.e("rest in cashtext before",""+rest);
                Log.e("tot in cashtext before",""+tot);
                Log.e("prev balance",""+prevblnc);
                  //  rest = (tot+prevblnc) - Double.parseDouble(rcvd_amt);
                if(editFlag==true)
                    rest=rec_rest_tot-Double.parseDouble(rcvd_amt);
                else
                    rest = tot - Double.parseDouble(rcvd_amt);
              //  }

                Log.e("In cash",""+tot+" "+rest);
                if (rest < 0)
                {
                    cash_amt.setText("");
                  //  Toast.makeText(this, "Amount exceeded!", Toast.LENGTH_SHORT).show();
                    dialog_result.setText("Amount exceeded!");
                    verify.setText("OK");
                    popupWindow = new PopupWindow(
                            customView,
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT
                    );
                    //  popupWindow.setAnimationStyle(R.style.animationdialog);
                    // popupWindow.setElevation(20.5f);
                    //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                    popupWindow.setOutsideTouchable(false);
                    if(popupWindow.isShowing())
                    {
                        popupWindow.dismiss();
                    }
                    if(editFlag==false && subEditFlag==false)
                    popupWindow.showAtLocation(ll_radio_parent, Gravity.CENTER, 0, 0);
                }
                   // restamt.setText("All Paid!" + "Extra Amt: " + String.valueOf(Math.abs(rest)));
                else if (rest == 0)
                    restamt.setText("All paid!");
                else
                    restamt.setText("Total Amt to be paid: " + String.format("%.02f", rest));
            }
            else if(cash_amt.getText().toString().equals(""))
            {
                if(editFlag==true)
                    restamt.setText("Total Amt to be paid: " + String.format("%.02f", rec_rest_tot));
                else
                    restamt.setText("Total Amt to be paid: " + String.format("%.02f", tot));
            }

        }

        else if(credit_amt.getText().hashCode()==editable.hashCode())
        {
            if(!credit_amt.getText().toString().equals(""))
            {
                Log.e("tot at cash",String.valueOf(tot));
                //cash_amt.setText(String.format("%.02f",Float.parseFloat(cash_amt.getText().toString())));
                //setMaximumFractionDigits(2)
                String s=credit_amt.getText().toString();
                if(credit_amt.getText().toString().contains("."))
                {
                    int length=credit_amt.getText().toString().substring(credit_amt.getText().toString().indexOf(".")).length();
                    if(length>3) {
                        credit_amt.setText("");
                        credit_amt.append(s.substring(0, s.length() - 1));
                        Log.e("String length", "" + length);
                        //cash_amt.setText(String.format("%.02f",Float.parseFloat(cash_amt.getText().toString())));
                    }
                }
//                rcvd_amt=String.format("%.02f",Float.parseFloat(cash_amt.getText().toString()));
                crdt_amt=String.format("%.02f",Float.parseFloat(credit_amt.getText().toString()));
                Log.e("crdt amt",crdt_amt);
              /*  if(!cash_amt.equals(""))
                    rest=tot-(Double.parseDouble(rcvd_amt)+Double.parseDouble(crdt_amt));
                else*/
               // rest = tot - Double.parseDouble(crdt_amt);
                rest=0.0;
                Log.e("rest",""+rest);
                if (rest < 0)
                {
                    cash_amt.setText("");
                    credit_amt.setText("");
                    //  Toast.makeText(this, "Amount exceeded!", Toast.LENGTH_SHORT).show();
                    dialog_result.setText("Amount exceeded!");
                    verify.setText("OK");
                    popupWindow = new PopupWindow(
                            customView,
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT
                    );
                    //  popupWindow.setAnimationStyle(R.style.animationdialog);
                    // popupWindow.setElevation(20.5f);
                    //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                    popupWindow.setOutsideTouchable(false);
                    if(popupWindow.isShowing())
                    {
                        popupWindow.dismiss();
                    }
                    if(editFlag==false && subEditFlag==false)
                        popupWindow.showAtLocation(ll_radio_parent, Gravity.CENTER, 0, 0);
                }
                // restamt.setText("All Paid!" + "Extra Amt: " + String.valueOf(Math.abs(rest)));
                else if (rest == 0)
                    restamt.setText("All Settled!");
                else
                    restamt.setText("Total Amt to be paid: " + String.format("%.02f", rest));
            }
            else if(credit_amt.getText().toString().equals(""))
            {
               restamt.setText(String.valueOf("Total Amt to be paid: " + String.format("%.02f", tot)));
            }

        }
              /*else if(cheque_amt.getText().hashCode()==editable.hashCode()){

            if(!cheque_amt.getText().toString().equals("")) {
                rcvd_amt = String.format("%.02f", Float.parseFloat(cheque_amt.getText().toString()));
                rest = tot - Double.parseDouble(rcvd_amt);
                if (rest < 0)
                {
                    cheque_amt.setText("");
                   // Toast.makeText(this, "Amount exceeded!", Toast.LENGTH_SHORT).show();
                    dialog_result.setText("Amount exceeded!");
                    verify.setText("OK");
                    popupWindow = new PopupWindow(
                            customView,
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT
                    );
                    //  popupWindow.setAnimationStyle(R.style.animationdialog);
                    // popupWindow.setElevation(20.5f);
                    //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                    popupWindow.setOutsideTouchable(false);
                    if(popupWindow.isShowing())
                    {
                        popupWindow.dismiss();
                    }
                    popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                }
                else if (rest == 0)
                    restamt.setText("All paid!");
                else
                    restamt.setText(String.valueOf("total Amt to be paid: " + String.format("%.02f", rest)));
            }
            else if(cheque_amt.getText().toString().equals(""))
            {
                restamt.setText(String.valueOf("total Amt to be paid: " + String.format("%.02f", tot)));
            }
        }
        else if(dd_amt.getText().hashCode()==editable.hashCode()){

            if(!dd_amt.getText().toString().equals("")) {
                rcvd_amt = String.format("%.02f", Float.parseFloat(dd_amt.getText().toString()));
                rest = tot - Double.parseDouble(rcvd_amt);
                if (rest < 0)
                {
                    dd_amt.setText("");
                   // Toast.makeText(this, "Amount exceeded!", Toast.LENGTH_SHORT).show();
                    dialog_result.setText("Amount exceeded!");
                    verify.setText("OK");
                    popupWindow = new PopupWindow(
                            customView,
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT
                    );
                    //  popupWindow.setAnimationStyle(R.style.animationdialog);
                    // popupWindow.setElevation(20.5f);
                    //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                    popupWindow.setOutsideTouchable(false);
                    if(popupWindow.isShowing())
                    {
                        popupWindow.dismiss();
                    }
                    popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                }
                else if (rest == 0)
                    restamt.setText("All paid!");
                else
                    restamt.setText(String.valueOf("total Amt to be paid: " + String.format("%.02f", rest)));
            }

            else if(dd_amt.getText().toString().equals(""))
            {
                restamt.setText(String.valueOf("total Amt to be paid: " + String.format("%.02f", tot)));
            }
        }*/
        else if(total.getText().hashCode()==editable.hashCode())
        {
            Log.e("total textchanged","caled");
            Log.e("total text",total.getText().toString());
            if(total.getText().toString()!=null) {

                Log.e("History check tot", String.valueOf(tot));
                tot=Double.parseDouble(String.format("%.02f", tot));
                Log.e("Rcvd Amt total textc",rcvd_amt);
                if(editFlag==true)
                    rest=rec_rest_tot;
                else
              //  rest = prevblnc +(tot - Double.parseDouble(rcvd_amt));
                rest = tot - Double.parseDouble(rcvd_amt);
                if(credit.isChecked()) {
                    Log.e("credit is checked","true");
                    credit_amt.setEnabled(true);
                    if(editFlag==true)
                        credit_amt.setText(String.valueOf(rec_rest_tot));
                    else
                    credit_amt.setText(String.valueOf(tot));
                    credit_amt.setEnabled(false);
                    rest=0.0;
                }
               if (rest < 0)
                {
                    rest=tot;
                   // restamt.setText(String.valueOf("total Amt to be paid: " + String.format("%.02f", String.format("%.02f", rest))));
                    restamt.setText("Total Amt to be paid: " + String.format("%.02f", rest));
                   // Toast.makeText(this, "Amount exceeded!", Toast.LENGTH_SHORT).show();
                    dialog_result.setText("Amount exceeded!");
                    verify.setText("OK");
                    popupWindow = new PopupWindow(
                            customView,
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT
                    );
                    //  popupWindow.setAnimationStyle(R.style.animationdialog);
                    // popupWindow.setElevation(20.5f);
                    //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                    popupWindow.setOutsideTouchable(false);
                    if(popupWindow.isShowing())
                    {
                        popupWindow.dismiss();
                    }
                    popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                } else if (rest == 0)
                    restamt.setText("tot All paid!");
                else
                    restamt.setText(String.valueOf("Total Amt to be paid: " + String.format("%.02f", rest)));
            }

        }

     else if(bag.getText().hashCode()==editable.hashCode())
        {
            if(editFlag==true)
            {
                Log.e("Count",""+count);
                Log.e("Count of bag",""+bag.getText());
                if(bag.getText().toString().equals(String.valueOf(count))) {
                    Log.e("Count disabled minus",""+bag.getText());
                    minus.setEnabled(false);
                }
                else
                {
                    Log.e("Count disabled minus",""+bag.getText());
                    minus.setEnabled(true);
                }
            }
        }

        else if(restamt.getText().hashCode()==editable.hashCode())
        {
           /* rest=rest+prevblnc;
            if(rest)*/
        }
       /* else if(clnt_name.getText().hashCode()==editable.hashCode())
        {
            Log.e("Clnt aftertextchanged","caled");
            if(!list_clnt.contains(clnt_name.getText().toString()))
            {
                Toast.makeText(this, "Client not available", Toast.LENGTH_SHORT).show();
            }
        }*/

       // Toast.makeText(this,"aftertextchanged"+rcvd_amt,Toast.LENGTH_SHORT).show();
    }


    /*
    *  function to fetch price of selected product for selected weight
    * */
    public void getProdPrice(String prod, String wt) {
        iprice=0.0;
       // Log.e("getProdPrice","called");
        //Log.e("Prod",prod);
        //Log.e("Wt",wt);
        final ProgressDialog progressDialog=new ProgressDialog(PlaceOrderActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        try {
            prod= URLEncoder.encode(String.valueOf(prod), "UTF-8");
            wt=URLEncoder.encode(String.valueOf(wt),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/fetchProdPrice.php?p_name="+prod+"&&p_weight="+wt;
      //  String url = "http://www.thinkbank.co.in/Rajeshahi_app_testingfetchProdPrice.php?p_name="+prod+"&&p_weight="+wt;
       // Log.e("URL",url);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Log.e("ResponsePrice", response.toString());
                try {

                    iprice= Double.parseDouble(response);
                    iprice=Double.parseDouble(String.format("%.02f", iprice));
                    Log.e("iprice precision", String.valueOf(iprice));
                    progressDialog.dismiss();
                    //Toast.makeText(getApplicationContext(), "iprice:"+String.valueOf(iprice), Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {

                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                Log.e("ResponseError", error.toString());

            }
        }){

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    /*
    * submit whole order(main order + sub orders)
    * */
    public void postAllData() {
        Log.e("postAllData", "called");

        UploadImageToServer.setEnabled(false);

        boolean methodblank=false,invalid=false;
        if(method.equals("cash"))
        {
            if(cash_amt.getText().toString().equals(""))
                methodblank=true;
        }
        if(method.equals("credit"))
        {
            if(credit_amt.getText().toString().equals(""))
                methodblank=true;
        }
        else if(method.equals("cheque"))
        {
            if ( cheque_amt.getText().toString().equals("") || cheque_no.getText().toString().equals(""))
                methodblank=true;
            else if(cheque_no.getText().length()<6)
            {
                Toast.makeText(this, "Invalid cheque no", Toast.LENGTH_SHORT).show();
                invalid=true;
            }
        }
        else if(method.equals("dd"))
        {
            if ( dd_amt.getText().toString().equals("") || dd_no.getText().toString().equals("") )
                methodblank=true;
            else if(dd_no.getText().length()<6)
            {
                Toast.makeText(this, "Invalid dd no", Toast.LENGTH_SHORT).show();
                invalid=true;
            }
        }
        Log.e("method",method);
        Log.e("result", String.valueOf(cheque_amt.getText().toString().equals("") && cheque_no.getText().toString().equals("")));
Log.e("methodblank", String.valueOf(methodblank));
Log.e("editflag", String.valueOf(editFlag));
Log.e("subeditflag", String.valueOf(subEditFlag));


        if (bag.getText().toString().equals("0")||
                (cash.isChecked() == false && cheque.isChecked() == false && dd.isChecked() == false &&credit.isChecked()==false) ||
                                      methodblank==true ||
                (editFlag==false && subEditFlag==false && bitmap==null)
                ){

            Log.e("Empy", "empty");

            if(progressDialog!=null)
                progressDialog.dismiss();

        dialog_result.setText("please fill all fields! ");
        verify.setText("OK");

        popupWindow = new PopupWindow(
                customView,
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        //  popupWindow.setAnimationStyle(R.style.animationdialog);
        // popupWindow.setElevation(20.5f);
        //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
        popupWindow.setOutsideTouchable(false);
            if(popupWindow.isShowing())
            {
                popupWindow.dismiss();
            }
        popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
    }
    else if(invalid==true){

        }
    else
        {
           /*progressDialog=new ProgressDialog(this);
          progressDialog.setMessage("Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
          progressDialog.show();*/
            ByteArrayOutputStream byteArrayOutputStreamObject ;
            byteArrayOutputStreamObject = new ByteArrayOutputStream();
            if(bitmap!=null)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
            byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
            final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
            String url;
           // Toast.makeText(getApplicationContext(), "Editflag " + String.valueOf(editFlag) + " subEditFlag: "
            //        + String.valueOf(subEditFlag), Toast.LENGTH_SHORT).show();
            if (editFlag == true)
                url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/updateAllData.php";
           // url = "http://www.thinkbank.co.in/Rajeshahi_app_testingupdateAllData.php";
            else
                url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/postAllData.php";                
              //  url = "http://www.thinkbank.co.in/Rajeshahi_app_testingpostAllData.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("Response", String.valueOf(response));

                            progressDialog.dismiss();
                            UploadImageToServer.setEnabled(true);
                            if (editFlag==true || subEditFlag==true)
                                dialog_result.setText("Order Updated!");
                            else
                            dialog_result.setText("Order placed!");
                            editFlag = false;
                            verify.setText("OK");

                            popupWindow = new PopupWindow(
                                    customView,
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.MATCH_PARENT
                            );
                            //  popupWindow.setAnimationStyle(R.style.animationdialog);
                            // popupWindow.setElevation(20.5f);
                            //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                            popupWindow.setOutsideTouchable(false);
                            popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                            Log.e("@@", "@@");
                            Log.e("@@ File : ", ""+file);
                            if(bitmap!=null)
                                Log.e("@@ bool", ""+file.delete());
                            bitmap=null;
                            //finish();
                            // if(getCurrentFocus().getParent()!=null)
                            //    ((ViewGroup)getCurrentFocus().getParent()).removeView(getCurrentFocus());

                            /*if(popupWindow.isShowing())
                            {
                                popupWindow.dismiss();
                            }
                            popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                            clear();
                            if(!popupWindow.isShowing()) {
                               // newOrder();
                            }*/


                           // revokeUriPermissionfileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("ErrorResponse", String.valueOf(error));

                            progressDialog.dismiss();
                            UploadImageToServer.setEnabled(true);
                            dialog_result.setText("Order Not placed.\nPlease Try Again !");
                            verify.setText("OK");

                            popupWindow = new PopupWindow(
                                    customView,
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.MATCH_PARENT
                            );
                            //  popupWindow.setAnimationStyle(R.style.animationdialog);
                            // popupWindow.setElevation(20.5f);
                            //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                            popupWindow.setOutsideTouchable(false);
                            popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("suborders", String.valueOf(obj));
                    params.put("order_main", String.valueOf(payObject));
                    params.put(ImagePathFieldOnServer, ConvertImage);
                    if(subEditFlag==true)
                    params.put("flag","sub");
                    else if(editFlag==true)
                        params.put("flag","main");

                    Log.e("ordersub", String.valueOf(obj));
                    Log.e("ordermain", String.valueOf(payObject));
                    Log.e("imagepath",ConvertImage);
                    Log.e("ordflag"," "+editFlag+" "+subEditFlag);
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

        }

    }

    private void UploadImageWithVolly() {

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/capture_img_upload_to_server.php";
       // String url = "http://www.thinkbank.co.in/Rajeshahi_app_testingcapture_img_upload_to_server.php";

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Response", String.valueOf(response));
                        progressDialog.dismiss();

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

                params.put(ImageNameFieldOnServer, GetImageNameFromEditText);
                params.put(ImagePathFieldOnServer, ConvertImage);


                Log.e("ordersub", String.valueOf(GetImageNameFromEditText));
                Log.e("ordermain", String.valueOf(ConvertImage));
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);



    }

    /*
    *  take control to image capture (camera) activity
    * */
    private void captureImage() {

        try {
             file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            mCameraFileUri = FileProvider.getUriForFile(PlaceOrderActivity.this,
                    PlaceOrderActivity.this.getApplicationContext().getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraFileUri);
            intent.putExtra("camera", mCameraFileUri);
            List<ResolveInfo> resolvedIntentActivities = getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;

                getApplicationContext().grantUriPermission(packageName, mCameraFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            /*intent.addFlags(intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(intent.FLAG_GRANT_READ_URI_PERMISSION);*/
            startActivityForResult(intent, CAMERA_CAPTURE_CODE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void chooseImage()
    {
       // Intent intent = new Intent();
       // intent.setType("image/*");
      //  intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),200);
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                  //  Toast.makeText(this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                  //  Toast.makeText(this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;

            case 101:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

               // Toast.makeText(this,"Permission Granted, Now your application can access Gallery.", Toast.LENGTH_LONG).show();

            } else {

               // Toast.makeText(this,"Permission Canceled, Now your application cannot access Gallery.", Toast.LENGTH_LONG).show();

            }
                break;
                default:
                    super.onRequestPermissionsResult(RC, per, PResult);
        }
    }

    /*
    *  filename format
    * */
    private String getFileName(){
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    /*
    *  get image file
    * */
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(getCacheDir(), "cache_images");
        if(!mediaStorageDir.exists()){
            mediaStorageDir.mkdir();
        }

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + getFileName() + ".png");
        } else if (type == MEDIA_TYPE_VIDEO) {
            return null;
        } else {
            return null;
        }
        return mediaFile;
    }

    /*
    *  return from camera activity
    * */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == CAMERA_CAPTURE_CODE){
                try {

                    if(mCameraFileUri==null) {
                       /* dialog_result.setText("please free up some space! ");
                        verify.setText("OK");

                        popupWindow = new PopupWindow(
                                customView,
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT
                        );
                        //  popupWindow.setAnimationStyle(R.style.animationdialog);
                        // popupWindow.setElevation(20.5f);
                        //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                        popupWindow.setOutsideTouchable(false);
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);*/
                       Toast.makeText(this,"Please free up space to use Camera",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCameraFileUri);
                        if (bitmap != null) {
                            ImageViewHolder.setImageBitmap(bitmap);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                            Log.e("Image size before", String.valueOf(bitmap.getAllocationByteCount()));


                          file=new Compressor(this)
                                    .setMaxWidth(100)
                                    .setMaxHeight(200)
                                    .setQuality(80)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .compressToFile(file);

                          //  bitmap= new Compressor(this).compressToBitmap(file);

                            mCameraFileUri = FileProvider.getUriForFile(PlaceOrderActivity.this,
                                    PlaceOrderActivity.this.getApplicationContext().getPackageName() + ".provider", file);
                            Log.e("mCameraFileURI", String.valueOf(mCameraFileUri));
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCameraFileUri);
                            ImageViewHolder.setImageBitmap(bitmap);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                                Log.e("Image size after", String.valueOf(bitmap.getAllocationByteCount()));


                            /*int nh = (int) ( bitmap.getHeight() * (64.0 / bitmap.getWidth()) );
                            bitmap=bitmap.createScaledBitmap(bitmap,64,nh,true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                                Log.e("Image size before", String.valueOf(bitmap.getAllocationByteCount()));

                            ImageViewHolder.setImageBitmap(bitmap);*/

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode==200)
            {

                Uri selectedImage = data.getData();
                File imageFile = new File(getRealPathFromURI(selectedImage));


                if(imageFile==null)
                    Log.e("imagefile","null");
                else
                    Log.e("imagefile", String.valueOf(imageFile));
                Log.e("image before", String.valueOf(imageFile.length()));


                bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ImageViewHolder.setImageBitmap(bitmap);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        Log.e("Image size before", String.valueOf(bitmap.getAllocationByteCount()));

                    imageFile=new Compressor(this)
                            .setMaxWidth(100)
                            .setMaxHeight(200)
                            .setQuality(80)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .compressToFile(imageFile);

                    Log.e("image after", String.valueOf(imageFile.length()));
                    mCameraFileUri = FileProvider.getUriForFile(PlaceOrderActivity.this,PlaceOrderActivity.this.getApplicationContext().getPackageName() + ".provider",imageFile);
                    Log.e("mCameraFileURI", String.valueOf(mCameraFileUri));
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCameraFileUri);
                 /*  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new OutputStream() {
                       @Override
                       public void write(int i) throws IOException {

                       }
                   });*/
                    ImageViewHolder.setImageBitmap(bitmap);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        Log.e("Image size after", String.valueOf(bitmap.getAllocationByteCount()));

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {

        String thePath = "no-path-found";
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                thePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return  thePath;
    }

    // Requesting runtime permissions.
    public void EnableRuntimePermissionToAccessCamera(){

        progressDialog.show();
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) &&
        (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) &&
        (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) &&
        (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED))
        {

            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

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

    public void EnableRuntimePermissionToAccessStorage(){

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String [] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }

        /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 101);

        }*/
    }

    public void EnableRuntimePermissionToAccessLocation(){}

    /*
    * Function to fetch all clients from db
    * */
    public void populateClntList()
    {
        progressDialog=new ProgressDialog(PlaceOrderActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        //progressDialog.show();

        if(list_clnt!=null)
            list_clnt.clear();


        Log.e("populateProdSpinner","called");
        String url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/fetchClientList.php";


        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response",String.valueOf(response));
                        // Toast.makeText(getBaseContext(),"Response:"+response,Toast.LENGTH_SHORT).show();
                        // the response is already constructed as a JSONObject!
                        try {

                            for(int i = 0; i<response.names().length(); i++){

                                String key=response.names().getString(i);
                                String value= String.valueOf(response.get(response.names().getString(i)));
                                value=value.replaceAll("[\\[,\\],\",]","");
                                Log.e("Value",value);
                                list_clnt.add(value);



                                //  Log.e("ResponseNameLoop", "key = " + key + " value = " + value);
                                //Log.e("ResponseListElement",list_prod.get(0));
                            }

                            clients = list_clnt.toArray(new String[0]);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                    (PlaceOrderActivity.this,android.R.layout.simple_list_item_1,clients);
                            clnt_name.setAdapter(adapter);

                            progressDialog.dismiss();

                             String element=String.valueOf( response.getJSONArray("w1"));
                            // Log.e("Response",String.valueOf( response.getJSONArray("w1")));
                            //  Toast.makeText(getBaseContext(),"Element:"+element,Toast.LENGTH_SHORT).show();

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

    /*
    *  function to generate random order id
    * */
    public static String random() {
        /*Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();*/
        char[] chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray();
        StringBuilder sb1 = new StringBuilder();
        Random random1 = new Random();
        for (int i = 0; i < 6; i++)
        {
            char c1 = chars1[random1.nextInt(chars1.length)];
            sb1.append(c1);
        }
        //String random_string = sb1.toString();
        return sb1.toString();
    }

    /*
    * function to enable clnt_name,product,plus-minus buttons,cash-cheque-dd buttons
    * */
    public void enableAll()
    {
        clnt_name.setEnabled(true);
        plus.setEnabled(true);
        minus.setEnabled(true);
        cash.setEnabled(true);
        credit.setEnabled(true);
        cheque.setEnabled(true);
        dd.setEnabled(true);
        product.setEnabled(true);
        tot=0.0;
    }

    /*
    * function to disable fields
    * */
    public void disableAll()
    {
        clnt_name.setEnabled(false);
        plus.setEnabled(false);
        minus.setEnabled(false);
        cash.setEnabled(false);
        credit.setEnabled(false);
        cheque.setEnabled(false);
        dd.setEnabled(false);
        product.setEnabled(false);
        weight.setEnabled(false);

    }



    @Override
    public void onFocusChange(View view, boolean b) {
/*
*  check if client is present or not
* */

        if(view.getId()==R.id.et_clnt_name ) {
           /* if(!clnt_name.getText().toString().equals(""))
                checkPrevBal(clnt_name.getText().toString());
*/
            if (editFlag == false) {
                if (!clnt_name.getText().toString().equals("")) {
                    if (!list_clnt.contains(clnt_name.getText().toString())) {
                        dialog_result2.setText("Customer not present!");
                        yes.setText("Add Customer");
                        no.setText("Cancel");
                        popupWindow2 = new PopupWindow(
                                customView2,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        );

                        popupWindow2.setOutsideTouchable(true);
                        popupWindow2.showAtLocation(total, Gravity.CENTER, 0, 0);

                        newCustomer = true;

                    }
                }
            }
        }
         //  Toast.makeText(this,"changed",Toast.LENGTH_SHORT).show();
        Log.e("SubEditFlag", String.valueOf(subEditFlag));


   /*     if(view.getId()==R.id.et_cash_rcvd_amt)
        {
            Log.e("Focus lost","cash rcvd amt");
            if(!cash_amt.getText().toString().equals(""))
            cash_amt.setText(String.format("%.02f", cash_amt.getText().toString()));
        }*/

    }

    public void checkPrevBal(String clnt){

        Log.e("Inside","checkPrevBal");
        final ProgressDialog progressDialog=new ProgressDialog(PlaceOrderActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        String url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/fetchPrevBal.php?c_id="+clnt;

         Log.e("URL",url);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Log.e("ResponsePrevBal", response.toString());
                rest=rest-prevblnc;
                prevblnc= Double.parseDouble(response);
                Log.e("rest in prevbal",""+rest);
                rest=rest+prevblnc;
                restamt.setText("Amt to be paid: "+rest);
                prevbal.setText("Previous balance: "+response);
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                Log.e("ResponseError", error.toString());

            }
        }){

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
}
