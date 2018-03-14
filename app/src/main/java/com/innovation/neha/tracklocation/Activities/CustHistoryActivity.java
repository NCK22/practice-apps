package com.innovation.neha.tracklocation.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.innovation.neha.tracklocation.Adapters.RecyclerViewAdapter;
import com.innovation.neha.tracklocation.Adapters.RecyclerViewCustAdapter;
import com.innovation.neha.tracklocation.AppController;
import com.innovation.neha.tracklocation.Pojos.Customer;
import com.innovation.neha.tracklocation.Pojos.Order;
import com.innovation.neha.tracklocation.R;
import com.innovation.neha.tracklocation.Storage.SPrefUserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CustHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    List<Customer> cust_hist_list=new ArrayList<Customer>();
    public static String u_id;
    ProgressDialog progressDialog;
    PopupWindow popupWindow;
    private TextView dialog_result;
    View customView;
    Button verify;
    String tag_string_req="jsonobject_req";
    private RecyclerView recyclerView;
    RecyclerViewCustAdapter adapter;
    private SPrefUserInfo sPrefUserInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_history);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        customView = inflater.inflate(R.layout.activity_dialog, null);
        dialog_result = (TextView) customView.findViewById(R.id.tv_dialog);
        verify = (Button) customView.findViewById(R.id.btn_dialog);
        verify.setOnClickListener(this);


        recyclerView = (RecyclerView) findViewById(R.id.rv_cust_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sPrefUserInfo=new SPrefUserInfo(CustHistoryActivity.this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        *  populate history customers added by logged in user
        * */
        populateHistory();
    }

    /*
    *  setup main adapter
    * */
    public void setupAdapter()
    {
        adapter = new RecyclerViewCustAdapter(getApplicationContext(), cust_hist_list);
        recyclerView.setAdapter(adapter);

        progressDialog.dismiss();

        adapter.setClickListener(new RecyclerViewCustAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                if(view.getId()==R.id.btn_ch_edit)
                {
                    Log.e("onItemClick", "Edit");

                    editHistory(position);
                }
            }

            @Override
            public void onClick(View view) {

            }
        });
    }

    public void populateHistory()
    {

        try {
            u_id= URLEncoder.encode(String.valueOf(/*SplashActivity.*/sPrefUserInfo.getUserInfo()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if(cust_hist_list!=null)
            cust_hist_list.clear();

        Log.e("populateWtSpinner","called");
        String url = "http://www.thinkbank.co.in/Rajeshahi_app_testing/getCustHistory.php?u_id="+u_id;
        Log.e("URL",url);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response",String.valueOf(response));
                        //  Toast.makeText(getBaseContext(),"Response:"+response,Toast.LENGTH_SHORT).show();
                        // the response is already constructed as a JSONObject!
                        try {

                            for(int i = 0; i<response.names().length(); i++){

                                JSONArray a= (JSONArray) response.get(response.names().getString(i));
                                Customer objCust=new Customer(a.getString(0),a.getString(1),
                                        a.getString(2),a.getString(3),a.getString(4),
                                        a.getString(5),a.getString(6),a.getString(7),a.getString(8));
                                Log.e("ResponseObject",String.valueOf(objCust));

                                cust_hist_list.add(objCust);
                                Log.e("ResponseSize", String.valueOf(cust_hist_list.size()));
                            }


                            /*Log.e("ResponseSize", String.valueOf(ord_hist_list.size()));
                            adapter = new RecyclerViewAdapter(getApplicationContext(), ord_hist_list);
                            recyclerView.setAdapter(adapter);
*/
                            setupAdapter();



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
                        progressDialog.dismiss();
                        dialog_result.setText("No Records Found");
                        verify.setText("OK");
                        popupWindow = new PopupWindow(
                                customView,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        );

                        popupWindow.setOutsideTouchable(true);
                        if(popupWindow.isShowing())
                            popupWindow.dismiss();
                        popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);


                    }
                });

        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);



    }

    @Override
    public void onClick(View view) {


        popupWindow.dismiss();
        finish();
    }

/*
*  function to edit history of selected customer
* */
    public void editHistory(int position)
    {

        Intent intent=new Intent(this,AddCustomerActivity.class);

                            intent.putExtra("id",cust_hist_list.get(position).getCustId());
                            intent.putExtra("name",cust_hist_list.get(position).getCustName());
                            intent.putExtra("addr",cust_hist_list.get(position).getCustAddr());
                            intent.putExtra("cnt",cust_hist_list.get(position).getCustCont());
                            intent.putExtra("gst",cust_hist_list.get(position).getCustGst());
                            intent.putExtra("pan",cust_hist_list.get(position).getCustPan());
                            intent.putExtra("lat",cust_hist_list.get(position).getCustLat());
                            intent.putExtra("lng",cust_hist_list.get(position).getCustLng());

                           Log.e("Date",cust_hist_list.get(position).getCustDate());

                            startActivity(intent);

    }
}
