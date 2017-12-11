package com.innovation.neha.tracklocation;

import android.graphics.Color;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener, Animation.AnimationListener {

    Spinner product,weight;
    List<String> list_prod=new ArrayList<String>();
    List<String>list_wt=new ArrayList<String>();

    EditText cash_amt,cheque_amt,dd_amt,cheque_no,dd_no;
    RadioButton cash,cheque,dd;
    LinearLayout ll_cash,ll_cheque,ll_dd,ll_radio_parent;
    FloatingActionButton plus,minus;
    ImageView iplus;
    TextView bag;

    Animation slideDownAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);


            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        ll_cash=(LinearLayout)findViewById(R.id.ll_cash);
        ll_cheque=(LinearLayout)findViewById(R.id.ll_cheque);
        ll_dd=(LinearLayout)findViewById(R.id.ll_dd);
        ll_radio_parent=(LinearLayout)findViewById(R.id.ll_radio_parent);

        cash_amt=(EditText)findViewById(R.id.et_cash_rcvd_amt);
        cheque_amt=(EditText)findViewById(R.id.et_chq_rcvd_amt);
        dd_amt=(EditText)findViewById(R.id.et_dd_rcvd_amt);
        cheque_no=(EditText)findViewById(R.id.et_chq_no);
        dd_no=(EditText)findViewById(R.id.et_dd_no);

        cash=(RadioButton)findViewById(R.id.rb_cash);
        cheque=(RadioButton)findViewById(R.id.rb_chq);
        dd=(RadioButton)findViewById(R.id.rb_dd);

        product=(Spinner)findViewById(R.id.sp_prod);
        weight=(Spinner)findViewById(R.id.sp_weight);

        bag=(TextView)findViewById(R.id.tv_bag);

        plus=(FloatingActionButton)findViewById(R.id.fab_plus);
        minus=(FloatingActionButton)findViewById(R.id.fab_minus);

        iplus=(ImageView)findViewById(R.id.img_overplus);

        plus.setOnClickListener(this);
        minus.setOnClickListener(this);

        cash.setOnCheckedChangeListener(this);
        cheque.setOnCheckedChangeListener(this);
        dd.setOnCheckedChangeListener(this);

        product.setOnItemSelectedListener(this);
        weight.setOnItemSelectedListener(this);

        //list_prod.add("Product");
        list_prod.add("Pavbhaji Masala");
        list_prod.add("Sambar masala");

       // list_wt.add("Weight");
        list_wt.add("10 gms");
        list_wt.add("20 gms");
        list_wt.add("100 gms");
        list_wt.add("250 gms");
        list_wt.add("500 gms");
        list_wt.add("1 Kg");
        list_wt.add("5 Kg");

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aaprod = new ArrayAdapter(this,android.R.layout.simple_spinner_item,list_prod);
        aaprod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        product.setAdapter(aaprod);

        ArrayAdapter aaweight=new ArrayAdapter(this,android.R.layout.simple_spinner_item,list_wt);
        aaweight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weight.setAdapter(aaweight);

        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slidedown);
        slideDownAnimation.setAnimationListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if(compoundButton.getId()==R.id.rb_cash)
        {
            cheque.setChecked(false);
            dd.setChecked(false);
            cash.setChecked(true);


            cash_amt.setEnabled(true);

            ll_cash.setBackgroundColor(Color.parseColor("#FFF176"));
            ll_cheque.setBackgroundColor(Color.parseColor("#BBDEFB"));
            ll_dd.setBackgroundColor(Color.parseColor("#BBDEFB"));

            ll_radio_parent.setBackgroundColor(Color.parseColor("#FFF176"));


        }

        else if(compoundButton.getId()==R.id.rb_chq)
        {
            cash.setChecked(false);
            dd.setChecked(false);
            cheque.setChecked(true);


            cheque_amt.setEnabled(true);
            cheque_no.setEnabled(true);

            ll_cash.setBackgroundColor(Color.parseColor("#BBDEFB"));
            ll_cheque.setBackgroundColor(Color.parseColor("#FFF176"));
            ll_dd.setBackgroundColor(Color.parseColor("#BBDEFB"));

            ll_radio_parent.setBackgroundColor(Color.parseColor("#FFF176"));

        }
        else if(compoundButton.getId()==R.id.rb_dd)
        {
            cheque.setChecked(false);
            cash.setChecked(false);
            dd.setChecked(true);


            dd_amt.setEnabled(true);
            dd_no.setEnabled(true);

            ll_cash.setBackgroundColor(Color.parseColor("#BBDEFB"));
            ll_cheque.setBackgroundColor(Color.parseColor("#BBDEFB"));
            ll_dd.setBackgroundColor(Color.parseColor("#FFF176"));

            ll_radio_parent.setBackgroundColor(Color.parseColor("#FFF176"));
        }
    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.fab_plus){

           /* float bottomOfScreen = getResources().getDisplayMetrics()
                    .heightPixels - (iplus.getHeight() * 6);
            //bottomOfScreen is where you want to animate to

            iplus.animate()
                    .translationY(bottomOfScreen)
                    .setInterpolator(new AccelerateInterpolator())
                    .setInterpolator(new BounceInterpolator())
                    .setDuration(2500);

            */

           iplus.startAnimation(slideDownAnimation);



        }
        else if(view.getId()==R.id.fab_minus){

        }
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
}
